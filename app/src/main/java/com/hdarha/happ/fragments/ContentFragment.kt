package com.hdarha.happ.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hdarha.happ.R
import com.hdarha.happ.activities.MainActivity
import com.hdarha.happ.adapters.SettingsAdapter
import com.hdarha.happ.objects.SettingItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_setting_items.*
import kotlinx.android.synthetic.main.view_profile_bottom.*
import kotlinx.android.synthetic.main.view_profile_header.*

private lateinit var auth: FirebaseAuth

class ContentFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_content, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //activity as AppCompatActivity

        auth = Firebase.auth
        setupSettings()


        signOutButton.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun setupSettings() {
        setting1.setOnClickListener {
            Toast.makeText(context,"Clicked Setting 1", Toast.LENGTH_SHORT).show()
        }
        setting2.setOnClickListener {
            Toast.makeText(context,"Clicked Setting 2", Toast.LENGTH_SHORT).show()
        }
        switchPushNotifications.setOnCheckedChangeListener { compoundButton, b ->
            var answer = "off"
            if (b) {
                answer = "on"
            }
            Toast.makeText(context,"Push notifications turned $answer", Toast.LENGTH_SHORT).show()
        }
        imageFacebook.setOnClickListener {
            Toast.makeText(context,"Clicked FB", Toast.LENGTH_SHORT).show()
        }
        setting4.setOnClickListener {
            Toast.makeText(context,"Clicked Setting 4", Toast.LENGTH_SHORT).show()
        }
        setting5.setOnClickListener {
            Toast.makeText(context,"Clicked Setting 5", Toast.LENGTH_SHORT).show()
        }
        setting6.setOnClickListener {
            Toast.makeText(context,"Clicked Setting 6", Toast.LENGTH_SHORT).show()
        }

    }
    private fun showConfirmationDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context!!)

        builder.setTitle("Sign Out")
        builder.setMessage("Are you sure you want to sign out ?")

        builder.setPositiveButton("Sign out") { dialog, which -> // Do nothing but close the dialog
            auth.signOut()
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        builder.setNegativeButton("No") { dialog, which -> // Do nothing
            dialog.dismiss()
        }

        val alert: AlertDialog = builder.create()
        alert.show()
    }


}