package com.hdarha.happ.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.hdarha.happ.R
import com.hdarha.happ.adapters.VideosAdapter
import com.hdarha.happ.objects.VideoItem
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy
import kotlinx.android.synthetic.main.activity_my_videos.*
import java.util.concurrent.TimeUnit

const val REQUEST_CODE_CHOOSE = 1
class MyVideosActivity : AppCompatActivity() {
    val context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_videos)
        setSupportActionBar(toolbar_my_videos)
        supportActionBar?.title = "My Videos"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val linearLayoutManager = LinearLayoutManager(this)
        val adapter = VideosAdapter(setupVideoList(),this)

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTitle)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        };


        recyclerview_videos.layoutManager = linearLayoutManager
        recyclerview_videos.adapter = adapter
        adapter.notifyDataSetChanged()


        fab.setOnClickListener {
            permissionCheckGallery()
        }

    }

    private fun setupVideoList() : ArrayList<VideoItem>{
        val videoPath = "android.resource://" + packageName+ "/" + R.raw.vid
        val mVideosList: ArrayList<VideoItem> = arrayListOf()
        val mMMR = MediaMetadataRetriever()
        mMMR.setDataSource(this, Uri.parse(videoPath))
        val bmp = mMMR.frameAtTime

        val retriever = MediaMetadataRetriever()

        retriever.setDataSource(this, Uri.parse(videoPath))
        val time =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val timeInMillisec = time.toLong()
        retriever.release()
        val mins = TimeUnit.MILLISECONDS.toMinutes(timeInMillisec).toInt()
        val secs = TimeUnit.MILLISECONDS.toSeconds(timeInMillisec).toInt()
        val dur = "${String.format("%02d",mins)}:${String.format("%02d",secs)}"
        for (x in 0..9) {
            val title = "Title $x"
            val date = " 0$x Apr"
            val dur = dur
            val img = bmp
            val video =  VideoItem(title,date,dur,img,1)
            mVideosList.add(video)
        }

        return mVideosList

    }

    private fun showGallery() {
        Matisse.from(this)
            .choose(MimeType.ofImage())
            .countable(true)
            .maxSelectable(1)
            .capture(true)
            .theme(R.style.Matisse_Dracula)
            .captureStrategy(
                CaptureStrategy(true, "com.hdarha.app.fileprovider", "testhapp")
            )
            .setOnSelectedListener { _, pathList ->
                Log.e(
                    "onSelected",
                    "onSelected: pathList=$pathList"
                );
            }
            .showSingleMediaType(true)
            .autoHideToolbarOnSingleTap(true)
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .thumbnailScale(0.85f)
            .imageEngine(GlideEngine())
            .showPreview(false) // Default is `true`
            .forResult(REQUEST_CODE_CHOOSE)
    }

    private fun permissionCheckGallery() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT)
                        .show()
                    showGallery()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    Toast.makeText(context, "DIK", Toast.LENGTH_SHORT).show()
                }
            }).check()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            //mAdapter.setData(Matisse.obtainResult(data), Matisse.obtainPathResult(data))
            Log.e("OnActivityResult ", Matisse.obtainOriginalState(data).toString())
            Log.e("OnActivityResult ", Matisse.obtainResult(data).toString())

            val intent = Intent(this.context, ImageDisplayActivity::class.java)
            intent.putExtra("imgUri", Matisse.obtainResult(data)[0].toString())
            startActivity(intent)


        } else if (requestCode == REQUEST_CODE_CHOOSE){
            Log.e("OnActivityResult","Error happened")
        }
    }
}