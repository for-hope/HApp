package com.hdarha.happ.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hdarha.happ.R
import com.hdarha.happ.databinding.ActivityMainBinding
import com.hdarha.happ.fragments.HistoryFragment
import com.hdarha.happ.fragments.HomeFragment
import com.hdarha.happ.fragments.SettingsFragment
import com.hdarha.happ.fragments.TAG
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var bi: ActivityMainBinding? = null
    //private lateinit var pagerAdapter: ScreensPagerAdapter

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initActivity()
        manageFragments(savedInstanceState)

        auth = Firebase.auth


    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "STARTING")
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        //updateUI(currentUser)
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
                        val fragment =
                            HistoryFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.frg_container, fragment, fragment.javaClass.simpleName)
                            .commit()
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
        bi = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
    }


}
