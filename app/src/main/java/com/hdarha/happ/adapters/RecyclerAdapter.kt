package com.hdarha.happ.adapters

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.haseebazeem.sampleGif.GifImageView
import com.hdarha.happ.R
import com.hdarha.happ.other.inflate
import com.hdarha.happ.objects.Sound

class RecyclerAdapter(
    context: Context,
    private val sounds: MutableList<Sound>,
    listener: OnItemClick
) :
    RecyclerView.Adapter<RecyclerAdapter.SoundHolder>() {
    private var soundId: Int = -1
    private var favId: Int = -1
    private var context = context
    // var fragment = dialogFragment

    private val mCallback: OnItemClick? = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundHolder {
        val inflatedView = parent.inflate(R.layout.view_sounds, false)
        return SoundHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        Log.d("size", sounds.size.toString())
        return sounds.size
    }

    override fun onBindViewHolder(holder: SoundHolder, position: Int) {
        val itemPhoto = sounds[position]
        holder.bindPhoto(itemPhoto, soundId)
    }

    private fun selectSound(soundId: Int) {
        this.soundId = soundId

    }


    interface OnItemClick {
        fun onClick(value: String?)
    }


    inner class SoundHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        //2
        private var view: View = v
        private var sound: Sound? = null

        //3
        init {
            v.setOnClickListener(this)
        }

        //4
        override fun onClick(v: View) {
//            Log.d("RecyclerView", "CLICK! $adapterPosition")
//            val soundId = sounds[adapterPosition].id
//            selectSound(soundId)
//            notifyDataSetChanged()

        }

        fun bindPhoto(sound: Sound, sId: Int) {
            var soundId = sId
            this.sound = sound
            val inflated = view
            val title = inflated.findViewById<TextView>(R.id.sound_title_tv)
            val author = inflated.findViewById<TextView>(R.id.sound_author_tv)
            val timestamp = inflated.findViewById<TextView>(R.id.sound_timestamp_tv)
            val eqImg = inflated.findViewById<GifImageView>(R.id.eq_img)
            val playImg = inflated.findViewById<ImageView>(R.id.play_img)
            val cardView = inflated.findViewById<MaterialCardView>(R.id.sound_card)
            val favIcon = inflated.findViewById<MaterialCheckBox>(R.id.fav_icon)
            favIcon.isChecked = adapterPosition == favId




            title.text = sound.title
            author.text = sound.author
            timestamp.text = sound.timestamp


            if (sound.id != soundId) {
                title.typeface = Typeface.DEFAULT
                eqImg.visibility = View.INVISIBLE
                playImg.visibility = View.VISIBLE
            } else if (title.typeface.style != Typeface.BOLD) {
                Log.d("TEST", "Visibility1")
                title.typeface = Typeface.DEFAULT_BOLD
                eqImg.visibility = View.VISIBLE
                playImg.visibility = View.INVISIBLE

            }
            cardView.setOnClickListener {
                val soundId1 = sounds[adapterPosition].id
                if (soundId == soundId1) {
                    Toast.makeText(view.context, "Its same", Toast.LENGTH_SHORT).show()
                    title.typeface = Typeface.DEFAULT
                    eqImg.visibility = View.INVISIBLE
                    playImg.visibility = View.VISIBLE
                    soundId = -1
                    selectSound(-1)
                } else {
                    selectSound(soundId1)
                    notifyDataSetChanged()
                }

            }

            favIcon.setOnClickListener {
                Log.d("Checked", "$adapterPosition")
                if (favId != -1) {
                    favId = adapterPosition
                    this.view.post {
                        Log.d("POST", "Notfied")
                        notifyDataSetChanged()
                    }
                } else {
                    favId = adapterPosition
                }
                mCallback!!.onClick(sounds[adapterPosition].title)
            }

        }

//        companion object {
//            //5
//            private val PHOTO_KEY = "PHOTO"
//        }
    }
}

