package com.hdarha.happ.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hdarha.happ.BuildConfig
import com.hdarha.happ.R
import com.hdarha.happ.activities.ImageDisplayActivity
import com.hdarha.happ.activities.LoginActivity
import com.hdarha.happ.activities.MyVideosActivity
import com.hdarha.happ.activities.SoundLibraryActivity
import com.hdarha.happ.adapters.ScreensPagerAdapter
import com.hdarha.happ.other.PREF_POINTS
import com.hdarha.happ.other.ScreenHelper
import com.hdarha.happ.other.permissionCheck
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_buttons_bar_main.*
import kotlinx.android.synthetic.main.view_top_layout_main.*

private const val REQUEST_CODE_CHOOSE = 1

class HomeFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var pagerAdapter: ScreensPagerAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_home, container, false)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //init data binding tool
        showCustomUI()
        auth = Firebase.auth
        updateUI(auth.currentUser)
        //setup viewpager screens
        val screens = ScreenHelper.getScreensFromJson(
            "movies.json",
            context!!
        )
        Log.d("screens", screens.toString())
        val dotsIndicator = activity!!.findViewById<DotsIndicator>(R.id.dots_indicator)
        val viewPager = activity!!.findViewById<ViewPager2>(R.id.view_pager)

        pagerAdapter = ScreensPagerAdapter(
            activity as AppCompatActivity,
            screens
        )
        viewPager.offscreenPageLimit = 2
        viewPager.adapter = pagerAdapter

        dotsIndicator.setViewPager2(viewPager)
        generate_btn.setOnClickListener {
            if (auth.currentUser != null) {
                permissionCheck(context!!,view!!) { showGallery() }
            } else {
                activitiesManager(LoginActivity::class.java)
            }

        }

        my_videos_btn.setOnClickListener {
            permissionCheck(context!!,view!!) { activitiesManager(MyVideosActivity::class.java) }

//            val intent = Intent(this.context, MyVideosActivity::class.java)
//            startActivity(intent)
        }
        sound_btn.setOnClickListener {
            activitiesManager(SoundLibraryActivity::class.java)
//            val intent = Intent(this.context, SoundLibraryActivity::class.java)
//            startActivity(intent)
        }

        profile_image.setOnClickListener {
            if (auth.currentUser != null) {
                activity?.bottomNavigationView?.selectedItemId = R.id.bottomNavigationMeMenuId

            } else {
                val i = Intent(this.context, LoginActivity::class.java)
                startActivity(i)
            }
        }

        homeShareBtn.setOnClickListener {
            shareApp()
//            val intent = Intent(context, VideoPlayerActivity::class.java)
//            intent.putExtra("url", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4")
//            startActivity(intent)
        }


    }

    private fun shareApp() {
        val link = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
        val title = getString(R.string.share_app_title)
        val desc = getString(R.string.share_app_description) + link
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            desc
        )
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, title))
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val profileUrl = currentUser.photoUrl
            //val p = Picasso.get()
            Log.d("url", profileUrl.toString())
            Glide.with(this).load(profileUrl).into(profile_image)
            creditNumberTextView.visibility = View.VISIBLE
            creditTextView.visibility = View.VISIBLE
            creditNumberTextView.text = activity!!.getSharedPreferences(PREF_POINTS,Context.MODE_PRIVATE).getInt("credit",0).toString()
        } else {
            signInTextView.visibility = View.VISIBLE
            creditNumberTextView.visibility = View.GONE
            creditTextView.visibility = View.GONE
        }
    }

    private fun activitiesManager(mClass: Class<*>) {
        if (auth.currentUser != null) {
            val i = Intent(this.context, mClass)
            startActivity(i)
        } else {

            val i = Intent(this.context, LoginActivity::class.java)
            startActivity(i)
        }

    }

    private fun showGallery() {
        Matisse.from(this)
            .choose(MimeType.ofImage())
            .countable(true)
            .maxSelectable(1)
            .capture(true)
            .theme(R.style.Matisse_Dracula)
            .captureStrategy(
                CaptureStrategy(true, "com.hdarha.app.fileprovider", "HApp")
            )
            .showSingleMediaType(true)
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .autoHideToolbarOnSingleTap(true)
            .thumbnailScale(0.85f)
            .imageEngine(GlideEngine())
            .showPreview(false) // Default is `true`
            .forResult(REQUEST_CODE_CHOOSE)
    }



    private fun showCustomUI() {
        val decorView = activity?.window?.decorView
        decorView?.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        activity?.window?.statusBarColor =
            ContextCompat.getColor(
                this.context!!,
                R.color.colorTransparent
            )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            //mAdapter.setData(Matisse.obtainResult(data), Matisse.obtainPathResult(data))
            Log.e("OnActivityResult ", Matisse.obtainOriginalState(data).toString())
            Log.e("OnActivityResult ", Matisse.obtainResult(data).toString())

            val intent = Intent(this.context, ImageDisplayActivity::class.java)
            intent.putExtra("imgUri", Matisse.obtainResult(data)[0].toString())
            startActivity(intent)


        } else if (requestCode == REQUEST_CODE_CHOOSE) {
            Log.e("OnActivityResult", "Error happened")
        }
    }

}