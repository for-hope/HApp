package com.hdarha.happ.adapters

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.haseebazeem.sampleGif.GifImageView
import com.hdarha.happ.R
import com.hdarha.happ.objects.Voice
import com.hdarha.happ.other.inflate
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageButton
import java.io.File
import java.util.concurrent.TimeUnit

private var titleReset: TextView? = null
private var gifDrawableReset: GifDrawable? = null
private var selectedSound: Int = -1

class RecyclerAdapter(
    private val sounds: ArrayList<Voice>,
    private val isActivity: Boolean,
    listener: OnItemClick,
    voiceClickListener: OnVoiceClick
) :
    RecyclerView.Adapter<RecyclerAdapter.SoundHolder>() {
    private var soundId: String = ""

    // var fragment = dialogFragment
    private val mVoiceClickCallBack: OnVoiceClick? = voiceClickListener
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
        holder.bindPhoto(itemPhoto)
    }


    fun resetLayout() {
        titleReset?.typeface = Typeface.DEFAULT
        gifDrawableReset?.stop()
        gifDrawableReset?.seekTo(0)
    }


    interface OnItemClick {
        fun onClick(value: Voice?, key: Int)
    }

    interface OnVoiceClick {
        fun onPlay(title: TextView, mGifDrawable: GifDrawable, pathStr: String)
    }

    inner class SoundHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        //2
        private var view: View = v
        private var sound: Voice? = null
        private var cacheDir: File? = null
        //private var mPlayer:MediaPlayer = mp

        //3
        init {
            v.setOnClickListener(this)
        }

        //4
        override fun onClick(v: View) {}


        fun bindPhoto(sound: Voice) {
            this.sound = sound
            val inflated = view
            val title = inflated.findViewById<TextView>(R.id.sound_title_tv)
            val author = inflated.findViewById<TextView>(R.id.sound_author_tv)
            val timestamp = inflated.findViewById<TextView>(R.id.sound_timestamp_tv)
            val eqImg = inflated.findViewById<GifImageView>(R.id.eq_img)
            val gifImg = inflated.findViewById<GifImageButton>(R.id.gifImageBtn)
            val cardView = inflated.findViewById<MaterialCardView>(R.id.sound_card)
            val favIcon = inflated.findViewById<MaterialCheckBox>(R.id.fav_icon)
            val selectSoundLayout = inflated.findViewById<LinearLayout>(R.id.SelectSoundLayout)
            //favIcon.isChecked = adapterPosition == favId
            favIcon.isChecked = sound.isFav

            title.text = sound.caption
            author.text = sound.tags[0]

            //get duration
            var pathStr = sound.location

            cacheDir = File(inflated.context.externalCacheDir, "voices/" + sound.name + ".m4a")
            if (cacheDir!!.exists()) {
                pathStr = cacheDir!!.path
            }

            //val uri: Uri = Uri.parse(pathStr)

            //setup duration

            val timeInMillisec = sound.duration

            val mins = TimeUnit.MILLISECONDS.toMinutes(timeInMillisec).toInt()
            val secs = TimeUnit.MILLISECONDS.toSeconds(timeInMillisec).toInt()

            val dur = "${String.format("%02d", mins)}:${String.format("%02d", secs)}"

            timestamp.text = dur



            if (sound.name != soundId) {
                title.typeface = Typeface.DEFAULT
                eqImg.visibility = View.INVISIBLE

            }

            //setup player
            //val mPlayer = MediaPlayer()


            gifImg.setImageResource(R.drawable.icon)
            val mGifDrawable = gifImg.drawable as GifDrawable
            mGifDrawable.stop()



            gifImg.setOnClickListener {
                mVoiceClickCallBack?.onPlay(title, mGifDrawable, pathStr)
                titleReset = title
                gifDrawableReset = mGifDrawable

            }



            if (!isActivity) {
                favIcon.isClickable = false
                favIcon.isActivated = false
                if (selectedSound != adapterPosition) {
                    selectSoundLayout.background = null
                    cardView.setCardBackgroundColor(Color.WHITE)
                }
                cardView.setOnClickListener {
                    selectSoundLayout.background =
                        ContextCompat.getDrawable(view.context, R.color.dracula_primary_dark)
                    cardView.setCardBackgroundColor(Color.parseColor("#e9e9e9"))
                    selectedSound = adapterPosition
                    mCallback!!.onClick(sounds[adapterPosition], adapterPosition)
                    notifyDataSetChanged()
                }

            } else {
                cardView.setOnClickListener {
                    mVoiceClickCallBack?.onPlay(title, mGifDrawable, pathStr)
                    titleReset = title
                    gifDrawableReset = mGifDrawable
                }
                favIcon.setOnClickListener {
                    Log.d("Checked", "$adapterPosition")
                    mCallback!!.onClick(sounds[adapterPosition], adapterPosition)
                }

            }

        }

    }


}

