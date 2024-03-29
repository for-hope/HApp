package com.hdarha.happ.fragments


import android.app.Activity
import android.app.Dialog
import android.content.res.Resources
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.TextView
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
import com.hdarha.happ.objects.Voice
import com.hdarha.happ.other.OnVoiceCallBack
import com.hdarha.happ.other.checkCache
import com.hdarha.happ.other.manageMediaPlayer
import pl.droidsonroids.gif.GifDrawable


class BottomSheet(listener: OnDialogComplete) : BottomSheetDialogFragment(),
    RecyclerAdapter.OnItemClick, OnVoiceCallBack, RecyclerAdapter.OnVoiceClick {
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    var bi: LayoutBottomSheetBinding? = null

    private var mCallback = listener
    private var mView: View? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var soundsList = mutableListOf<Voice>()
    private lateinit var mPlayer: MediaPlayer
    private var mAdapter: RecyclerAdapter? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val bottomSheet =
            super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        //mPlayer = MediaPlayer()
        //inflating layout
        val view =
            View.inflate(
                context,
                R.layout.layout_bottom_sheet, null
            )
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
        checkCache(this.activity as Activity, this)

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

        bi!!.bottomSheetPB.isIndeterminate = true
        bi!!.bottomSheetPB.visibility = View.VISIBLE
        bi!!.progessLayout.visibility = View.VISIBLE

        bi!!.activityBtn.setOnClickListener {
            Toast.makeText(
                context,
                "Edit button clicked",
                Toast.LENGTH_SHORT
            ).show()
        }

        //aap bar edit button clicked
        bi!!.rndBtn.setOnClickListener {
            if (soundsList.isNotEmpty()) {
                val rndSound = soundsList.random()
                val position = soundsList.indexOf(rndSound)
                mCallback.onComplete(rndSound, position)
                dismiss()
            } else {
                Toast.makeText(this.context, "Loading sounds..", Toast.LENGTH_SHORT).show()
            }
        }

        //aap bar more button clicked
        bi!!.cancelBtnBs.setOnClickListener {
            dismiss()
        }


        //hiding app bar at the start
        hideAppBar(bi!!.appBarLayout)


        return bottomSheet
    }

    override fun onResume() {
        super.onResume()
        mPlayer = MediaPlayer()
    }

    override fun onStart() {
        super.onStart()
        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        //mPlayer = MediaPlayer()
    }

    override fun onStop() {
        super.onStop()
        mPlayer.release()
    }

    override fun onPause() {
        super.onPause()
        if (mPlayer.isPlaying) {
            mPlayer.stop()
        }
        mPlayer.reset()
    }

    private fun hideAppBar(view: View) {
        val params = view.layoutParams
        params.height = 0
        view.layoutParams = params
        val fab = mView?.findViewById<FloatingActionButton>(R.id.fab_bottom_sheet)
        fab?.visibility = View.GONE
    }

    private fun showView(view: View, size: Int) {
        val params = view.layoutParams
        params.height = size
        view.layoutParams = params
        val fab = mView?.findViewById<FloatingActionButton>(R.id.fab_bottom_sheet)
        fab?.visibility = View.VISIBLE

    }


    private val actionBarSize: Int
        get() {
            val array =
                context!!.theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
            return array.getDimension(0, 0f).toInt()
        }

    override fun onClick(value: Voice?, key: Int) {
        //Toast.makeText(this.context, value.caption, Toast.LENGTH_SHORT).show()
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

    override fun onVoicesRetrieved(voices: ArrayList<Voice>, isCache: Boolean) {
        if (context != null) {
            val v = mView!!
            val recyclerView = v.findViewById<RecyclerView>(R.id.rec_view)
            recyclerView.visibility = View.VISIBLE
            val linearLayoutManager = LinearLayoutManager(context)
            val adapter = RecyclerAdapter(voices, false, this, this)
            recyclerView.layoutManager = linearLayoutManager
            recyclerView.adapter = adapter
            mAdapter = adapter
            bi!!.bottomSheetPB.visibility = View.GONE
            bi!!.progessLayout.visibility = View.GONE
            soundsList = voices.toMutableList()
            adapter.notifyItemInserted(voices.size - 1)
        }
    }

    override fun onPlay(title: TextView, mGifDrawable: GifDrawable, pathStr: String) {
        manageMediaPlayer(title, mGifDrawable, pathStr, mAdapter, mPlayer)
    }


}

interface OnDialogComplete {
    fun onComplete(value: Voice?, key: Int)
}