package com.hdarha.happ.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hdarha.happ.R
import com.hdarha.happ.other.PREF_POINTS
import kotlinx.android.synthetic.main.fragment_header.*

private lateinit var auth: FirebaseAuth
class HeaderFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_header, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        auth = Firebase.auth

        setupProfileInfo(auth.currentUser)
    }
    private fun setupProfileInfo(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val profileImage = view?.findViewById<ImageView>(R.id.profileImageView)
            val url = currentUser.photoUrl
            if (profileImage != null) {
                Glide.with(context!!).load(url).into(profileImage)
            } else {
                Toast.makeText(context!!,"Error loading profile pic",Toast.LENGTH_SHORT).show()
            }
            val mPref = activity!!.getSharedPreferences(PREF_POINTS, Context.MODE_PRIVATE)
            val credit = mPref.getInt("credit",0)
            val creation = mPref.getInt("creation",0)
            val creditTextView = view?.findViewById<TextView>(R.id.textViewCredit)
            val creationTextView = view?.findViewById<TextView>(R.id.textViewCreations)
            creditTextView?.text = credit.toString()
            creationTextView?.text = creation.toString()
            var providerId = ""
            for (data in currentUser.providerData) {
                providerId = data.providerId
                if (data.providerId == "google.com") {
                    break
                } else if (data.providerId == "facebook.com") {
                    break
                }
            }
            var img = activity!!.getDrawable(R.drawable.ic_facebook_brand)
            if (providerId == "google.com") {
                img = activity!!.getDrawable(R.drawable.ic_google)
            }
            displayNameTextView.setCompoundDrawablesWithIntrinsicBounds(img,null,null,null)
            displayNameTextView.text = currentUser.displayName
        }
    }

}