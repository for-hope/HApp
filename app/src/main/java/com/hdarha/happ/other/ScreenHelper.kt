package com.hdarha.happ.other

import android.content.Context
import com.hdarha.happ.objects.Screen
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

object ScreenHelper {


    const val KEY_BG_URI = "bgUri"
    const val KEY_POSTER_URI = "posterUri"
    const val KEY_BANNER_URI = "banner"
    const val KEY_TITLE_STRING = "title"
    const val KEY_OVERVIEW_STRING = "overview"
    fun getScreensFromJson(fileName: String, context: Context): ArrayList<Screen> {

        val screens = ArrayList<Screen>()

        try {
            // Load the JSONArray from the file
            val jsonString = loadJsonFromFile(
                fileName,
                context
            )
            val json = JSONObject(jsonString)
            val jsonScreens = json.getJSONArray("movies")

            // Create the list of Movies
            for (index in 0 until jsonScreens.length()) {
                val movieBgUri = jsonScreens.getJSONObject(index).getString(KEY_BG_URI)
                val screenPosterUri = jsonScreens.getJSONObject(index).getString(KEY_POSTER_URI)
                val bannerUri = jsonScreens.getJSONObject(index).getString(KEY_BANNER_URI)
                val title = jsonScreens.getJSONObject(index).getString(KEY_TITLE_STRING)
                val overview = jsonScreens.getJSONObject(index).getString(KEY_OVERVIEW_STRING)
                screens.add(Screen(movieBgUri,screenPosterUri,bannerUri,title,overview))
            }
        } catch (e: JSONException) {
            return screens
        }

        return screens
    }

    private fun loadJsonFromFile(filename: String, context: Context): String {
        var json = ""

        try {
            val input = context.assets.open(filename)
            val size = input.available()
            val buffer = ByteArray(size)
            input.read(buffer)
            input.close()
            json = buffer.toString(Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return json
    }
}