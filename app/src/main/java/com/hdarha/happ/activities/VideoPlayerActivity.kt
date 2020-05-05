package com.hdarha.happ.activities

import HVideo
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.google.gson.Gson
import com.hdarha.happ.R
import kotlinx.android.synthetic.main.activity_video_player.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.net.URL
import java.net.URLConnection


class VideoPlayerActivity : AppCompatActivity() {
    private var videoPath: String = ""
    var downloadID: Long = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        setSupportActionBar(toolbar_va)
        supportActionBar?.title = "Results"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home_black_24dp)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTitle)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        };

        videoPath = intent.getStringExtra("url")!!
        val uri = Uri.parse(videoPath)
        videoView.setVideoURI(uri)

        val mediaController = MediaController(this)

        videoView.setMediaController(mediaController)
        mediaController.setAnchorView(videoView)
        videoView.start()

        bottomAppBarVA.setNavigationOnClickListener {
            Toast.makeText(
                this,
                "Start Act",
                Toast.LENGTH_LONG
            ).show()
        }

        //registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        unregisterReceiver(onDownloadComplete);
//    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.video_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id: Int = item.itemId

        if (id == R.id.action_save_video) {
            if (videoPath != "") {
                val videoName = "HApp_Video_" + System.currentTimeMillis() + ".mp4"
                downloadVideo(videoPath, videoName)
                Toast.makeText(
                    this,
                    "Video saved to //" + Environment.DIRECTORY_MOVIES + File.separator + videoName,
                    Toast.LENGTH_LONG
                ).show()
            }
            return true
        } else if (id == R.id.action_cancel_video) {
            showConfirmationDialog()

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showConfirmationDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        builder.setTitle("Cancel")
        builder.setMessage("Are you sure you want to cancel without saving?")

        builder.setPositiveButton("YES") { dialog, which -> // Do nothing but close the dialog
            dialog.dismiss()
        }

        builder.setNegativeButton("NO") { dialog, which -> // Do nothing
            dialog.dismiss()
        }

        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun downloadVideo(videoPath: String, videoName: String) {

        GlobalScope.launch {
            val policy =
                StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)


            //val cacheFile = File(getExternalFilesDir("voices"),voice.name+".m4a")

            val filePath = File(externalCacheDir, "videos" + File.separator + videoName)
            if (!filePath.exists()) {

                val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val uri: Uri = Uri.parse(videoPath)

                val request = DownloadManager.Request(uri)
                request.setTitle(videoName)
                request.setDescription("Downloading Video...")
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setNotificationVisibility(0)

                //request.setDestinationUri(Uri.fromFile(filePath))
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, videoName)
                downloadID = downloadManager.enqueue(request)

                val sharedPreferences = getSharedPreferences("videos", Context.MODE_PRIVATE)
                ///
                ///
                val retriever =  MediaMetadataRetriever()
                var time = ""
                var bmp:Bitmap? = null
                Log.d("URI",uri.toString())
                Log.d("vId",videoPath)
                try {
                    retriever.setDataSource(
                        videoPath,
                        HashMap()
                    )
                     time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                     bmp = retriever.frameAtTime
                }catch (e:Exception){
                    e.printStackTrace()
                }

                retriever.release()

                val timeInMillisec = time.toLong()

                ///
                ///
                val gson = Gson()
                val listOfVideos = mutableListOf<HVideo>()
                var listOfJsonVideos =
                    sharedPreferences.getStringSet("videosList", mutableSetOf<String>())

                listOfJsonVideos?.forEach {
                    val video: HVideo = gson.fromJson(it, HVideo::class.java)
                    listOfVideos.add(video)
                }



                val video = HVideo(uri, videoName, timeInMillisec, 0,bmp!!)
                var b = true
                listOfJsonVideos = mutableSetOf()
                for ((index, v) in listOfVideos.withIndex()) {
                    if (video.name == v.name) {
                        listOfVideos[index] = video
                        b = false
                    }
                    val json = gson.toJson(v, HVideo::class.java)
                    listOfJsonVideos.add(json)
                }
                if (b) {
                    listOfVideos.add(video)
                    val json = gson.toJson(video, HVideo::class.java)
                    listOfJsonVideos.add(json)
                }

                sharedPreferences.edit { putStringSet("videosList", listOfJsonVideos) }

            }
        }
    }

//    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            //Fetching the download id received with the broadcast
//            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
//            //Checking if the received broadcast is for our enqueued download by matching download id
//            if (downloadID == id) {
//                val url = URL(videoPath)
//                val urlConnection: URLConnection = url.openConnection()
//                urlConnection.connect()
//                val file_size: Int = urlConnection.getContentLength()
//                Toast.makeText(this@VideoPlayerActivity, "Download Completed", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }
//    }

}