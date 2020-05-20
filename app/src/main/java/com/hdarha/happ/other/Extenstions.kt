package com.hdarha.happ.other

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.snackbar.Snackbar
import com.hdarha.happ.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.CompositePermissionListener
import com.karumi.dexter.listener.single.PermissionListener
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener

const val PREF_HISTORY = "history"
const val PREF_VOICE = "voice"
const val PREF_POINTS = "points"
const val PREF_SETTINGS = "settings"
fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

val Any.TAG: String
    get() {
        return if (!javaClass.isAnonymousClass) {
            val name = javaClass.simpleName
            if (name.length <= 23) name else name.substring(0, 23)// first 23 chars
        } else {
            val name = javaClass.name
            if (name.length <= 23) name else name.substring(
                name.length - 23,
                name.length
            )// last 23 chars
        }
    }

fun get24dpDrawable(activity: Activity, resId: Int): BitmapDrawable {
    val dimen = activity.resources.getDimension(R.dimen._18sdp).toInt()
    val vector = activity.getDrawable(resId) as VectorDrawable
    return BitmapDrawable(activity.resources, vector.toBitmap(dimen, dimen))
}

fun convertDpToPixel(dp: Float, context: Context?): Float {
    return dp * (context!!.resources
        .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}
fun permissionCheck(context: Context, view: View, startAction: () -> Unit) {

    val snackbarPermissionListener: PermissionListener =
        SnackbarOnDeniedPermissionListener.Builder
            .with(
                view,
                context.getString(R.string.storage_denied_msg)
            )
            .withOpenSettingsButton("Settings")
            .withCallback(object : Snackbar.Callback() {
                override fun onShown(snackbar: Snackbar?) {
                    // Event handler for when the given Snackbar is visible
                }

                override fun onDismissed(snackbar: Snackbar?, event: Int) {
                    // Event handler for when the given Snackbar has been dismissed
                    Toast.makeText(
                        context,
                        "The app won't work properly without this permission",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }).build()

    val compositePermissionListener =
        CompositePermissionListener(snackbarPermissionListener, object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
//                Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT)
//                    .show()
                startAction()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                if (p0!!.isPermanentlyDenied) {
                    Toast.makeText(
                        context,
                        "Please enable permission access in settings",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permission: PermissionRequest?,
                token: PermissionToken?
            ) {
                //Toast.makeText(context, "DIK", Toast.LENGTH_SHORT).show()
                token?.continuePermissionRequest()
            }
        })

    Dexter.withContext(context)
        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        .withListener(compositePermissionListener).check()
}