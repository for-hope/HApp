package com.hdarha.happ.activities

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks
import com.github.ksoichiro.android.observablescrollview.ScrollState
import com.hdarha.happ.R
import me.everything.android.ui.overscroll.IOverScrollDecor
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator
import me.everything.android.ui.overscroll.adapters.ScrollViewOverScrollDecorAdapter
import kotlin.math.max

abstract class ParallaxHeaderActivity : Fragment(),
    ObservableScrollViewCallbacks {
    private var headerView: LinearLayout? = null
    private var headerHeight = 0
    private var minimumHeaderHeight = 0
    private var mContentView : FrameLayout? = null
    protected fun setContentView(
        view: View,
        header: Fragment,
        content: Fragment
    ) {


        //super.setContentView(layout);
        val mContext = context
        val rootView = view as ViewGroup
        headerHeight = convertDpToPixel(
            300f,
            mContext
        ).toInt()
        minimumHeaderHeight =
            convertDpToPixel(
                260f,
                mContext
            ).toInt()
        val contentViewId = View.generateViewId()
        val contentView = FrameLayout(mContext!!)
        contentView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        contentView.setPadding(0, headerHeight, 0, 0)
        contentView.id = contentViewId
        val scrollView = ObservableScrollView(mContext)
        scrollView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        scrollView.setScrollViewCallbacks(this)
        scrollView.isFillViewport = true
        scrollView.addView(contentView)
        scrollView.setScrollViewCallbacks(this)

        val decor: IOverScrollDecor = VerticalOverScrollBounceEffectDecorator(
            ScrollViewOverScrollDecorAdapter(scrollView)
        )
        decor.setOverScrollUpdateListener { decor1: IOverScrollDecor?, state: Int, offset: Float ->
            if (offset > 0) {
                // 'view' is currently being over-scrolled from the top.
                update((-offset).toInt())
            } else if (offset < 0) {
                updateProfileLayout(offset.toInt())
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

        headerView!!.layoutParams.height =
            max(headerHeight - scrollY, minimumHeaderHeight)
        headerView!!.requestLayout()

    }

    private fun updateProfileLayout(offset:Int) {

        val profile = headerView!!.findViewById<LinearLayout>(R.id.profileLinearLayout)
        val layoutParams = FrameLayout.LayoutParams(profile.width,profile.height)
        layoutParams.setMargins(0,offset,0,0)
        profile.layoutParams = layoutParams
        profile.requestLayout()
        val h = minimumHeaderHeight + offset
        headerView!!.layoutParams.height = h
        //mContentView!!.setPadding(0, h, 0, 0)
        headerView!!.requestLayout()
        //mContentView!!.requestLayout()

    }

    companion object {
        fun convertDpToPixel(dp: Float, context: Context?): Float {
            return dp * (context!!.resources
                .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }
    }
}