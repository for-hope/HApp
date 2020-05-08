package com.hdarha.happ.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentContainer
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hdarha.happ.R
import com.hdarha.happ.adapters.ScreensPagerAdapter
import com.hdarha.happ.databinding.ActivityMainBinding
import com.hdarha.happ.fragments.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity :  AppCompatActivity() {
    var bi: ActivityMainBinding? = null
    private lateinit var pagerAdapter: ScreensPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initActivity()
        if (savedInstanceState == null) {
            val fragment = HomeFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.frg_container, fragment, fragment.javaClass.simpleName)
                .commit()
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(object :
            BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.bottomNavigationLabId -> {
                        Log.d("Main", "CLICKED")
                        val fragment = HomeFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.frg_container, fragment, fragment.javaClass.simpleName)
                            .commit()
                        return true
                    }
                    R.id.bottomNavigationHistorykMenuId -> {

                        Log.d("Main", "CLICKED11")
                        val fragment =
                            HistoryFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.frg_container, fragment, fragment.javaClass.simpleName)
                            .commit()
                        return true
                    }
                    R.id.bottomNavigationMeMenuId -> {
                        Log.d("Main", "CLICKED12")
                        val fragment =
                            SettingsFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.frg_container, fragment, fragment.javaClass.simpleName)
                            .commit()

                        return true
                    }
                }
                return false
            }

        })


    }


    private fun initActivity() {

        //init Fresco image holder.
        Fresco.initialize(this)
        //make status bar transparent
        //showCustomUI()
        bi = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
    }


}
