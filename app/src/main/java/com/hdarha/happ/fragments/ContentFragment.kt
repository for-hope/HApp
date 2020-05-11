package com.hdarha.happ.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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


        val profileRecyclerView = view!!.findViewById<RecyclerView>(R.id.profile_recyclerview)
        val linearLayoutManager = LinearLayoutManager(context)


        val adapter = SettingsAdapter(
            getSettingsList(),
            activity as Activity
        )
        profileRecyclerView.layoutManager = linearLayoutManager
        profileRecyclerView.adapter = adapter
        profileRecyclerView.isNestedScrollingEnabled = false
        adapter.notifyDataSetChanged()



        signOutButton.setOnClickListener {
            showConfirmationDialog()
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
    private fun getSettingsList(): ArrayList<SettingItem> {
        val mArrayList = arrayListOf<SettingItem>()

        for (x in 0..6) {
            when (x) {
                0 -> {
                    val intArray = arrayListOf<Int>()
                    val btnDrawable =
                        R.drawable.ic_keyboard_arrow_right_gray_24dp
                    val title = "User Manual"
                    val titleDrawable =
                        R.drawable.ic_list_black_24dp
                    intArray.add(btnDrawable)
                    val setting = SettingItem(
                        title,
                        titleDrawable,
                        intArray, null
                    )
                    mArrayList.add(setting)
                }
                1 -> {
                    val intArray = arrayListOf<Int>()
                    val btnDrawable =
                        R.drawable.ic_keyboard_arrow_right_gray_24dp
                    val title = "Feedback"
                    val titleDrawable = R.drawable.ic_survey
                    intArray.add(btnDrawable)
                    val setting = SettingItem(
                        title,
                        titleDrawable,
                        intArray, null
                    )
                    mArrayList.add(setting)
                }
                2 -> {
                    val viewArray = arrayListOf<View>()
                    val switchView = SwitchMaterial(activity as Activity)
                    switchView.text = ""
                    viewArray.add(switchView)
                    val intArray = arrayListOf<Int>()
                    val btnDrawable =
                        R.drawable.ic_keyboard_arrow_right_gray_24dp
                    val title = "Push Notification"
                    val titleDrawable =
                        R.drawable.ic_notifications_none_black_24dp
                    intArray.add(btnDrawable)
                    val setting = SettingItem(
                        title,
                        titleDrawable,
                        null, viewArray
                    )
                    mArrayList.add(setting)
                }
                3 -> {
                    val intArray = arrayListOf<Int>()
                    val fbIcon =
                        R.drawable.ic_facebook
                    val googleIcon = R.drawable.ic_instagram
                    val twitterIc = R.drawable.ic_twitter
                    val emailIcon = R.drawable.ic_arroba
                    val title = "Contact us"
                    val titleDrawable =
                        R.drawable.ic_headset_mic_black_24dp

                    intArray.add(fbIcon)
                    intArray.add(googleIcon)
                    intArray.add(twitterIc)
                    intArray.add(emailIcon)
                    Log.d("Profile", intArray.toString())
                    val setting = SettingItem(
                        title,
                        titleDrawable,
                        intArray, null
                    )

                    mArrayList.add(setting)
                }
                4 -> {
                    val intArray = arrayListOf<Int>()
                    val btnDrawable =
                        R.drawable.ic_keyboard_arrow_right_gray_24dp
                    val title = "Erase all content"
                    val titleDrawable = R.drawable.ic_eraser
                    intArray.add(btnDrawable)
                    val setting = SettingItem(
                        title,
                        titleDrawable,
                        null, null
                    )
                    mArrayList.add(setting)
                }
                5 -> {
                    val intArray = arrayListOf<Int>()
                    val btnDrawable =
                        R.drawable.ic_keyboard_arrow_right_gray_24dp
                    val title = "Terms of service"
                    val titleDrawable =
                        R.drawable.ic_lightbulb_outline_black_24dp
                    intArray.add(btnDrawable)
                    val setting = SettingItem(
                        title,
                        titleDrawable,
                        intArray, null
                    )
                    mArrayList.add(setting)
                }
                6 -> {
                    val intArray = arrayListOf<Int>()
                    val btnDrawable =
                        R.drawable.ic_keyboard_arrow_right_gray_24dp
                    val title = "About us"
                    val titleDrawable =
                        R.drawable.ic_info_outline_black_24dp
                    intArray.add(btnDrawable)
                    val setting = SettingItem(
                        title,
                        titleDrawable,
                        intArray, null
                    )
                    mArrayList.add(setting)
                }

            }

        }

        return mArrayList
    }

}