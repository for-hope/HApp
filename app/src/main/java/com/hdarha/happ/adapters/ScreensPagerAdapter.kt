package com.hdarha.happ.adapters

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
        return ScreenFragment.newInstance(screens[position])
    }
}