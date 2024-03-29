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


}