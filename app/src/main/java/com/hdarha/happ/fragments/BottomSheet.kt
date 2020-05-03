package com.hdarha.happ.fragments


import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hdarha.happ.R
import com.hdarha.happ.adapters.RecyclerAdapter
import com.hdarha.happ.databinding.LayoutBottomSheetBinding
import com.hdarha.happ.objects.Sound
import com.hdarha.happ.objects.Voice
import com.hdarha.happ.other.RetrofitClientInstance
import kotlinx.android.synthetic.main.activity_sound_library.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BottomSheet(listener: OnDialogComplete) : BottomSheetDialogFragment(),
    RecyclerAdapter.OnItemClick {
    var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    var bi: LayoutBottomSheetBinding? = null
    var selectedSound = ""
    private var mCallback = listener
    private var mView: View? = null
    private lateinit var adapter: RecyclerAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var soundsList = mutableListOf<Voice>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val bottomSheet =
            super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        //inflating layout
        val view =
            View.inflate(context,
                R.layout.layout_bottom_sheet, null)
        mView = view
        linearLayoutManager = LinearLayoutManager(view.context)
        //binding views to data binding.
        bi = DataBindingUtil.bind(view)

        //setting layout with bottom sheet
        bottomSheet.setContentView(view)
        bottomSheetBehavior =
            BottomSheetBehavior.from(view.parent as View)
        val recyclerView = view!!.findViewById<RecyclerView>(R.id.rec_view)
        recyclerView.visibility = View.GONE
        getSounds(view)

        //setting Peek at the 16:9 ratio keyline of its parent.
        bottomSheetBehavior!!.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO


        //setting max height of bottom sheet
        bi!!.extraSpace.minimumHeight = Resources.getSystem().displayMetrics.heightPixels / 2
        bottomSheetBehavior!!.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(view: View, i: Int) {
                if (BottomSheetBehavior.STATE_EXPANDED == i) {
                    showView(bi!!.appBarLayout, actionBarSize)
                    //hideAppBar(bi!!.profileLayout)
                }
                if (BottomSheetBehavior.STATE_COLLAPSED == i) {
                    hideAppBar(bi!!.appBarLayout)
                    //showView(bi!!.profileLayout, actionBarSize)
                }
                if (BottomSheetBehavior.STATE_HIDDEN == i) {
                    dismiss()
                }
            }

            override fun onSlide(view: View, v: Float) {}
        })

        //aap bar cancel button clicked

        bi!!.activityBtn.setOnClickListener {
            Toast.makeText(
                context,
                "Edit button clicked",
                Toast.LENGTH_SHORT
            ).show()
        }

        //aap bar edit button clicked
        bi!!.rndBtn.setOnClickListener {
            val rndSound = soundsList.random()
            mCallback.onComplete(rndSound.caption, rndSound.name)
            dismiss()
        }

        //aap bar more button clicked
        bi!!.cancelBtnBs.setOnClickListener {
            dismiss()
        }


        //hiding app bar at the start
        hideAppBar(bi!!.appBarLayout)


        return bottomSheet
    }

    override fun onStart() {
        super.onStart()
        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun hideAppBar(view: View) {
        val params = view.layoutParams
        params.height = 0
        view.layoutParams = params
    }

    private fun showView(view: View, size: Int) {
        val params = view.layoutParams
        params.height = size
        view.layoutParams = params
    }

    private fun getSounds(view:View){
        val retrofitClient = RetrofitClientInstance()
        val service: RetrofitClientInstance.VoiceService = retrofitClient.retrofitInstance!!.create(
            RetrofitClientInstance.VoiceService::class.java)

        val voices = service.listVoices()

        voices?.enqueue(object : Callback<List<Voice>> {

            override fun onResponse(call: Call<List<Voice>>, response: Response<List<Voice>>) {
                Log.d("SoundsList",response.body()!![0].caption)
                val mVoices = response.body()
                voicesLoaded(ArrayList(mVoices!!),view)
            }
            override fun onFailure(call: Call<List<Voice>>, t: Throwable) {
                Log.d("SoundsList", t.localizedMessage?.toString()!!)
                Toast.makeText(
                    context,
                    "Something went wrong...Please try later!",
                    Toast.LENGTH_SHORT
                ).show()
            }



        })
    }

    private fun voicesLoaded(voices : ArrayList<Voice>,v: View) {
        val recyclerView = v.findViewById<RecyclerView>(R.id.rec_view)
        recyclerView.visibility = View.VISIBLE
        val linearLayoutManager = LinearLayoutManager(context)
        val adapter = RecyclerAdapter(context!!, voices,true,this)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        soundsList = voices.toMutableList()
        adapter.notifyItemInserted(voices.size - 1)

    }





    private val actionBarSize: Int
        get() {
            val array =
                context!!.theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
            return array.getDimension(0, 0f).toInt()
        }

    override fun onClick(value: String?, key:String) {
        Toast.makeText(this.context, value, Toast.LENGTH_SHORT).show()
        if (bottomSheetBehavior!!.state != BottomSheetBehavior.STATE_EXPANDED) {
            dismiss()
            mCallback.onComplete(value, key)
        } else {

            val fab = mView?.findViewById<FloatingActionButton>(R.id.fab_bottom_sheet)
            fab?.setOnClickListener {
                dismiss()
                mCallback.onComplete(value, key)
            }
        }


    }


}

interface OnDialogComplete {
    fun onComplete(value: String?, key: String)
}