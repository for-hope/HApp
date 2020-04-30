package com.hdarha.happ.fragments


import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hdarha.happ.R
import com.hdarha.happ.adapters.UploadItemsAdapter
import com.hdarha.happ.objects.Upload
import kotlinx.android.synthetic.main.fragment_history.*


class HistoryFragment : Fragment() {
    private val photosCheckList:ArrayList<String> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mAppCompatActivity = activity as AppCompatActivity
        mAppCompatActivity.setSupportActionBar(view!!.findViewById(R.id.uploadsToolbar))
        mAppCompatActivity.supportActionBar?.title = "History"
        showCustomUI()
        val mSwipeToRefresh: SwipeRefreshLayout = view!!.findViewById(R.id.swipeToRefresh)

        mSwipeToRefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorPrimary
            )
        )
        mSwipeToRefresh.setColorSchemeColors(Color.WHITE)

        val mRecyclerAdapter: RecyclerView = view!!.findViewById(R.id.imgItemsRecyclerView)
        val uploadsList: ArrayList<Upload> = ArrayList()
        //val drawablesList: ArrayList<Int> = ArrayList()
        val linearLayoutManager = LinearLayoutManager(context)
        /////////

        val photoDatesList = getPhotoDatesList()
        //val photosCheckList:ArrayList<String> = arrayListOf()
        for(date in photoDatesList) {
            val drawablesList: ArrayList<String> = ArrayList(getPhotosList(date))
            if (drawablesList.isNotEmpty()) {
            val upload = Upload(date, drawablesList)
            uploadsList.add(upload)
            }
        }


        val adapter =
            UploadItemsAdapter(
                uploadsList,
                activity as Activity
            )
        mRecyclerAdapter.layoutManager = linearLayoutManager
        mRecyclerAdapter.adapter = adapter
        adapter.notifyDataSetChanged()
        swipeToRefresh.setOnRefreshListener {
            adapter.notifyDataSetChanged()
            swipeToRefresh.isRefreshing = false
        }

    }
    private fun getPhotosList(date:String): MutableSet<String> {
        val prefValue = "GalleryPref"
        val sharedPref: SharedPreferences =
            activity!!.getSharedPreferences(prefValue, Context.MODE_PRIVATE)
        var photosList: MutableSet<String>? = sharedPref.getStringSet(date, null)
        if (photosList == null) {
            photosList = mutableSetOf()
        }

        for (photo in photosList) {
            if (photosCheckList.contains(photo)) {
                photosList.remove(photo)
            } else {
                Log.d("History",photo)
                photosCheckList.add(photo)
            }
        }

        return photosList
    }

    private fun getPhotoDatesList(): MutableSet<String> {
        val prefValue = "GalleryPref"
        val keyValue = "PhotoDates"
        val sharedPref: SharedPreferences =
            activity!!.getSharedPreferences(prefValue, Context.MODE_PRIVATE)
        var photoDatesList: MutableSet<String>? = sharedPref.getStringSet(keyValue, null)
        if (photoDatesList == null) {
            photoDatesList = mutableSetOf()
        }
        photoDatesList = photoDatesList.reversed().toMutableSet()
        return photoDatesList
    }

    private fun showCustomUI() {

        val decorView = activity?.window?.decorView
        decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_VISIBLE)

        activity?.window?.statusBarColor =
            ContextCompat.getColor(this.context!!,
                R.color.colorPrimary
            )

    }
}