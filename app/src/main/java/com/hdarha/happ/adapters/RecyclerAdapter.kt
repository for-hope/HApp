package com.hdarha.happ.adapters

import android.content.Context
import android.graphics.Typeface
import android.media.AudioAttributes
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import java.util.concurrent.TimeUnit

private var isPlaying: Boolean = false
private var mediaPlayers: ArrayList<MediaPlayer> = arrayListOf()
private var mPlayer: MediaPlayer = MediaPlayer()
private var audioPlaying = -1
private var titleReset:TextView? = null
private var gifDrawableReset:GifDrawable? = null
class RecyclerAdapter(
    context: Context,
    private val sounds: ArrayList<Voice>,
    private val isActivity: Boolean,
    listener: OnItemClick
) :
    RecyclerView.Adapter<RecyclerAdapter.SoundHolder>() {
    private var soundId: String = ""
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

    private fun selectSound(soundId: String) {
        this.soundId = soundId

    }


    interface OnItemClick {
        fun onClick(value: Voice?, key: Int)
    }


    inner class SoundHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        //2
        private var view: View = v
        private var sound: Voice? = null
        //private var mPlayer:MediaPlayer = mp

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

        fun resetLayout() {
            titleReset?.typeface = Typeface.DEFAULT
            gifDrawableReset?.stop();
            gifDrawableReset?.seekTo(0)
        }
        fun bindPhoto(sound: Voice, sId: String) {
            var soundId = sId
            this.sound = sound
            val inflated = view
            val title = inflated.findViewById<TextView>(R.id.sound_title_tv)
            val author = inflated.findViewById<TextView>(R.id.sound_author_tv)
            val timestamp = inflated.findViewById<TextView>(R.id.sound_timestamp_tv)
            val eqImg = inflated.findViewById<GifImageView>(R.id.eq_img)
            val playImg = inflated.findViewById<ImageView>(R.id.play_img)
            val gifImg = inflated.findViewById<GifImageButton>(R.id.gifImageBtn)
            val cardView = inflated.findViewById<MaterialCardView>(R.id.sound_card)
            val favIcon = inflated.findViewById<MaterialCheckBox>(R.id.fav_icon)
            val selectSoundLayout = inflated.findViewById<LinearLayout>(R.id.SelectSoundLayout)
            //favIcon.isChecked = adapterPosition == favId
            favIcon.isChecked = sound.isFav



            title.text = sound.caption
            author.text = sound.tags[0]
            //get duration
            val pathStr = sound.location
            val uri: Uri = Uri.parse(pathStr)

            //setup duration

            val timeInMillisec = sound.duration

            val mins = TimeUnit.MILLISECONDS.toMinutes(timeInMillisec).toInt()
            val secs = TimeUnit.MILLISECONDS.toSeconds(timeInMillisec).toInt()

            val dur = "${String.format("%02d", mins)}:${String.format("%02d", secs)}"

            timestamp.text = dur



            if (sound.name != soundId) {
                title.typeface = Typeface.DEFAULT
                eqImg.visibility = View.INVISIBLE
                playImg.visibility = View.INVISIBLE
            }

            //setup player
            //val mPlayer = MediaPlayer()


            gifImg.setImageResource(R.drawable.icon)
            val mGifDrawable = gifImg.drawable as GifDrawable
            mGifDrawable.stop()


            gifImg.setOnClickListener {
                if (audioPlaying != adapterPosition){
                    //mPlayer.release()
                    if (mPlayer.isPlaying) {
                    resetLayout()
                    }
                    mPlayer.reset()
                    mPlayer.setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    mPlayer.setDataSource(pathStr)
                    mPlayer.prepare()
                    Log.e("TPRE","AGAIN")
                    audioPlaying = adapterPosition


                }
                if (mGifDrawable.isRunning) {
                    mPlayer.seekTo(mPlayer.duration-1)

                } else {

                    if (!mPlayer.isPlaying) {
                        title.typeface = Typeface.DEFAULT_BOLD
                        mPlayer.start()
                        titleReset = title
                        gifDrawableReset = mGifDrawable
                        isPlaying = true
                        mGifDrawable.start()
                    }
                }
                mPlayer.setOnCompletionListener {
                    //mGifDrawable.reset();
                    Log.e("AdapterPostionCalled","$adapterPosition")
                    isPlaying = false
                    title.typeface = Typeface.DEFAULT
                    mGifDrawable.stop();
                    mGifDrawable.seekTo(0)
                    audioPlaying = -1
                }

            }


            cardView.setOnClickListener {
                selectSoundLayout.background =
                    ContextCompat.getDrawable(view.context, R.color.materialLightBlue)
            }

            favIcon.setOnClickListener {
                Log.d("Checked", "$adapterPosition")
                mCallback!!.onClick(sounds[adapterPosition], adapterPosition)
            }

        }


//        companion object {
//            //5
//            private val PHOTO_KEY = "PHOTO"
//        }
    }

}

