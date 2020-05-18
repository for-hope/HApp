package com.hdarha.happ.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hdarha.happ.R

import com.hdarha.happ.fragments.HistoryFragment
import com.hdarha.happ.fragments.HomeFragment
import com.hdarha.happ.fragments.SettingsFragment
import com.hdarha.happ.other.permissionCheck
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    //private lateinit var pagerAdapter: ScreensPagerAdapter

    private lateinit var auth: FirebaseAuth
    private lateinit var mView:View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initActivity()
        firebaseAnalytics = Firebase.analytics
        manageFragments(savedInstanceState)
        mView = findViewById(R.id.contentMainActivity)
        auth = Firebase.auth


    }

    override fun onBackPressed() {
        finish()
    }



    private fun historyFragment() {
        val fragment =
            HistoryFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.frg_container, fragment, fragment.javaClass.simpleName)
            .commit()
    }
    private fun manageFragments(savedInstanceState: Bundle?) {
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
                        val fragment = HomeFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.frg_container, fragment, fragment.javaClass.simpleName)
                            .commit()
                        return true
                    }
                    R.id.bottomNavigationHistorykMenuId -> {
                        if (auth.currentUser != null) {
                           permissionCheck(this@MainActivity,mView) {historyFragment()}
                        } else {
                            val i = Intent(this@MainActivity, LoginActivity::class.java)
                            startActivity(i)
                        }
                        return true
                    }
                    R.id.bottomNavigationMeMenuId -> {
                        if (auth.currentUser != null) {
                            val fragment =
                                SettingsFragment()
                            supportFragmentManager.beginTransaction()
                                .replace(
                                    R.id.frg_container,
                                    fragment,
                                    fragment.javaClass.simpleName
                                )
                                .commit()
                        } else {
                            val i = Intent(this@MainActivity, LoginActivity::class.java)
                            startActivity(i)
                        }
                        return true
                    }
                }
                return false
            }

        })

    }

    private fun initActivity() {
        if (!Fresco.hasBeenInitialized()) {
        //init Fresco image holder.
        Fresco.initialize(this)
        }
        //make status bar transparent
        //showCustomUI()

    }


}
