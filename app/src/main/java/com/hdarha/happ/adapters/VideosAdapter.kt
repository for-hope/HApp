package com.hdarha.happ.adapters

import HVideo
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.hdarha.happ.R
import com.hdarha.happ.other.inflate
import com.yalantis.ucrop.util.FileUtils.getPath
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class VideosAdapter(private val videos:ArrayList<HVideo>,private val activity:Activity) : RecyclerView.Adapter<VideosAdapter.VideoHolder>() {

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
            val thumbnailImageView =  inflated.findViewById<ImageView>(R.id.video_thumbnail)
            val deleteButton = inflated.findViewById<ImageView>(R.id.video_delete)
            val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
            val c = Calendar.getInstance()
            c.timeInMillis = video.dateAdded
            val date = sdf.format(c.time)
            dateTextView.text = date

            titleTextView.text = video.name.replace("HApp_Video_","")

            val timeInMillis = video.duration
            val mins = TimeUnit.MILLISECONDS.toMinutes(timeInMillis).toInt()
            val secs = TimeUnit.MILLISECONDS.toSeconds(timeInMillis).toInt()
            val dur = "${String.format("%02d",mins)}:${String.format("%02d",secs)}"
            timeTextView.text = dur
            thumbnailImageView.setImageBitmap(video.thumbnail)

            deleteButton.setOnClickListener { confirmationDialog(video.uri) }

        }
        private fun confirmationDialog(fileUri: Uri){
            AlertDialog.Builder(activity)
                .setTitle("Delete Video")
                .setMessage("Are you sure you want to delete this video?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes) { _, _ ->
                  deleteVideo(fileUri)
                }
                .setNegativeButton(android.R.string.no, null)
                .show()
        }
        private fun deleteVideo(fileUri:Uri) {
            val f = File(getPath(activity,fileUri)!!)
            val deleted= f.delete()
            if (deleted){
            Toast.makeText(activity,"Video deleted.",Toast.LENGTH_SHORT).show()
                videos.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                notifyDataSetChanged()
            } else {
                Toast.makeText(activity,"Video not deleted.",Toast.LENGTH_SHORT).show()
            }
        }

    }
}