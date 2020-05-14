package com.hdarha.happ.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.hdarha.happ.R
import com.hdarha.happ.other.ScreenHelper
import com.hdarha.happ.objects.Screen
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_movie.*


var TAG = "ScreenFragment"

class ScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState:
        Bundle?
    ): View? {



        val view = inflater.inflate(R.layout.fragment_movie, container, false)
        val bgImageView = view.findViewById<ImageView>(R.id.bg_img)
        val screenHolder = view.findViewById<FrameLayout>(R.id.mScreenHolder)
        val titleTextView = view.findViewById<TextView>(R.id.textViewTopBanner)
        val overviewTextView = view.findViewById<TextView>(R.id.textViewBottomBanner)
        val bannerImageView = view.findViewById<ImageView>(R.id.imageViewBanner)


        val args = arguments

        val gradiantBG = args!!.getString(ScreenHelper.KEY_POSTER_URI)
        val resources = context!!.resources
        val gDrawable = resources.getIdentifier(gradiantBG,"drawable",context!!.packageName)
        screenHolder.background = resources.getDrawable(gDrawable,resources.newTheme())
        titleTextView.text = args.getString(ScreenHelper.KEY_TITLE_STRING)
        overviewTextView.text = args.getString(ScreenHelper.KEY_OVERVIEW_STRING)
        val bannerResourceId = resources.getIdentifier(args.getString(ScreenHelper.KEY_BANNER_URI),"drawable",context!!.packageName)
        val bannerResource = resources.getDrawable(bannerResourceId,resources.newTheme())
        bannerImageView.setImageDrawable(bannerResource)


        val bgUri = args.getString(ScreenHelper.KEY_BG_URI)
        if ( bgUri == "v1") {
            overviewTextView.setTextColor(Color.CYAN)
        }

        if (bgUri == "") {
            bgImageView.visibility = View.GONE
        } else {
            Picasso.get()
                .load(
                    resources.getIdentifier(
                        args!!.getString(ScreenHelper.KEY_BG_URI),
                        "drawable",
                        activity!!.packageName
                    )
                )
                .into(bgImageView, object : Callback {
                    override fun onSuccess() {
                        Log.d(TAG, "success")
                    }

                    override fun onError(e: Exception?) {
                        Log.d(TAG, "error")
                    }
                })
        }
        return view
    }

    companion object {


        fun newInstance(screen: Screen): ScreenFragment {


            val args = Bundle()
            args.putString(ScreenHelper.KEY_BG_URI, screen.bgUri)
            args.putString(ScreenHelper.KEY_POSTER_URI, screen.poserUri)
            args.putString(ScreenHelper.KEY_BANNER_URI, screen.banner)
            args.putString(ScreenHelper.KEY_TITLE_STRING,screen.title)
            args.putString(ScreenHelper.KEY_OVERVIEW_STRING,screen.overview)

            val fragment = ScreenFragment()
            fragment.arguments = args
            return fragment
        }
    }

}
