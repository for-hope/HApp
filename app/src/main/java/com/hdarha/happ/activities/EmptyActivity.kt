package com.hdarha.happ.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hdarha.happ.R
import com.hdarha.happ.fragments.ContentFragment
import com.hdarha.happ.fragments.HeaderFragment

class EmptyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_empty, HeaderFragment(), ContentFragment())
        showCustomUI()
    }

    private fun showCustomUI() {
        val decorView = this.window.decorView
        decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        this.window.statusBarColor =
            ContextCompat.getColor(this,
                R.color.colorTransparent
            )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}