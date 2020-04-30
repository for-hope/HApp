package com.hdarha.happ.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.hdarha.happ.R
import com.hdarha.happ.other.ScreenHelper
import com.hdarha.happ.objects.Screen
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


var TAG = "ScreenFragment"

class ScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState:
        Bundle?
    ): View? {

        // Creates the view controlled by the fragment

        val view = inflater.inflate(R.layout.fragment_movie, container, false)
        val bgImageView = view.findViewById<ImageView>(R.id.bg_img)


        // Retrieve and display the movie data from the Bundle
        val args = arguments

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

        return view
    }

    companion object {

        // Method for creating new instances of the fragment
        fun newInstance(screen: Screen): ScreenFragment {

            // Store the movie data in a Bundle object
            val args = Bundle()
            args.putString(ScreenHelper.KEY_BG_URI, screen.bgUri)

            // Create a new MovieFragment and set the Bundle as the arguments
            // to be retrieved and displayed when the view is created
            val fragment = ScreenFragment()
            fragment.arguments = args
            return fragment
        }
    }

}
