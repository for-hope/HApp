package com.hdarha.happ.activities

import android.app.Dialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.analytics.FirebaseAnalytics
import com.hdarha.happ.R
import kotlinx.android.synthetic.main.activity_video_player.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


class VideoPlayerActivity : AppCompatActivity() {
    private var videoPath: String = ""
    var downloadID: Long = 0L
    var isDownloaded = false
    var isDownloading = false
    private lateinit var videoName: String
    private var share = false
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var downloadManager: DownloadManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        setSupportActionBar(toolbar_va)
        supportActionBar?.title = "Results"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home_black_24dp)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTitle)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        videoPath = intent.getStringExtra("url")!!

        videoName = "HApp_Video_" + System.currentTimeMillis() + ".mp4"

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        var videoUri = Uri.parse(videoPath)



        if (!videoPath.startsWith("http")) {
            isDownloaded = true
            videoUri =
                FileProvider.getUriForFile(this, "com.hdarha.app.fileprovider", File(videoPath))
        }
        try {
            progressLayout.visibility = View.VISIBLE
            videoView.setVideoURI(videoUri)
            videoView.requestFocus()
            videoView.setOnErrorListener { mediaPlayer, i, i2 ->
                Log.d("VideoPlayerActivity", "OnCreate : $i and $i2")
                true
            }
            val mediaController = MediaController(this)
            videoView.setMediaController(mediaController)
            mediaController.setAnchorView(videoView)
            videoView.setOnPreparedListener {
                Log.d("Video", "START")
                videoView.visibility = View.VISIBLE
                progressLayout.visibility = View.GONE
                videoView.start()
            }
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
            e.printStackTrace()
            Toast.makeText(this, "Video playback error...", Toast.LENGTH_SHORT).show()
        }





        shareBtn.setOnClickListener {
            if (isDownloaded) {
                shareVideo()
            } else {
                Toast.makeText(this, "Downloading video...", Toast.LENGTH_SHORT).show()
                if (!isDownloading) {
                    downloadVideo(videoName)
                    share = true
                }

            }

        }
        //registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }


    override fun onBackPressed() {
        if (!isDownloaded) {
            showConfirmationDialog()
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onDownloadComplete)
    }

    private fun shareVideo() {
        val videoUri =
            FileProvider.getUriForFile(this, "com.hdarha.app.fileprovider", File(videoPath))
        val shareIntent = ShareCompat.IntentBuilder.from(this)
            .setStream(videoUri)
            .setType("video/mp4")
            .setText(videoName)
            .setSubject("Check out this video i made with HApp!")
            .setChooserTitle("Share Video!")
            .createChooserIntent()
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val resInfoList: List<ResolveInfo> = this.packageManager
            .queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName: String = resolveInfo.activityInfo.packageName
            this.grantUriPermission(
                packageName,
                videoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        startActivity(shareIntent)

    }

    private fun makeDialog(): Dialog {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_share_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        val window: Window = dialog.window!!
        val wlp = window.attributes
        wlp.gravity = Gravity.BOTTOM
        wlp.windowAnimations = R.style.Animation_Design_BottomSheetDialog
        wlp.dimAmount = 0.8f
        //wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        window.attributes = wlp
        return dialog
    }

    private fun showDialog(dialog: Dialog, imgPath: String) {
        //dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)


        val imgView = dialog.findViewById<ImageView>(R.id.shareImageView)
        val mMMR = MediaMetadataRetriever()
        Log.d("PATH", imgPath)
        mMMR.setDataSource(
            imgPath,
            HashMap<String, String>()
        )
        val bmp = mMMR.frameAtTime
        imgView.setImageBitmap(bmp)
        dialog.show()
        mMMR.release()
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        unregisterReceiver(onDownloadComplete);
//    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!isDownloaded) {
            menuInflater.inflate(R.menu.video_main_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id: Int = item.itemId
//                    "Video saved to //" + Environment.DIRECTORY_MOVIES + File.separator + videoName,
        if (id == R.id.action_save_video) {
            if (videoPath != "" && !isDownloaded) {
                if (!isDownloading) {
                    downloadVideo(videoName)
                    Toast.makeText(
                        this,
                        "Downloading...",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(this, "Video is downloading.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Video already downloaded.", Toast.LENGTH_SHORT).show()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showConfirmationDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        builder.setTitle("Discard")
        builder.setMessage("Are you sure you want to exit without saving?")

        builder.setPositiveButton("Discard") { _, _ -> // Do nothing but close the dialog
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        builder.setNegativeButton("NO") { dialog, _ -> // Do nothing
            dialog.dismiss()
        }

        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun downloadVideo(videoName: String) {
        isDownloading = true

        GlobalScope.launch {
            val policy =
                StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            try {
                val uri: Uri = Uri.parse(videoPath)
                val request = DownloadManager.Request(uri)
                request.setTitle(videoName)
                request.setDescription("Downloading Video...")
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_MOVIES,
                    "HApp" + File.separator + videoName
                )

                downloadID = downloadManager.enqueue(request)
            } catch (e: Exception) {
                Log.e("downloadVideo", e.message.toString())
            }

        }

    }



    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            //Fetching the download id received with the broadcast
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val query = DownloadManager.Query()
            query.setFilterById(id)
            val downloadManager =
                this@VideoPlayerActivity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val cursor: Cursor = downloadManager.query(query)
            cursor.moveToFirst()
            val statusIndex: Int = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val status: Int = cursor.getInt(statusIndex)

            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                Log.d("Status", "Successful")
                this@VideoPlayerActivity.supportActionBar?.title = "Download status successful"
                Toast.makeText(this@VideoPlayerActivity, "Download successful", Toast.LENGTH_SHORT)
                    .show()
            } else if (status == DownloadManager.STATUS_FAILED) {
                Log.e("Status", "Download failed")
                val reasonIndex: Int = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                val reason: Int = cursor.getInt(reasonIndex)
                Log.e("STATUS", reason.toString() + "")
                this@VideoPlayerActivity.supportActionBar?.title = "F: $reason"
                Toast.makeText(
                    this@VideoPlayerActivity,
                    "Download failed reason : $reason",
                    Toast.LENGTH_LONG
                ).show()
            }


            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                isDownloading = false
                val action = intent.action
                if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    isDownloaded = true
                    val oldVideoPath = videoPath
                    @Suppress("DEPRECATION")
                    videoPath = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                        videoName
                    ).path
                    if (!share) {
                        showDialog(makeDialog(), oldVideoPath)
                    } else {
                        shareVideo()
                    }

                    Toast.makeText(
                        this@VideoPlayerActivity,
                        "Download Complete",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                } else {
                    Toast.makeText(
                        this@VideoPlayerActivity,
                        "Download couldn't be complete.",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }
        }
        private fun checkDownloadStatus() {
            val query = DownloadManager.Query()
            val id: Long = preferenceManager.getLong(strPref_Download_ID, 0)
            query.setFilterById(id)
            val cursor: Cursor = downloadManager.query(query)
        }

    }

}