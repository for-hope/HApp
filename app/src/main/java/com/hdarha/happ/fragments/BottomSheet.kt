package com.hdarha.happ.fragments


import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
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


class BottomSheet(listener: OnDialogComplete) : BottomSheetDialogFragment(),
    RecyclerAdapter.OnItemClick {
    var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    var bi: LayoutBottomSheetBinding? = null
    var selectedSound = ""
    private var mCallback = listener
    private var mView: View? = null
    private lateinit var adapter: RecyclerAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val soundsList = mutableListOf<Sound>()

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

        initSounds(view)

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
            mCallback.onComplete(soundsList.random().title)
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

    private fun initSounds(view: View) {
        val s1 = Sound(
            1,
            "Meme number one",
            "memer1",
            "2:00",
            "www.google.com"
        )
        val s2 = Sound(
            2,
            "Meme number two",
            "memer2",
            "5:00",
            "www.google.com"
        )
        val s3 = Sound(
            3,
            "Meme number three",
            "memer3",
            "1:00",
            "www.google.com"
        )
        val s4 = Sound(
            4,
            "Meme number one",
            "memer5",
            "2:00",
            "www.google.com"
        )
        val s5 = Sound(
            5,
            "Meme number two",
            "meme02",
            "5:00",
            "www.google.com"
        )
        val s6 = Sound(
            6,
            "Meme number three",
            "memer8",
            "1:00",
            "www.google.com"
        )

        soundsList.add(s1)
        soundsList.add(s2)
        soundsList.add(s3)
        soundsList.add(s4)
        soundsList.add(s5)
        soundsList.add(s6)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rec_view)


        adapter = RecyclerAdapter(
            this.context!!,
            soundsList,
            this
        )
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        adapter.notifyItemInserted(soundsList.size - 1)


    }


    private val actionBarSize: Int
        get() {
            val array =
                context!!.theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
            return array.getDimension(0, 0f).toInt()
        }

    override fun onClick(value: String?) {
        Toast.makeText(this.context, value, Toast.LENGTH_SHORT).show()
        if (bottomSheetBehavior!!.state != BottomSheetBehavior.STATE_EXPANDED) {
            dismiss()
            mCallback.onComplete(value)
        } else {

            val fab = mView?.findViewById<FloatingActionButton>(R.id.fab_bottom_sheet)
            fab?.setOnClickListener {
                dismiss()
                mCallback.onComplete(value)
            }
        }


    }


}

interface OnDialogComplete {
    fun onComplete(value: String?)
}