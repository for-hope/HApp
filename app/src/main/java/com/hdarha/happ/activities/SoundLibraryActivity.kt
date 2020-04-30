package com.hdarha.happ.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hdarha.happ.R
import com.hdarha.happ.adapters.RecyclerAdapter
import com.hdarha.happ.objects.Sound
import kotlinx.android.synthetic.main.activity_sound_library.*

class SoundLibraryActivity : AppCompatActivity(),RecyclerAdapter.OnItemClick{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_library)
        setSupportActionBar(toolbar_sound_library)
        supportActionBar?.title = "Sound Library"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val linearLayoutManager = LinearLayoutManager(this)
        val soundsList = initSounds()
        val adapter = RecyclerAdapter(this,soundsList,this)
        recyclerview_sounds.layoutManager = linearLayoutManager
        recyclerview_sounds.adapter = adapter
        adapter.notifyItemInserted(soundsList.size - 1)

    }

    private fun initSounds() : ArrayList<Sound> {
        val soundsList: ArrayList<Sound> = arrayListOf()
            val s1 = Sound(
                1,
                "Meme number one",
                "memer1",
                "2:00",
                "www.google.com"
            )
            val s2 = Sound(
                2,
                "Meme number two",
                "memer2",
                "5:00",
                "www.google.com"
            )
            val s3 = Sound(
                3,
                "Meme number three",
                "memer3",
                "1:00",
                "www.google.com"
            )
            val s4 = Sound(
                4,
                "Meme number one",
                "memer5",
                "2:00",
                "www.google.com"
            )
            val s5 = Sound(
                5,
                "Meme number two",
                "meme02",
                "5:00",
                "www.google.com"
            )
            val s6 = Sound(
                6,
                "Meme number three",
                "memer8",
                "1:00",
                "www.google.com"
            )

            soundsList.add(s1)
            soundsList.add(s2)
            soundsList.add(s3)
            soundsList.add(s4)
            soundsList.add(s5)
            soundsList.add(s6)

        return soundsList

    }

    override fun onClick(value: String?) {
        Toast.makeText(this,"Hey",Toast.LENGTH_SHORT).show()
    }

}