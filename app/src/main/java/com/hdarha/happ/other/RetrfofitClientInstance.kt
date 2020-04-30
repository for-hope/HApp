package com.hdarha.happ.other

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


private const val BASE_URL = "https://api.mock.org/"

class RetrofitClientInstance {
    private var retrofit: Retrofit? = null

    val retrofitInstance: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
    interface ImageService {
        @Multipart
        @POST("/deepface")
        open fun uploadImage(
            @Part image : MultipartBody.Part,
            @Part("audio_id") requestBody: RequestBody
        ): Call<ResponseBody?>?
    }
}

