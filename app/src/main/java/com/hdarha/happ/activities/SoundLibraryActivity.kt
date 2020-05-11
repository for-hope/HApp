package com.hdarha.happ.activities

import android.app.DownloadManager
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.hdarha.happ.R
import com.hdarha.happ.adapters.RecyclerAdapter
import com.hdarha.happ.objects.Voice
import com.hdarha.happ.other.OnVoiceCallBack
import com.hdarha.happ.other.checkCache
import com.hdarha.happ.other.manageMediaPlayer
import com.hdarha.happ.other.saveToCache
import kotlinx.android.synthetic.main.activity_sound_library.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifDrawable
import java.io.File


class SoundLibraryActivity : AppCompatActivity(), RecyclerAdapter.OnItemClick, OnVoiceCallBack,
    RecyclerAdapter.OnVoiceClick {
    private var voicesList: ArrayList<Voice> = arrayListOf()
    private var downloadChecker = true
    private var mAdapter: RecyclerAdapter? = null
    private var audioPlaying = false
    private lateinit var mPlayer: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_library)
        setSupportActionBar(toolbar_sound_library)
        supportActionBar?.title = "Sound Library"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        mPlayer = MediaPlayer()
        window.statusBarColor = ContextCompat.getColor(this, R.color.dracula_primary_dark)
        voicesPB.isIndeterminate = true
        voicesPB.visibility = View.VISIBLE
        pbLayout.visibility = View.VISIBLE

        recyclerview_sounds.visibility = View.GONE
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerview_sounds.layoutManager = linearLayoutManager
        recyclerview_sounds.adapter = RecyclerAdapter(this, arrayListOf(),true,this,this)


        soundsRefresh.setRefreshListener {

            checkCache(this, this)

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        if (mPlayer.isPlaying) {
            mPlayer.stop()
        }
        mPlayer.reset()
        mPlayer.release()
        super.onDestroy()
    }

    override fun onPause() {
        mPlayer.reset()
        super.onPause()
    }

    private fun downloader() {

        GlobalScope.launch {
            val policy =
                StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            if (downloadChecker) {
                for (voice in voicesList) {

                    //val cacheFile = File(getExternalFilesDir("voices"),voice.name+".m4a")

                    val cacheFile =
                        File(externalCacheDir, "voices" + File.separator + voice.name + ".m4a")
                    if (!cacheFile.exists()) {

                        val downloadManager =
                            getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                        val uri: Uri = Uri.parse(voice.location)

                        val request = DownloadManager.Request(uri)
                        request.setTitle(voice.caption)
                        request.setDescription("Downloading")
                        //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                        Log.d("DownloadManager", "Saving to $cacheFile")
                        request.setDestinationUri(Uri.fromFile(cacheFile))
                        Log.d("DownloadManager", "Saved to ${Uri.fromFile(cacheFile)}")
                        downloadManager.enqueue(request)

                    }
                }
                downloadChecker = false
            }

        }
    }

    override fun onResume() {
        super.onResume()
        checkCache(this, this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    override fun onClick(value: Voice?, key: Int) {
        Log.d("ListUnSorted", voicesList.toString())

        run {
            voicesList[key].isFav = !voicesList[key].isFav

            GlobalScope.launch {
                runOnUiThread {

                    voicesList.sortByDescending { it.isFav }
                    //voicesList!!.sortWith(Comparator { t, t2 -> t.isFav.compareTo(t2.isFav) })
                    //voicesList!!.reverse()
                    Log.d("ListSorted", voicesList.toString())
                    val adapter = RecyclerAdapter(
                        this@SoundLibraryActivity,
                        voicesList,
                        true,
                        this@SoundLibraryActivity,
                        this@SoundLibraryActivity
                    )

                    recyclerview_sounds.adapter = adapter
                    adapter.notifyDataSetChanged()
                    voicesList.forEach {
                        Log.d("VOICE", it.caption)
                    }
                    saveToCache(voicesList, this@SoundLibraryActivity)
                }
            }
        }
    }

    override fun onVoicesRetrieved(voices: ArrayList<Voice>, isCache: Boolean) {
        if (isCache) {
            saveToCache(voices, this)
            Log.d("DownloadManager", "about to start")
            downloader()
        }
        this.voicesList = voices
        voicesPB.visibility = View.GONE
        pbLayout.visibility = View.GONE
        recyclerview_sounds.visibility = View.VISIBLE

        val adapter = RecyclerAdapter(this, voices, true, this, this)

        recyclerview_sounds.adapter = adapter
        mAdapter = adapter
        adapter.notifyItemInserted(voices.size - 1)
        soundsRefresh.setRefreshing(false)
    }


    override fun onPlay(title: TextView, mGifDrawable: GifDrawable, pathStr: String) {
        manageMediaPlayer(title, mGifDrawable, pathStr, mAdapter, mPlayer)
    }

}