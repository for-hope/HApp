package com.hdarha.happ.fragments


import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hdarha.happ.R
import com.hdarha.happ.adapters.UploadItemsAdapter
import com.hdarha.happ.objects.Upload
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class HistoryFragment : Fragment() {
    private val photosCheckList: ArrayList<String> = arrayListOf()
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



        historyProgressBar.isIndeterminate = true
        historyProgressBar.visibility = View.VISIBLE
        setupRecyclerView()

        swipeToRefresh.setRefreshListener {
            setupRecyclerView()
        }

    }

    private fun setupRecyclerView() {

        GlobalScope.launch {
            activity!!.runOnUiThread {
                val mRecyclerAdapter: RecyclerView = view!!.findViewById(R.id.imgItemsRecyclerView)
                mRecyclerAdapter.visibility = View.GONE
                val uploadsList: ArrayList<Upload> = ArrayList()
                //val drawablesList: ArrayList<Int> = ArrayList()
                val linearLayoutManager = LinearLayoutManager(context)
                /////////

//        val photoDatesList = getPhotoDatesList()
                val map = getStoredMap()
                val photoDatesList = getDates(map)

                //val photosCheckList:ArrayList<String> = arrayListOf()
                for (date in photoDatesList) {
                    val drawablesList: ArrayList<String> = getPhotos(map, date)
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
                mRecyclerAdapter.visibility = View.VISIBLE
                adapter.notifyDataSetChanged()
                historyProgressBar.visibility = View.GONE
                swipeToRefresh.setRefreshing(false)
            }
        }
    }

    private fun getDates(map: HashMap<String, String>): ArrayList<String> {
        val datesList = arrayListOf<String>()
        map.forEach {
            if (!datesList.contains(it.value)) {
                datesList.add(it.value)
            }
        }
        return datesList
    }

    private fun getPhotos(map: HashMap<String, String>, date: String): ArrayList<String> {
        val photosList = arrayListOf<String>()
        map.forEach {
            if (it.value == date) {
                photosList.add(it.key)
            }
        }
        return photosList
    }

    private fun getStoredMap(): HashMap<String, String> {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        val currentDate = sdf.format(Date())
        val prefValue = "history"
        val keyValue = "map"
        val gson = Gson()
        val sharedPreferences =
            this.activity!!.getSharedPreferences(prefValue, Context.MODE_PRIVATE)

        val hashString = sharedPreferences.getString(keyValue, "")
        val type: Type = object : TypeToken<HashMap<String, String>>() {}.type
        var storedHashMap: HashMap<String, String> = hashMapOf()
        if (hashString != "") {
            storedHashMap = gson.fromJson(hashString, type) as HashMap<String, String>
        }
        return storedHashMap
    }


    private fun getPhotosList(date: String): MutableSet<String> {
        val prefValue = "GalleryPref"
        val sharedPref: SharedPreferences =
            activity!!.getSharedPreferences(prefValue, Context.MODE_PRIVATE)
        Log.d("DATE", date)
        var photosList: MutableSet<String>? = sharedPref.getStringSet(date, null)
        if (photosList == null) {
            photosList = mutableSetOf()
        }

        for (photo in photosList) {
            Log.d("PHOTO", photosList.size.toString())
            if (photosCheckList.contains(photo)) {
                photosList.remove(photo)
            } else {
                Log.d("History", photo)
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
            ContextCompat.getColor(
                this.context!!,
                R.color.colorPrimary
            )


        this.activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        this.activity?.window?.statusBarColor =
            ContextCompat.getColor(this.context!!, R.color.colorTitle)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.activity?.window?.decorView?.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        };


    }
}