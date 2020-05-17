package com.hdarha.happ.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.hdarha.happ.R
import com.hdarha.happ.objects.Screen
import com.hdarha.happ.other.ScreenHelper


class ScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState:
        Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_movie, container, false)

        val screenHolder = view.findViewById<FrameLayout>(R.id.mScreenHolder)

        val bannerImageView = view.findViewById<ImageView>(R.id.imageViewBanner)


        val args = arguments

        val gradiantBG = args!!.getString(ScreenHelper.KEY_POSTER_URI)
        val resources = context!!.resources
        val gDrawable = resources.getIdentifier(gradiantBG, "drawable", context!!.packageName)
        screenHolder.background = resources.getDrawable(gDrawable, resources.newTheme())
        val bannerResourceId = resources.getIdentifier(
            args.getString(ScreenHelper.KEY_BANNER_URI),
            "drawable",
            context!!.packageName
        )
        val bannerResource = resources.getDrawable(bannerResourceId, resources.newTheme())
        bannerImageView.setImageDrawable(bannerResource)


        return view
    }

    companion object {


        fun newInstance(screen: Screen): ScreenFragment {


            val args = Bundle()
            args.putString(ScreenHelper.KEY_BG_URI, screen.bgUri)
            args.putString(ScreenHelper.KEY_POSTER_URI, screen.poserUri)
            args.putString(ScreenHelper.KEY_BANNER_URI, screen.banner)
            args.putString(ScreenHelper.KEY_TITLE_STRING, screen.title)
            args.putString(ScreenHelper.KEY_OVERVIEW_STRING, screen.overview)

            val fragment = ScreenFragment()
            fragment.arguments = args
            return fragment
        }
    }

}
