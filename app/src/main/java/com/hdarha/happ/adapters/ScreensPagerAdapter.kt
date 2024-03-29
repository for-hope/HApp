package com.hdarha.happ.adapters

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hdarha.happ.fragments.ScreenFragment
import com.hdarha.happ.objects.Screen


// 1
class ScreensPagerAdapter(activity:AppCompatActivity, private val screens: ArrayList<Screen>) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return screens.size
    }

    override fun createFragment(position: Int): Fragment {
        Log.d("SF",screens[position].toString())
        return ScreenFragment.newInstance(screens[position])
    }
}