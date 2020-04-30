package com.hdarha.happ.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.hdarha.happ.R
import com.hdarha.happ.objects.VideoItem
import com.hdarha.happ.other.inflate

class VideosAdapter(private val videos:ArrayList<VideoItem>,private val activity:Activity) : RecyclerView.Adapter<VideosAdapter.VideoHolder>() {

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
        private var video: VideoItem? = null

        //3
        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

        }

        fun bindItem(video: VideoItem, activity: Activity) {
            this.video = video
            val inflated = view

            val dateTextView = inflated.findViewById<TextView>(R.id.video_date_tv)
            val titleTextView = inflated.findViewById<TextView>(R.id.video_title_tv)
            val timeTextView = inflated.findViewById<TextView>(R.id.video_timestamp)
            val thumbnailImageView =  inflated.findViewById<ImageView>(R.id.video_thumbnail)
            val deleteButton = inflated.findViewById<ImageView>(R.id.video_delete)

            dateTextView.text = video.date
            titleTextView.text = video.title
            timeTextView.text = video.duration
            thumbnailImageView.setImageBitmap(video.thumbnail)
            deleteButton.setOnClickListener { Toast.makeText(activity,"Video deleted",Toast.LENGTH_SHORT).show() }

        }
    }
}