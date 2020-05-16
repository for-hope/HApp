package com.hdarha.happ.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hdarha.happ.R
import com.hdarha.happ.activities.MainActivity
import com.hdarha.happ.activities.WebViewActivity
import kotlinx.android.synthetic.main.layout_setting_items.*
import kotlinx.android.synthetic.main.view_profile_bottom.*
import java.util.*


class ContentFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var preferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_content, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //activity as AppCompatActivity

        auth = Firebase.auth
        preferences = activity!!.getSharedPreferences("Settings", Context.MODE_PRIVATE)

        setupSettings()


        signOutButton.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun applyLanguage(lang: String) {
        val langCode = lang.take(2)
        val editor = preferences.edit()
        editor.putString("lang", langCode)
        editor.apply()
        textLanguageSetting.text = "Language ($langCode)"
        Toast.makeText(
            context,
            "App language changed to $lang",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun languageSetting() {
        var defaultLang = preferences.getString("lang", "En")
        textLanguageSetting.text = "Language ($defaultLang)"
        setting1.setOnClickListener {
            defaultLang = preferences.getString("lang", "En")
            var checkedItem = 0
            if (defaultLang == "Fr") {
                checkedItem = 1
            } else if (defaultLang == "Ar") {
                checkedItem = 2
            }
            val languages =
                arrayOf("English", "French", "Arabic")
            val builder =
                AlertDialog.Builder(this.activity!!)
            builder.setTitle("Change Language")
            builder.setIcon(R.drawable.ic_translate_black_24dp)
            builder.setSingleChoiceItems(languages, checkedItem, null)
            builder.setPositiveButton("Apply") { dialogInterface, _ ->
                dialogInterface.dismiss()
                val which = (dialogInterface as AlertDialog).listView.checkedItemPosition
                val lang = languages[which]
                applyLanguage(lang)

            }
            builder.show()
        }
    }

    private fun webSetting(title: String, url: String) {
        val mIntent = Intent(this.context, WebViewActivity::class.java)
        mIntent.putExtra("title", title)
        mIntent.putExtra("url", url)
        startActivity(mIntent)
    }

    private fun notificationSetting() {
        val notif = preferences.getBoolean("notifications", true)
        switchPushNotifications.isChecked = notif
        switchPushNotifications.setOnClickListener {
            if (!switchPushNotifications.isChecked) {
                switchPushNotifications.isChecked = true
                val d = makeDialog()
                val drawable = activity!!.getDrawable(R.drawable.ic_subscription)
                val body = "You won't receive any notifications if you turn this setting off."
                showDialog(d, drawable!!, body, "Turn Off", "Cancel", 0)
            } else {
                val editor = preferences.edit()
                editor.putBoolean("notifications", false)
                editor.apply()
                Toast.makeText(context, "Push notifications are turned on", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }


    private fun contactUsSetting() {
        imageFacebook.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com"))
            startActivity(browserIntent)
        }
        imageTwitter.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("http://www.twitter.com"))
            startActivity(browserIntent)
        }
        imageInstagram.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("http://www.instagram.com"))
            startActivity(browserIntent)
        }
        imageEmail.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "mlaminefetni@gmail.com", null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "HAapp Feedback")
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }
    }

    private fun eraseContent() {
        setting5.setOnClickListener {
            val d = makeDialog()
            val drawable = activity!!.getDrawable(R.drawable.ic_remove)
            showDialog(
                d,
                drawable!!,
                "Are you sure you want to remove all history and content from HApp?",
                "Remove All",
                "Cancel",
                1
            )
        }

    }

    private fun notificationOperation() {
        val editor = preferences.edit()
        editor.putBoolean("notifications", false)
        editor.apply()
        switchPushNotifications.isChecked = false
        Toast.makeText(context, "Push notifications are turned off", Toast.LENGTH_SHORT).show()
    }

    private fun dialogOperations(id: Int) {
        if (id == 0) {
            notificationOperation()
        } else if (id == 1) {
            Toast.makeText(this.context, "Deleting everything", Toast.LENGTH_SHORT).show()
        }

    }

    private fun eraseOperation() {
        //todo
    }
    private fun makeDialog(): Dialog {
        val dialog = Dialog(this.context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.layout_setting_alert)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    private fun showDialog(
        dialog: Dialog,
        drawable: Drawable,
        text: String,
        yesText: String,
        noText: String,
        id: Int
    ) {
        val imgView = dialog.findViewById<ImageView>(R.id.imageViewSettingDialog)
        val body = dialog.findViewById<TextView>(R.id.textViewDialogBody)
        val yesBtn = dialog.findViewById<Button>(R.id.btnPostiveSettings)
        val noBtn = dialog.findViewById<Button>(R.id.btnNegativeSettings)

        imgView.setImageDrawable(drawable)
        body.text = text
        yesBtn.text = yesText
        yesBtn.setOnClickListener {
            dialogOperations(id)
            dialog.dismiss()
        }
        noBtn.text = noText
        noBtn.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun setupSettings() {
        languageSetting()
        setting2.setOnClickListener {
            webSetting("Feedback", "")
        }
        notificationSetting()
        contactUsSetting()
        eraseContent()
        setting6.setOnClickListener {
            webSetting("Privacy Policy", "")
        }
        setting7.setOnClickListener {
            webSetting("About Us", "")
        }

    }


    private fun showConfirmationDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context!!)

        builder.setTitle("Sign Out")
        builder.setMessage("Are you sure you want to sign out ?")

        builder.setPositiveButton("Sign out") { dialog, which -> // Do nothing but close the dialog
            auth.signOut()
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        builder.setNegativeButton("No") { dialog, which -> // Do nothing
            dialog.dismiss()
        }

        val alert: AlertDialog = builder.create()
        alert.show()
    }


}