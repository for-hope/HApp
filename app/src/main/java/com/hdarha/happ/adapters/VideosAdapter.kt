package com.hdarha.happ.adapters

import HVideo
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.hdarha.happ.R
import com.hdarha.happ.activities.VideoPlayerActivity
import com.hdarha.happ.other.TAG
import com.hdarha.happ.other.get24dpDrawable
import com.hdarha.happ.other.inflate
import com.yalantis.ucrop.util.FileUtils.getPath
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class VideosAdapter(private val videos: ArrayList<HVideo>, private val activity: Activity) :
    RecyclerView.Adapter<VideosAdapter.VideoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        val inflatedView = parent.inflate(R.layout.view_video_item, false)
        return VideoHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        val videoItem = videos[position]
        holder.bindItem(videoItem, activity)
    }


    inner class VideoHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var video: HVideo? = null

        //3
        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

        }

        fun bindItem(video: HVideo, activity: Activity) {
            this.video = video
            val inflated = view
            val dateTextView = inflated.findViewById<TextView>(R.id.video_date_tv)
            val titleTextView = inflated.findViewById<TextView>(R.id.video_title_tv)
            val timeTextView = inflated.findViewById<TextView>(R.id.video_timestamp)
            val thumbnailImageView = inflated.findViewById<ImageView>(R.id.video_thumbnail)
            val deleteButton = inflated.findViewById<ImageView>(R.id.video_delete)
            val statusTextView = inflated.findViewById<TextView>(R.id.video_status)
            val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
            val c = Calendar.getInstance()
            //

            statusTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                get24dpDrawable(
                    activity,
                    R.drawable.ic_check_circle_black_24dp
                ), null, null, null
            )


            c.timeInMillis = video.dateAdded
            var date = sdf.format(c.time)

            if (DateUtils.isToday(c.time.time)) {
                date = activity.getString(R.string.today)
            }
            dateTextView.text = date
            dateTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                get24dpDrawable(
                    activity,
                    R.drawable.ic_date_range_black_24dp
                ), null, null, null
            )
            titleTextView.text = video.name.replace("HApp_Video_", "")


            val timeInMillis = video.duration
            val mins = TimeUnit.MILLISECONDS.toMinutes(timeInMillis).toInt()
            val secs = TimeUnit.MILLISECONDS.toSeconds(timeInMillis).toInt()
            val dur = "${String.format("%02d", mins)}:${String.format("%02d", secs)}"
            timeTextView.text = dur
            timeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                get24dpDrawable(
                    activity,
                    R.drawable.ic_video_camera
                ), null, null, null
            )


            try {
                thumbnailImageView.setImageBitmap(video.thumbnail)
            } catch (e: OutOfMemoryError) {
                Log.e(TAG, e.message.toString())
                Toast.makeText(activity, "Error loading image (Memory Full).", Toast.LENGTH_SHORT)
                    .show()
            }

            //val vectorBin = activity.getDrawable() as VectorDrawable

            // val drawableBin:Drawable = BitmapDrawable(activity.resources, Bitmap.createScaledBitmap(bitmap,50,50,true))

            //val b =vectorBin.toBitmap(dimen, dimen)
            deleteButton.setImageDrawable(get24dpDrawable(activity, R.drawable.ic_bin))
            deleteButton.setOnClickListener { confirmationDialog(video.uri) }
            thumbnailImageView.setOnClickListener {
                val intent = Intent(activity, VideoPlayerActivity::class.java)
                intent.putExtra("url", getPath(activity, video.uri))
                activity.startActivity(intent)

            }
        }

        private fun confirmationDialog(fileUri: Uri) {
            AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.delete_video))
                .setMessage(activity.getString(R.string.delete_confirmation))
                .setPositiveButton(activity.getString(R.string.delete)) { _, _ ->
                    deleteVideo(fileUri)
                }
                .setNegativeButton(android.R.string.no, null)
                .show()
        }

        private fun deleteVideo(fileUri: Uri) {
            val f = File(getPath(activity, fileUri)!!)
            val deleted = f.delete()
            if (deleted) {
                Toast.makeText(activity, "Video deleted.", Toast.LENGTH_SHORT).show()
                videos.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "Video not deleted.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}