package com.hdarha.happ.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.github.ksoichiro.android.observablescrollview.ScrollState
import com.hdarha.happ.R
import com.hdarha.happ.activities.ParallaxHeaderActivity

class SettingsFragment : ParallaxHeaderActivity() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setContentView(view!!.findViewById(R.id.frgFrameLayout),HeaderFragment(),ContentFragment())
        showCustomUI()

    }
    private fun showCustomUI() {
        val decorView = activity?.window?.decorView
        decorView?.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        activity?.window?.statusBarColor =
            ContextCompat.getColor(this.context!!,
                R.color.colorTransparent
            )
    }
}