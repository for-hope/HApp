package com.hdarha.happ.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hdarha.happ.R
import com.squareup.picasso.Picasso
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
            Picasso.get().load(currentUser.photoUrl).into(profileImage)
            displayNameTextView.text = currentUser.displayName
        }
    }

}