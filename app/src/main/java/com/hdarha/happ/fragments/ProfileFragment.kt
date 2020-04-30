package com.hdarha.happ.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.hdarha.happ.R
import com.hdarha.happ.objects.SettingItem
import com.hdarha.happ.adapters.SettingsAdapter

class ProfileFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showCustomUI()
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
    private fun getSettingsList():ArrayList<SettingItem> {
        val mArrayList = arrayListOf<SettingItem>()

        for (x in 0..6) {
            when(x) {
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
                        intArray,null
                    )
                    mArrayList.add(setting)
                }
                1-> {
                    val intArray = arrayListOf<Int>()
                    val btnDrawable =
                        R.drawable.ic_keyboard_arrow_right_gray_24dp
                    val title = "Feedback"
                    val titleDrawable = R.drawable.ic_survey
                    intArray.add(btnDrawable)
                    val setting = SettingItem(
                        title,
                        titleDrawable,
                        intArray,null
                    )
                    mArrayList.add(setting)
                }
                2-> {
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
                        null,viewArray
                    )
                    mArrayList.add(setting)
                }
                3-> {
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
                    Log.d("Profile",intArray.toString())
                    val setting = SettingItem(
                        title,
                        titleDrawable,
                        intArray,null
                    )

                    mArrayList.add(setting)
                }
                4-> {
                    val intArray = arrayListOf<Int>()
                    val btnDrawable =
                        R.drawable.ic_keyboard_arrow_right_gray_24dp
                    val title = "Erase all content"
                    val titleDrawable = R.drawable.ic_eraser
                    intArray.add(btnDrawable)
                    val setting = SettingItem(
                        title,
                        titleDrawable,
                        null,null
                    )
                    mArrayList.add(setting)
                }
                5-> {
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
                        intArray,null
                    )
                    mArrayList.add(setting)
                }
                6-> {
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
                        intArray,null
                    )
                    mArrayList.add(setting)
                }

            }

        }

        return mArrayList
    }

}