package com.hdarha.happ.activities

import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hdarha.happ.R
import com.hdarha.happ.fragments.BottomSheet
import com.hdarha.happ.fragments.OnDialogComplete
import com.hdarha.happ.objects.OKResponse
import com.hdarha.happ.objects.Voice
import com.hdarha.happ.other.RetrofitClientInstance
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.util.FileUtils.getPath
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_image_display.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.relex.photodraweeview.PhotoDraweeView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class ImageDisplayActivity : AppCompatActivity(),
    OnDialogComplete {
    private var mTopToolbar: Toolbar? = null
    private var mPhotoDraweeView: PhotoDraweeView? = null
    private var imgUri: String? = null
    private var ogImage: String? = null
    private var isSoundSelected = false
    private var audioId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_display)


        mPhotoDraweeView = findViewById(R.id.photo_drawee_view)


        mTopToolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(mTopToolbar)
        supportActionBar?.title = "Edit Photo"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTitle)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        };

        val bottomSheet = BottomSheet(this)
        imgUri = intent.getStringExtra("imgUri")
        ogImage = imgUri
        bottom_bar.replaceMenu(R.menu.bottom_menu)
        bottom_bar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_volume -> {


                    bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                    if (bottomSheet.isAdded) {
                        this.supportFragmentManager.executePendingTransactions()
                        bottomSheet.dialog!!.setOnDismissListener {
                            Log.d("DIALOG", "dissmissed")
                        }
                    }


                    true
                }
                else -> false
            }
        }
        mPhotoDraweeView!!.setPhotoUri(Uri.parse(imgUri))

        bottom_bar.setNavigationOnClickListener {
            val rnds = (0..10).random()
            val dir: File = File("/storage/emulated/0/Pictures/happ/")
            var success = true
            if (!dir.exists()) {
                success = dir.mkdirs()
            }
            //TODO
            if (success) {
                val f = File("/storage/emulated/0/Pictures/happ/cropped$rnds.jpg")
                val dest: Uri =
                    Uri.fromFile(f)
                cropImage(Uri.parse(imgUri), dest)
            } else {
                Toast.makeText(this, "Error creating image directory.", Toast.LENGTH_SHORT).show()
            }

        }

        fab.setOnClickListener {
            if (isSoundSelected) {
                Blurry.with(this).radius(10).sampling(2).onto(rootView_img_display.rootView as ViewGroup)
                //showDialog("Processing video...")
                startUpload()
            } else {
                Toast.makeText(this, "Select a voice first.", Toast.LENGTH_SHORT).show()
            }

        }

        textAnimation()


    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun makeDialog(): Dialog {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_proceessing_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    private fun showDialog(title: String, dialog: Dialog) {

        //dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val imgView = dialog.findViewById<ImageView>(R.id.dialog_img)
        imgView.setImageURI(Uri.parse(imgUri))
        val body = dialog.findViewById(R.id.dialog_body) as TextView
        body.text = title
        val yesBtn = dialog.findViewById(R.id.accept_btn_dialog) as Button
        val pb = dialog.findViewById<ProgressBar>(R.id.dialog_pb)
        pb.isIndeterminate = true
        pb.scaleY = 3f
        val noBtn = dialog.findViewById(R.id.cancel_btn_dialog) as Button

        noBtn.setOnClickListener {
            dialog.dismiss()
            Blurry.delete(rootView_img_display.rootView as ViewGroup)
        }

//        Handler().postDelayed(
//            {
//
//
//
//            },
//            1000 // value in milliseconds
//        )

        dialog.show()
    }

    private fun finishDialog(dialog: Dialog, url: String?) {
        val body = dialog.findViewById(R.id.dialog_body) as TextView
        val yesBtn = dialog.findViewById(R.id.accept_btn_dialog) as Button
        val pb = dialog.findViewById<ProgressBar>(R.id.dialog_pb)
        val noBtn = dialog.findViewById(R.id.cancel_btn_dialog) as Button
        if (ogImage != null && ogImage != "") {
            savePictures(ogImage!!)
        } else {
            Log.d("IMAGE", ogImage + "OGIMAGE")
        }

        body.text = "Processing Complete"
        body.typeface = Typeface.DEFAULT_BOLD
        body.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_check_circle_black_24dp,
            0
        )
        pb.visibility = View.INVISIBLE
        noBtn.visibility = View.GONE
        yesBtn.visibility = View.VISIBLE

        yesBtn.setOnClickListener {
            val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra("url", url)
            startActivity(intent)
            finish()
        }
    }

    private fun errDialog(dialog: Dialog, err: String) {
        val body = dialog.findViewById(R.id.dialog_body) as TextView
        val errText = dialog.findViewById(R.id.reasonTextView) as TextView
        val yesBtn = dialog.findViewById(R.id.accept_btn_dialog) as Button
        val pb = dialog.findViewById<ProgressBar>(R.id.dialog_pb)
        val noBtn = dialog.findViewById(R.id.cancel_btn_dialog) as Button

        body.text = "Error occured"
        errText.text = "Reason : $err"
        errText.visibility = View.VISIBLE

        body.typeface = Typeface.DEFAULT_BOLD
        body.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_error_black_24dp,
            0
        )
        pb.visibility = View.INVISIBLE
        noBtn.visibility = View.GONE
        yesBtn.visibility = View.VISIBLE

        yesBtn.text = "Try again"
        yesBtn.setOnClickListener {
            dialog.dismiss()
            Blurry.delete(rootView_img_display.rootView as ViewGroup)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id: Int = item.itemId

        if (id == R.id.action_favorite) {
            Toast.makeText(
                this,
                "Action clicked",
                Toast.LENGTH_LONG
            ).show()

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteCache(file: String) {
        val dir = File("/storage/emulated/0/Pictures/happ/")
        val files = dir.listFiles()

        for (f in files) {
            if (f == File(file)) {
                Log.d("FILE1", "YES")
            } else {
                f.delete()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {

            val resultUri = UCrop.getOutput(data!!)
            Log.d("gotRes", resultUri.toString())

            deleteCache(resultUri!!.path.toString())
            ogImage = imgUri

            imgUri = resultUri.path.toString()
            mPhotoDraweeView!!.setPhotoUri(resultUri, this)

//            val i = intent
//            i.putExtra("imgUri", resultUri.toString())
//            finish()
//            startActivity(i)
        } else if (resultCode == UCrop.RESULT_ERROR) {

            val cropError = UCrop.getError(data!!)
            Log.e("OnActivityResult0 ", cropError.toString())
        }
    }

    private fun cropImage(src: Uri, dest: Uri) {
        Log.e("OnActivityResult011 ", src.toString())
        UCrop.of(src, dest)
            .start(this)
    }

    private fun textAnimation() {
        val first = first_text as TextView
        val second = sec_text as TextView

        val animator = ValueAnimator.ofFloat(0.0f, 1.0f)
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.duration = 9000L
        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            val width = first.width.toFloat()
            val translationX = width * progress
            first.translationX = translationX
            second.translationX = translationX - width
        }
        animator.start()
    }

    override fun onComplete(value: Voice?, key: Int) {
        isSoundSelected = true
        Log.d("Interface", value?.caption!!)
        first_text.text = value.caption!!
        sec_text.text = value.caption!!
        val cd = ColorDrawable(Color.parseColor("#6D2DEC29"))
        first_text.background = cd
        sec_text.background = cd
        audioId = value.name
    }

    private fun savePictures(imgUri: String) {
        GlobalScope.launch {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
            val currentDate = sdf.format(Date())
            val prefValue = "history"
            val keyValue = "map"
            val gson = Gson()
            val sharedPreferences =
                this@ImageDisplayActivity.getSharedPreferences(prefValue, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val hashString = sharedPreferences.getString(keyValue, "")
            val type: Type = object : TypeToken<HashMap<String, String>>() {}.type
            var storedHashMap: HashMap<String, String> = hashMapOf()
            if (hashString != "") {
                storedHashMap = gson.fromJson(hashString, type) as HashMap<String, String>
            }
            storedHashMap[imgUri] = currentDate
            val hashStringToSave = gson.toJson(storedHashMap)
            editor.putString(keyValue, hashStringToSave)
            editor.apply()
        }
    }




    private fun startUpload() {
        val dialog: Dialog = makeDialog()
        showDialog("Processing Video...", dialog)
        val retrofitClient = RetrofitClientInstance()
        val service: RetrofitClientInstance.ImageService =
            retrofitClient.retrofitInstance!!.create(RetrofitClientInstance.ImageService::class.java)

        val inputStream = this.contentResolver.openInputStream(Uri.parse(imgUri))
        val imgFile = File(getPath(this,Uri.parse(imgUri)))
        //val imgFile = File("/storage/emulated/0/DCIM/Facebook/FB_IMG_1588431186364.jpg")

        if (imgFile.exists()) {
            Log.d("REQUEST", "OK ${imgFile.toString()}")
        } else {
            Log.d("REQUEST_ERR", "OK ${imgFile.toString()}")
        }
        //val requestBodyFile: RequestBody = RequestBody.create(MediaType.parse("image/*"), imgFile)
        val requestBodyFile: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), imgFile)

        val part: MultipartBody.Part =
            MultipartBody.Part.createFormData("img", imgFile.name, requestBodyFile)

        val audioId = RequestBody.create(MediaType.parse("text/plain"), this.audioId)


        val uploadBundle: Call<ResponseBody?> = service.uploadImage(part, audioId)!!

        uploadBundle.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>?,
                response: Response<ResponseBody?>
            ) {
                val mJson = response.body()?.string()
                Log.d("RESPONSE", "OK ${response.body()?.string()}")
                Log.d("RESPONSE", "OK ${response.body()}")
                Log.d("RESPONSE", "OK ${mJson}")
                Log.d("RESPONSE", "OK ${response.errorBody()?.string()}")

                if (response.message() == "OK") {
                    val gson: Gson = Gson()
                    val json = response.body()?.string()
                    val resp = gson.fromJson(mJson, OKResponse::class.java)
                    if (resp != null) {
                        if (resp.status == "success") {
                            finishDialog(dialog, resp.url)
                        } else {
                            Toast.makeText(
                                this@ImageDisplayActivity,
                                "An error occurred.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            errDialog(dialog, resp.reason)
                        }
                    } else {
                        Toast.makeText(
                            this@ImageDisplayActivity,
                            "An error occurred.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        Log.e("ImageAct", "Err")
                        errDialog(dialog, "Server error try again later")
                    }
                } else {
                    errDialog(dialog, "Application error check your connection and try again.")
                    Toast.makeText(this@ImageDisplayActivity, "Error", Toast.LENGTH_SHORT).show()
                }


            }

            override fun onFailure(call: Call<ResponseBody?>?, t: Throwable?) {
                finishDialog(dialog, "wwww")
                Log.d("RESPONSE", " NOT OK $t")
                Toast.makeText(this@ImageDisplayActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        })


    }


}
