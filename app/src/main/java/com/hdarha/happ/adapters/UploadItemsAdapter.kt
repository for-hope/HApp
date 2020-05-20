package com.hdarha.happ.adapters

import android.app.Activity
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hdarha.happ.R
import com.hdarha.happ.objects.Upload
import com.hdarha.happ.other.Utility.calculateNoOfColumns
import com.hdarha.happ.other.inflate
import java.text.SimpleDateFormat
import java.util.*


class UploadItemsAdapter(private val uploads: ArrayList<Upload>, private val activity: Activity) :
    RecyclerView.Adapter<UploadItemsAdapter.UploadsHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UploadsHolder {
        val inflatedView = parent.inflate(R.layout.view_img_item, false)
        return UploadsHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return uploads.size
    }

    override fun onBindViewHolder(holder: UploadsHolder, position: Int) {
        val uploadItem = uploads[position]
        holder.bindItem(uploadItem, activity)
    }


    inner class UploadsHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var upload: Upload? = null


        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

        }

        fun bindItem(upload: Upload, activity: Activity) {
            this.upload = upload
            val inflated = view

            val dateTextView = inflated.findViewById<TextView>(R.id.dateTextView)
            val mRecyclerView = inflated.findViewById<RecyclerView>(R.id.imgGridViewRec)
            val adapter = GalleryAdapter(
                upload.imgDrawableList,
                activity
            )
            //val widthInPixel = convertDpToPixel(100f,activity)
            val dp =
                (activity.resources.getDimension(R.dimen._75sdp) / activity.resources.displayMetrics.density)
            val mNoOfColumns = calculateNoOfColumns(activity.applicationContext, dp)
            val gridViewManager = GridLayoutManager(activity, mNoOfColumns)

            mRecyclerView.layoutManager = gridViewManager
            mRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            Log.d("UploadAdapter", upload.imgDrawableList.toString())

            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
            val parsedDate = formatter.parse(upload.date)
            val displaySdf = SimpleDateFormat("dd MMM", Locale.getDefault())
            var dateStr = displaySdf.format(parsedDate!!)

            if (DateUtils.isToday(parsedDate.time)) {
                dateStr = "Today"
            }
            dateTextView.text = dateStr
        }
    }
}