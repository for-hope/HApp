package com.hdarha.happ.adapters

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log


import android.view.View
import android.view.ViewGroup

import android.widget.ImageView


import androidx.recyclerview.widget.RecyclerView
import com.hdarha.happ.activities.ImageDisplayActivity
import com.hdarha.happ.R
import com.hdarha.happ.other.inflate
import com.squareup.picasso.Picasso


class GalleryAdapter(private val images: ArrayList<String>,private val activity: Activity) :
    RecyclerView.Adapter<GalleryAdapter.GalleryHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GalleryHolder {
        val inflatedView = parent.inflate(R.layout.layout_gallery_image_holder, false)
        return GalleryHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: GalleryHolder, position: Int) {
        val imageItem = images[position]
        holder.bindItem(imageItem,activity)
    }

    inner class GalleryHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var drawableId: String? = null


        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

        }

        fun bindItem(imageItem: String,activity: Activity) {
            this.drawableId = imageItem
            val inflated = view

            val galleryImageView = inflated.findViewById<ImageView>(R.id.GalleryImageView)
            Log.d("GalleryAdapter","Image URI $drawableId")

            Picasso.get().load(Uri.parse(drawableId)).into(galleryImageView)
            galleryImageView.setOnClickListener {
                val intent = Intent(activity, ImageDisplayActivity::class.java)
                intent.putExtra("imgUri", drawableId)
                activity.startActivity(intent)
            }
            //galleryImageView.setImageResource(drawableId!!)

        }
    }


}