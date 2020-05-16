package com.hdarha.happ.activities

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hdarha.happ.R
import kotlinx.android.synthetic.main.activity_sound_library.*
import kotlinx.android.synthetic.main.activity_webview.*


class WebViewActivity : AppCompatActivity() {

    private class SettingsWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String
        ): Boolean {
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        setSupportActionBar(settingsToolBar)
        initActivity()
        val webSettings = mSettingWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        mSettingWebView.webViewClient = SettingsWebViewClient()
        mSettingWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                mWebProgress.progress = progress
                if (progress == 100) mWebProgress.visibility = View.GONE
            }
        }
        val url = intent.getStringExtra("url")
        mSettingWebView.loadUrl("https://twitter.com/fr/privacy")

    }

    override fun onBackPressed() {
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    private fun initActivity() {
        val title = intent.getStringExtra("title")
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        window.statusBarColor = Color.WHITE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

    }
}