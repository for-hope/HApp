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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.firebase.analytics.FirebaseAnalytics
import com.hdarha.happ.R
import com.hdarha.happ.other.TAG
import kotlinx.android.synthetic.main.activity_video_player.*
import java.io.File


class VideoPlayerActivity : AppCompatActivity() {
    private var videoPath: String = ""
    private var downloadID: Long = 0L
    private var isDownloaded = false
    private var isDownloading = false
    private lateinit var videoName: String
    private var share = false
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var player: SimpleExoPlayer
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




        player = SimpleExoPlayer.Builder(this).build()
        exoPlayerView.player = player
        // Produces DataSource instances through which media data is loaded.

        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory = DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, "HApp")
        )

        // This is the MediaSource representing the media to be played.
        val videoSource: MediaSource =
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri)

        // Prepare the player with the source.
        player.prepare(videoSource)
        player.playWhenReady

        progressLayout.visibility = View.GONE


        shareBtn.setOnClickListener {
            if (isDownloaded) {
                shareVideo()
            } else {
                if (!isDownloading) {
                    Toast.makeText(this, "Downloading Video...", Toast.LENGTH_SHORT).show()
                    downloadVideo(videoName)
                    share = true
                } else {
                    checkDownloadStatus()
                }

            }

        }
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
        player.release()

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
                    checkDownloadStatus()
                    //Toast.makeText(this, "Video is downloading.", Toast.LENGTH_SHORT).show()
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

        builder.setPositiveButton("Discard") { dialog, _ -> // Do nothing but close the dialog
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }

        builder.setNegativeButton("NO") { dialog, _ -> // Do nothing
            dialog.dismiss()
        }

        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun downloadVideo(videoName: String) {
        isDownloading = true

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
                Environment.DIRECTORY_MOVIES, "HApp" + File.separator + videoName
            )

            downloadID = downloadManager.enqueue(request)
        } catch (e: Exception) {
            Log.e("downloadVideo", e.message.toString())
        }


    }

    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            checkDownloadStatus()
        }

    }


    private fun checkDownloadStatus() {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        //val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

        query.setFilterById(downloadID)
        val cursor: Cursor? = downloadManager.query(query)
        Log.d(TAG, "Starting..")
        if (cursor!!.moveToFirst()) {
            Log.d(TAG, "moved to cursor")
            val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val status = cursor.getInt(columnIndex)
            val columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
            val reason = cursor.getInt(columnReason)

            when (status) {
                DownloadManager.STATUS_FAILED -> {
                    var failedReason = ""
                    when (reason) {
                        DownloadManager.ERROR_CANNOT_RESUME -> failedReason =
                            "ERROR_CANNOT_RESUME"
                        DownloadManager.ERROR_DEVICE_NOT_FOUND -> failedReason =
                            "ERROR_DEVICE_NOT_FOUND"
                        DownloadManager.ERROR_FILE_ALREADY_EXISTS -> failedReason =
                            "ERROR_FILE_ALREADY_EXISTS"
                        DownloadManager.ERROR_FILE_ERROR -> failedReason = "ERROR_FILE_ERROR"
                        DownloadManager.ERROR_HTTP_DATA_ERROR -> failedReason =
                            "ERROR_HTTP_DATA_ERROR"
                        DownloadManager.ERROR_INSUFFICIENT_SPACE -> failedReason =
                            "ERROR_INSUFFICIENT_SPACE"
                        DownloadManager.ERROR_TOO_MANY_REDIRECTS -> failedReason =
                            "ERROR_TOO_MANY_REDIRECTS"
                        DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> failedReason =
                            "ERROR_UNHANDLED_HTTP_CODE"
                        DownloadManager.ERROR_UNKNOWN -> failedReason = "ERROR_UNKNOWN"
                    }
                    downloadFailed(failedReason)
                }
                DownloadManager.STATUS_PAUSED -> {
                    var pausedReason = ""
                    when (reason) {
                        DownloadManager.PAUSED_QUEUED_FOR_WIFI -> pausedReason =
                            "PAUSED_QUEUED_FOR_WIFI"
                        DownloadManager.PAUSED_UNKNOWN -> pausedReason = "PAUSED_UNKNOWN"
                        DownloadManager.PAUSED_WAITING_FOR_NETWORK -> pausedReason =
                            "PAUSED_WAITING_FOR_NETWORK"
                        DownloadManager.PAUSED_WAITING_TO_RETRY -> pausedReason =
                            "PAUSED_WAITING_TO_RETRY"
                    }
                    Toast.makeText(
                        this@VideoPlayerActivity,
                        "Download PAUSED: $pausedReason",
                        Toast.LENGTH_LONG
                    ).show()
                }
                DownloadManager.STATUS_PENDING -> Toast.makeText(
                    this@VideoPlayerActivity,
                    "Download is pending.",
                    Toast.LENGTH_LONG
                ).show()
                DownloadManager.STATUS_RUNNING -> Toast.makeText(
                    this@VideoPlayerActivity,
                    "Download is running.",
                    Toast.LENGTH_LONG
                ).show()
                DownloadManager.STATUS_SUCCESSFUL -> {
                    downloadSuccessful()
                    //downloadManager.remove(id)
                }
            }


        } else {
            downloadCancelled()
        }
    }

    private fun downloadFailed(failedReason: String) {
        Toast.makeText(
            this@VideoPlayerActivity,
            "Download FAILED: $failedReason",
            Toast.LENGTH_LONG
        ).show()
        isDownloading = false
        isDownloaded = false
    }

    private fun downloadSuccessful() {
        Toast.makeText(
            this@VideoPlayerActivity,
            "DOWNLOAD SUCCESSFUL",
            Toast.LENGTH_LONG
        ).show()
        isDownloading = false
        isDownloaded = true
        val oldVideoPath = videoPath
        @Suppress("DEPRECATION") val videoFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
            "HApp" + File.separator + videoName
        )


        videoPath = videoFile.path
        if (!share) {
            showDialog(makeDialog(), oldVideoPath)
        } else {
            shareVideo()
        }


    }

    private fun downloadCancelled() {
        Log.d(TAG, "Download cancelled")
        Toast.makeText(
            this@VideoPlayerActivity,
            "Download cancelled.",
            Toast.LENGTH_LONG
        ).show()
        isDownloading = false
        isDownloaded = false
    }

}