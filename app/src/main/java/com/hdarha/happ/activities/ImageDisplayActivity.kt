package com.hdarha.happ.activities

import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import com.hdarha.happ.R
import com.hdarha.happ.fragments.BottomSheet
import com.hdarha.happ.fragments.OnDialogComplete
import com.hdarha.happ.other.RetrofitClientInstance
import com.yalantis.ucrop.UCrop
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_image_display.*
import me.relex.photodraweeview.PhotoDraweeView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class ImageDisplayActivity : AppCompatActivity(),
    OnDialogComplete {
    private var mTopToolbar: Toolbar? = null
    private var mPhotoDraweeView: PhotoDraweeView? = null
    private var imgUri: String? = null
    private var originalImage: String? = null
    private var isSoundSelected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_display)

        mPhotoDraweeView = findViewById(R.id.photo_drawee_view)


        mTopToolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(mTopToolbar)
        supportActionBar?.title = "Edit Photo"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val bottomSheet = BottomSheet(this)
        imgUri = intent.getStringExtra("imgUri")

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
                Blurry.with(this).radius(10).sampling(2).onto(rootView_img_display)
                showDialog("Processing video...")
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

    private fun showDialog(title: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_proceessing_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
        yesBtn.setOnClickListener {
            val intent = Intent(this, VideoPlayerActivity::class.java)
            startActivity(intent)
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
            Blurry.delete(rootView_img_display)
        }

        Handler().postDelayed(
            {
                if (originalImage != null && originalImage != "") {
                    savePictures(originalImage!!)
                }
                // This method will be executed once the timer is over
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


            },
            1000 // value in milliseconds
        )

        dialog.show()
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
            originalImage = imgUri
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

    override fun onComplete(value: String?) {
        isSoundSelected = true
        Log.d("Interface", value)
        first_text.text = value
        sec_text.text = value
        val cd = ColorDrawable(Color.parseColor("#6D2DEC29"))
        first_text.background = cd
        sec_text.background = cd
    }

    private fun savePictures(imgUri: String) {

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        val currentDate = sdf.format(Date())
        Log.d("CurrentDate", currentDate)
        val prefValue = "GalleryPref"
        val keyValue = "PhotoDates"

        val sharedPref: SharedPreferences =
            this.getSharedPreferences(prefValue, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()


        var photoDatesList: MutableSet<String>? = sharedPref.getStringSet(keyValue, null)

        if (photoDatesList == null) {
            photoDatesList = mutableSetOf()
        }
        photoDatesList.add(currentDate)
        editor.putStringSet(keyValue, photoDatesList)
        var photosList: MutableSet<String>? = sharedPref.getStringSet(currentDate, null)
        if (photosList == null) {
            photosList = mutableSetOf()
        }
        photosList.add(imgUri)
        editor.putStringSet(currentDate, photosList)
        editor.apply()


    }
    fun getPath(uri: Uri?): String? {
        val projection =
            arrayOf<String>(MediaStore.Images.Media.DATA)
        val cursor: Cursor = managedQuery(uri, projection, null, null, null)
        startManagingCursor(cursor)
        val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(columnIndex)
    }
    private fun startUpload() {
        val retrofitClient = RetrofitClientInstance()
        val service: RetrofitClientInstance.ImageService = retrofitClient.retrofitInstance!!.create(RetrofitClientInstance.ImageService::class.java)

        val inputStream = this.contentResolver.openInputStream(Uri.parse(imgUri))
        val imgFile = File(getPath(Uri.parse(imgUri)))

        val requestBodyFile: RequestBody = RequestBody.create(MediaType.parse("image/*"), imgFile)

        val part: MultipartBody.Part =
            MultipartBody.Part.createFormData("image", imgFile.name, requestBodyFile)

        val audio_id = RequestBody.create(MediaType.parse("text/plain"), "1")

 



//make sync call




        val uploadBundle: Call<ResponseBody?> = service.uploadImage(part,audio_id)!!

        uploadBundle.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>?,
                response: Response<ResponseBody?>
            ) {
                val gson = Gson()
                Log.d("RESPONSE","OK ${response}")
            }

            override fun onFailure(call: Call<ResponseBody?>?, t: Throwable?) {
                Log.d("RESPONSE"," NOT OK $t")
            }
        })

        //Log.d("Response_API","${response.body()}")
    }


}
