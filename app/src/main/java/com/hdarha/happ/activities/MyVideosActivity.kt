package com.hdarha.happ.activities

import HVideo
import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.hdarha.happ.R
import com.hdarha.happ.adapters.VideosAdapter
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.yalantis.ucrop.util.FileUtils.getPath
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy
import kotlinx.android.synthetic.main.activity_my_videos.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import uz.jamshid.library.IGRefreshLayout
import java.io.File


class MyVideosActivity : AppCompatActivity() {
    private val REQUEST_CODE_CHOOSE = 1
    val context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_videos)
        setSupportActionBar(toolbar_my_videos)
        supportActionBar?.title = "My Videos"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTitle)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        fab.setOnClickListener {
            permissionCheckGallery()
        }

        progressBarMyVideos.isIndeterminate = true
        progressBarMyVideos.visibility = View.VISIBLE
        val swipe = findViewById<IGRefreshLayout>(R.id.swipe)
        swipe.setRefreshListener {
            Handler().postDelayed({
                swipe.setRefreshing(false)
            }, 3000)
        }

        getVideoList()

    }

    private fun setAdapter(videos: MutableList<HVideo>) {
        val linearLayoutManager = LinearLayoutManager(this)
        val adapter = VideosAdapter(ArrayList(videos), this)
        recyclerview_videos.layoutManager = linearLayoutManager
        recyclerview_videos.adapter = adapter
        progressBarMyVideos.visibility = View.GONE
        adapter.notifyDataSetChanged()
    }

    private fun getVideoList() {
        GlobalScope.launch {
            val videoList = mutableListOf<HVideo>()
            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_ADDED
            )
            val selection = "${MediaStore.Video.Media.DISPLAY_NAME} LIKE ?"
            //val selectionArgs = arrayOf("%FolderName%")
            val selectionArgs = arrayOf("%HApp_%")
            val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"


            val query = contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )


            query?.use { cursor ->
                // Cache column indices.
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)

                val dateAddedColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)


                while (cursor.moveToNext()) {
                    // Get values of columns for a given video.
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)

                    val dateAdded = cursor.getLong(dateAddedColumn)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val mMMR = MediaMetadataRetriever()
                    if (File(getPath(this@MyVideosActivity,contentUri)!!).exists()) {
                    mMMR.setDataSource(this@MyVideosActivity, contentUri)
                    val bmp = mMMR.frameAtTime
                    val time = mMMR.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    val timeInMillis = time.toLong()
                    mMMR.release()
                    // Stores column values and the contentUri in a local object
                    // that represents the media file.
                    videoList += HVideo(contentUri, name, timeInMillis, dateAdded * 1000, bmp)
                    }
                }
            }
            videoList.reverse()
            runOnUiThread { setAdapter(videoList) }


        }

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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


        } else if (requestCode == REQUEST_CODE_CHOOSE) {
            Log.e("OnActivityResult", "Error happened")
        }
    }
}