package com.hdarha.happ.activities

import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
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
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.util.FileUtils.getPath
import id.zelory.compressor.Compressor
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_image_display.*
import kotlinx.coroutines.Dispatchers
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


class ImageDisplayActivity : AppCompatActivity(),
    OnDialogComplete {
    private var mTopToolbar: Toolbar? = null
    private var mPhotoDraweeView: PhotoDraweeView? = null
    private var imgUri: String? = null
    private var ogImage: String? = null
    private val cancelRequestSignal = CancellationSignal()
    private val bottomSheet = BottomSheet(this)
    //private lateinit var firstImage:String
    private var isSoundSelected = false
    private var audioId: String = ""
    private var isCropped: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_display)
        initUI()
    }


    private fun initClickListeners() {
        bottom_bar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_volume -> {

                    showBottomSheet(bottomSheet)
                    true
                }
                else -> false
            }
        }


        bottom_bar.setNavigationOnClickListener {
            startCropActivity()
        }

        fab.setOnClickListener {
            submitImage()

        }
    }

    private fun submitImage() {
        if (isSoundSelected) {
            Blurry.with(this).radius(10).sampling(2)
                .onto(rootView_img_display.rootView as ViewGroup)
            startUpload()
        } else {
            Toast.makeText(this, "Select a voice first.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCropActivity() {
        val rnds = (0..10).random()
        //val dir: File = File("/storage/emulated/0/Pictures/happ/")
        val dir = File(externalCacheDir, "cropped")
        var success = true
        if (!dir.exists()) {
            success = dir.mkdirs()
        }
        if (success) {
            val f = File(dir, "cropped$rnds.jpg")
            val dest: Uri =
                Uri.fromFile(f)
            if (ogImage != null && ogImage != "") {
                cropImage(Uri.parse(ogImage), dest)
            } else {
                cropImage(Uri.parse(imgUri), dest)
            }

        } else {
            Toast.makeText(this, "Error creating image directory.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showBottomSheet(bottomSheet: BottomSheet) {
        if (!bottomSheet.isVisible and !bottomSheet.isAdded) {
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
            if (bottomSheet.isAdded) {
                this.supportFragmentManager.executePendingTransactions()
            }
        }

    }

    private fun initUI() {
        mPhotoDraweeView = findViewById(R.id.photo_drawee_view)
        mTopToolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(mTopToolbar)
        supportActionBar?.title = "Edit Photo"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTitle)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        imgUri = intent.getStringExtra("imgUri")
        //firstImage = imgUri!!
        if (!isCropped) {
            ogImage = imgUri!!
        }

        mPhotoDraweeView!!.setPhotoUri(Uri.parse(imgUri))
        bottom_bar.replaceMenu(R.menu.bottom_menu)
        textAnimation()
        initClickListeners()

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

    private fun showDialog(dialog: Dialog) {
        val imgView = dialog.findViewById<ImageView>(R.id.dialog_img)
        Log.d("ImgURi", imgUri!!)
        if (imgUri!!.startsWith("content")) {
            Picasso.get().load(Uri.parse(imgUri)).resize(300, 200).centerCrop().into(imgView)
        } else {
            val f = File(imgUri!!)
            Picasso.get().load(f).resize(300, 200).centerCrop().into(imgView)
        }

        //.setImageURI(Uri.parse(imgUri))
        val body = dialog.findViewById(R.id.dialog_body) as TextView
        body.text = "Processing Video"

        val pb = dialog.findViewById<ProgressBar>(R.id.dialog_pb)
        pb.isIndeterminate = true
        pb.scaleY = 3f
        val noBtn = dialog.findViewById(R.id.cancel_btn_dialog) as Button

        noBtn.setOnClickListener {
            cancelRequestSignal.cancel()
            dialog.dismiss()
            Blurry.delete(rootView_img_display.rootView as ViewGroup)
        }

        dialog.show()
    }

    private fun finishDialog(dialog: Dialog, url: String?) {
        val body = dialog.findViewById(R.id.dialog_body) as TextView
        val yesBtn = dialog.findViewById(R.id.accept_btn_dialog) as Button
        val pb = dialog.findViewById<ProgressBar>(R.id.dialog_pb)
        val noBtn = dialog.findViewById(R.id.cancel_btn_dialog) as Button
        if (ogImage != null && ogImage != "") {
            savePictures(ogImage!!)
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
            dialog.dismiss()
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

    private fun deleteCache(file: String) {
        val dir = File(externalCacheDir, "cropped")
        val files = dir.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (f in files) {
                if (f != File(file)) {
                    f.delete()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            deleteCache(resultUri!!.path.toString())
            if (!isCropped) {
                ogImage = imgUri
            }

            imgUri = resultUri.path.toString()
            Log.d("IMGURI", imgUri)
            mPhotoDraweeView!!.setPhotoUri(resultUri, this)

        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Log.e("OnActivityResult0 ", cropError.toString())
        }
    }

    private fun cropImage(src: Uri, dest: Uri) {
        isCropped = true
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
        //Log.d("Interface", value?.caption!!)
        first_text.text = value?.caption!!
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
            Log.d("SavePicture", "Saved.")
        }
    }


    private fun startUpload() {
        val dialog: Dialog = makeDialog()
        showDialog(dialog)
        val retrofitClient = RetrofitClientInstance()
        val service: RetrofitClientInstance.ImageService =
            retrofitClient.retrofitInstance!!.create(RetrofitClientInstance.ImageService::class.java)

        Log.d("CheckImgUri", imgUri!!)


        var imgFile = File(imgUri!!)

        if (imgUri!!.startsWith("content")) {
            Log.d("CheckImgUriContent", imgUri!!)
            val mUri = Uri.parse(imgUri!!)
            val path = getPath(this, mUri)
            imgFile = File(path)
        }
        val sizeNormal = imgFile.length() / 1024
        Log.d("File Size:","$sizeNormal kb")
        GlobalScope.launch {
            val compressedFile = Compressor.compress(applicationContext,imgFile)
            val size = compressedFile.length() / 1024
            Log.d("File Size Compressed ", "$size kb ")



            val requestBodyFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), compressedFile)

            val part: MultipartBody.Part =
                MultipartBody.Part.createFormData("img", imgFile.name, requestBodyFile)

            val audioId = RequestBody.create(MediaType.parse("text/plain"), this@ImageDisplayActivity.audioId)


            val uploadBundle: Call<ResponseBody?> = service.uploadImage(part, audioId)!!

            cancelRequestSignal.setOnCancelListener {
                uploadBundle.cancel()
            }
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
                        //val json = response.body()?.string()
                        val resp = gson.fromJson(mJson, OKResponse::class.java)
                        if (resp != null) {
                            if (resp.status == "success") {
                                finishDialog(dialog, resp.url)
                            } else {
                                errDialog(dialog, resp.reason)
                            }
                        } else {
                            errDialog(dialog, "Server error try again later")
                        }
                    } else {
                        errDialog(dialog, "Application error try again.")
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>?, t: Throwable?) {
                    Log.e("Error", t.toString())
                    errDialog(dialog, "Connection error check your connection and try again.")
//                Log.d("RESPONSE", " NOT OK $t")

                }
            })
        }




    }


}
