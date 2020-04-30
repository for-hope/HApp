package com.hdarha.happ.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.hdarha.happ.R
import com.hdarha.happ.activities.ImageDisplayActivity
import com.hdarha.happ.activities.MyVideosActivity
import com.hdarha.happ.activities.SoundLibraryActivity
import com.hdarha.happ.adapters.ScreensPagerAdapter
import com.hdarha.happ.objects.Card
import com.hdarha.happ.other.ScreenHelper
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy
import kotlinx.android.synthetic.main.view_buttons_bar_main.*
import kotlinx.android.synthetic.main.view_top_layout_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET

private const val REQUEST_CODE_CHOOSE = 1
class HomeFragment : Fragment() {

    private lateinit var pagerAdapter: ScreensPagerAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_home, container, false)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //init data binding tool
        showCustomUI()
        //setup viewpager screens
        val screens = ScreenHelper.getScreensFromJson(
            "movies.json",
            context!!
        )

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
            permissionCheckGallery()
        }

        my_videos_btn.setOnClickListener {
            val intent = Intent(this.context, MyVideosActivity::class.java)
            startActivity(intent)
        }
        sound_btn.setOnClickListener {
            val intent = Intent(this.context, SoundLibraryActivity::class.java)
            startActivity(intent)
        }

        profile_image.setOnClickListener {
            startUpload()
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
                CaptureStrategy(true, "com.hdarha.app.fileprovider", "testhapp")
            )
            .setOnSelectedListener { _, pathList ->
                Log.e(
                    "onSelected",
                    "onSelected: pathList=$pathList"
                );
            }
            .showSingleMediaType(true)
            .autoHideToolbarOnSingleTap(true)
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .thumbnailScale(0.85f)
            .imageEngine(GlideEngine())
            .showPreview(false) // Default is `true`
            .forResult(REQUEST_CODE_CHOOSE)
    }

    private fun permissionCheckGallery() {
        Dexter.withContext(this.activity)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT)
                        .show()
                    showGallery()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    Toast.makeText(context, "DIK", Toast.LENGTH_SHORT).show()
                }
            }).check()
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


        } else if (requestCode == REQUEST_CODE_CHOOSE){
            Log.e("OnActivityResult","Error happened")
        }
    }

    private fun startUpload() {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://db.ygoprodeck.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: GitHubService = retrofit.create(GitHubService::class.java)

        val repos: Call<Card>? = service.listRepos()


        repos?.enqueue(object : Callback<Card> {
            override fun onResponse(
                call: Call<Card>?,
                response: Response<Card>
            ) {
                Log.d("RetroFit_Nice", response.body()?.data!![0].desc)
                Toast.makeText(
                    context,
                    "Something went right",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onFailure(
                call: Call<Card>?,
                t: Throwable?
            ) {
                Log.d("RetroFit",t.toString())
                Toast.makeText(
                    context,
                    "Something went wrong...Please try later!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        //Log.d("RETROFIT_API",repos)


    }

    interface GitHubService {

        @GET("/api/v7/cardinfo.php?name=Raigeki")

        fun listRepos(): Call<Card>?
    }
}