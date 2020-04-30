package com.hdarha.happ.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.hdarha.happ.R


data class ImgItemsAdapter(var imgList:List<Int>, var activity: Activity) : BaseAdapter(){

    override fun getItem(position: Int): Any {
        return imgList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return imgList.size
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val mViewHolder: ViewHolder? = null


        val view: View = View.inflate(activity,
            R.layout.layout_gallery_image_holder,null)

        val ivGalleryImage = view.findViewById<ImageView>(R.id.GalleryImageView) as ImageView





       ivGalleryImage.setImageResource(imgList[position])



        return view
    }

}