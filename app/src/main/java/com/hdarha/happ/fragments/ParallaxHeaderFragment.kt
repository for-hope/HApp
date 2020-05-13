package com.hdarha.happ.fragments

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks
import com.github.ksoichiro.android.observablescrollview.ScrollState
import com.hdarha.happ.R
import me.everything.android.ui.overscroll.IOverScrollDecor
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator
import me.everything.android.ui.overscroll.adapters.ScrollViewOverScrollDecorAdapter
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

abstract class ParallaxHeaderFragment : Fragment(),
    ObservableScrollViewCallbacks {
    private var headerView: LinearLayout? = null
    private var headerHeight = 0
    private var minimumHeaderHeight = 0
    private var mContentView: LinearLayout? = null
    private lateinit var mScrollView: ObservableScrollView
    private var lastScroll = 0
    protected fun setContentView(
        view: View,
        header: Fragment,
        content: Fragment
    ) {


        //super.setContentView(layout);
        val mContext = context
        val rootView = view as ViewGroup
        headerHeight = convertDpToPixel(
            250f,
            mContext
        ).toInt()
        minimumHeaderHeight =
            convertDpToPixel(
                200f,
                mContext
            ).toInt()
        val contentViewId = View.generateViewId()
        val contentView = LinearLayout(mContext!!)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        //params.setMargins(0,headerHeight,0,0)
        contentView.layoutParams = params
        contentView.orientation = LinearLayout.VERTICAL
        contentView.setPadding(0, headerHeight, 0, 0)
        contentView.id = contentViewId
        val scrollView = ObservableScrollView(mContext)

        val p =LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
        )

        scrollView.layoutParams = p
        scrollView.setScrollViewCallbacks(this)
        scrollView.isFillViewport = true
        scrollView.addView(contentView)
        scrollView.setScrollViewCallbacks(this)
        scrollView.isNestedScrollingEnabled =  false

        val decor: IOverScrollDecor = VerticalOverScrollBounceEffectDecorator(
            ScrollViewOverScrollDecorAdapter(scrollView)
        )
        decor.setOverScrollUpdateListener { decor1: IOverScrollDecor?, state: Int, offset: Float ->
            if (offset.toInt() > 0) {
                // 'view' is currently being over-scrolled from the top.
                update((-offset).toInt())
            } else if (offset.toInt() < 0) {
                Log.d("Offet", offset.toInt().toString())
                updateProfileLayout(offset.toInt() + lastScroll)
            }

        }

        rootView.addView(scrollView)
        addFragment(contentViewId, content)
        val headerViewId = View.generateViewId()
        headerView = LinearLayout(mContext)
        headerView!!.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            headerHeight
        )
        headerView!!.id = headerViewId
        rootView.addView(headerView)

        addFragment(headerViewId, header)
        mContentView = contentView
        mScrollView = scrollView
    }


    override fun onScrollChanged(
        scrollY: Int,
        firstScroll: Boolean,
        dragging: Boolean
    ) {
        update(scrollY)
    }

    override fun onUpOrCancelMotionEvent(scrollState: ScrollState?) {

    }

    override fun onDownMotionEvent() {}
    private fun addFragment(id: Int, fragment: Fragment) {
        val transaction =
            activity!!.supportFragmentManager.beginTransaction()
        transaction.replace(id, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun update(scrollY: Int) {
        if (scrollY > 0) {
            lastScroll = -scrollY
            updateProfileLayout(-scrollY)
        }
        if (scrollY < 0) {
            val defMaragin = convertDpToPixel(25f, context).toInt()
            if (defMaragin - scrollY > 0) {
                Log.d("Updatey",scrollY.toString())
                val profile = headerView!!.findViewById<LinearLayout>(R.id.profileLinearLayout)
                val layoutParams:FrameLayout.LayoutParams = profile.layoutParams as FrameLayout.LayoutParams
                val defaultOffset = min(defMaragin - scrollY, defMaragin)
                layoutParams.setMargins(0, -scrollY, 0, 0)
                profile.layoutParams = layoutParams
                profile.requestLayout()
                val h = max(headerHeight - scrollY, minimumHeaderHeight)
                headerView!!.layoutParams.height = h
                headerView!!.requestLayout()
            }

        }

    }

    private fun updateProfileLayout(offset: Int) {
        if (offset < 0) {
            //Log.d("updateProfile", offset.toString())
            val profile = headerView!!.findViewById<LinearLayout>(R.id.profileLinearLayout)

            val layoutParams:FrameLayout.LayoutParams = profile.layoutParams as FrameLayout.LayoutParams
            layoutParams.setMargins(0, offset, 0, 0)
            profile.layoutParams = layoutParams
            profile.requestLayout()
            val h = headerHeight + offset
            if (h > 0) {
            headerView!!.layoutParams.height = h
                headerView!!.visibility = View.VISIBLE
            } else {
                headerView!!.visibility = View.GONE
            }
            headerView!!.requestLayout()
        }
    }

    companion object {
        fun convertDpToPixel(dp: Float, context: Context?): Float {
            return dp * (context!!.resources
                .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }
    }
}