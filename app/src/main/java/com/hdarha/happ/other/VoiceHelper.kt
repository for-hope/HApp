package com.hdarha.happ.other

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.hdarha.happ.adapters.RecyclerAdapter
import com.hdarha.happ.objects.Voice
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifDrawable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun retrieveFromCache(activity: Activity, listener: OnVoiceCallBack) {
    val voicesList = arrayListOf<Voice>()
    val gson = Gson()
    val mPrefs = activity.getSharedPreferences("voice", Context.MODE_PRIVATE)
    var jsonList = mutableSetOf<String>()
    jsonList = mPrefs.getStringSet("jsonList", jsonList)!!
    if (jsonList.isNotEmpty()) {
        GlobalScope.launch {
            delay(1000L)
            activity.runOnUiThread {
                Log.d("SoundLibrary", "Async Cache")
                for (jsonObject in jsonList) {
                    val voice: Voice = gson.fromJson(jsonObject, Voice::class.java)

                    voicesList.add(voice)
                }
                voicesList.sortByDescending { it.isFav }
                listener.onVoicesRetrieved(voicesList, false)
            }
        }


    }

}

fun saveToCache(voices: ArrayList<Voice>, activity: Activity) {
    GlobalScope.launch {
        Log.d("SoundLibrary", "Saving Cache")
        val gson = Gson()
        val mPrefs = activity.getSharedPreferences("voice", Context.MODE_PRIVATE)
        val editor = mPrefs.edit()
        val jsonArrayList = arrayListOf<String>()

        for (voice in voices) {
            val jsonString = gson.toJson(voice)
            Log.d("VOICE_CACHING", voice.caption)
            jsonArrayList.add(jsonString)
        }
        val set = jsonArrayList.toSet()



        editor.putStringSet("jsonList", set)
        editor.putBoolean("isCached", true)
        editor.apply()
    }

}

fun checkCache(activity: Activity, listener: OnVoiceCallBack) {
    Log.d("SoundLibrary", "Checking Cache")
    val mPrefs = activity.getSharedPreferences("voice", Context.MODE_PRIVATE)

    val isCached = mPrefs.getBoolean("isCached", false)
    if (!isCached) {
        Log.d("SoundLibrary", "Cache not detected")
        getSounds(activity, listener)
    } else {
        retrieveFromCache(activity, listener)
        Log.d("SoundLibrary", "Cache detected")
    }
    //editor.putBoolean("voice_cache",true)
}


fun getSounds(activity: Activity, listener: OnVoiceCallBack) {
    val retrofitClient = RetrofitClientInstance()
    val service: RetrofitClientInstance.VoiceService = retrofitClient.retrofitInstance!!.create(
        RetrofitClientInstance.VoiceService::class.java
    )

    val voices = service.listVoices()

    voices?.enqueue(object : Callback<List<Voice>> {

        override fun onResponse(call: Call<List<Voice>>, response: Response<List<Voice>>) {
            Log.d("SoundsList", response.body()!![0].caption)
            val mVoices = response.body()
            getMetaData(ArrayList(mVoices!!), listener)

        }

        override fun onFailure(call: Call<List<Voice>>, t: Throwable) {
            Log.d("SoundsList", t.localizedMessage?.toString()!!)
            Toast.makeText(
                activity,
                "Something went wrong...Please try later!",
                Toast.LENGTH_SHORT
            ).show()
        }


    })
}

fun getMetaData(voices: ArrayList<Voice>, listener: OnVoiceCallBack) {

    for ((index, voice) in voices.withIndex()) {
        val pathStr = voice.location


        //setup duration
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(pathStr, HashMap<String, String>())
        val durationStr =
            mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val timeInMillisec = durationStr.toLong()
        voices[index].duration = timeInMillisec
        mmr.release()
    }
    listener.onVoicesRetrieved(voices, true)
    //TODO voicesLoaded(voices, true)

}

fun manageMediaPlayer(title: TextView, mGifDrawable: GifDrawable, pathStr: String, adapter: RecyclerAdapter?,mPlayer:MediaPlayer) {
    if (mPlayer.isPlaying) {
        mPlayer.stop()
    }
    adapter?.resetLayout()
    mGifDrawable.start()
    title.typeface = Typeface.DEFAULT_BOLD
    mPlayer.reset()
    mPlayer.setDataSource(pathStr)
    mPlayer.prepare()
    mPlayer.start()


    mPlayer.setOnCompletionListener {
        title.typeface = Typeface.DEFAULT
        mGifDrawable.stop();
        mGifDrawable.seekTo(0)
    }
}

interface OnVoiceCallBack {
    fun onVoicesRetrieved(voices: ArrayList<Voice>, isCache: Boolean)
}

