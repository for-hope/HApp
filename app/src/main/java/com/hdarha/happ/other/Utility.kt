package com.hdarha.happ.other

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.DisplayMetrics


object Utility {
    fun calculateNoOfColumns(
        context: Context,
        columnWidthDp: Float
    ): Int { // For example columnWidthdp=180
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / columnWidthDp + 0.5).toInt()
    }

    private fun getPath(uri: Uri?,activity:Activity): String? {
        val projection =
            arrayOf(MediaStore.Images.Media.DATA)
        val cursor =
            activity.contentResolver.query(uri!!, projection, null, null, null)
                ?: return null
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val s = cursor.getString(columnIndex)
        cursor.close()
        return s
    }

}