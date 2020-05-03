package com.hdarha.happ.other

import com.hdarha.happ.objects.Voice
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.concurrent.TimeUnit


private const val BASE_URL = "https://hdarha.herokuapp.com"

class RetrofitClientInstance {
    private var retrofit: Retrofit? = null
    private var client: OkHttpClient.Builder = OkHttpClient.Builder()
    val retrofitInstance: Retrofit?
        get() {
            client.connectTimeout(60, TimeUnit.SECONDS)
            client.readTimeout(60, TimeUnit.SECONDS)
            client.writeTimeout(60, TimeUnit.SECONDS)

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build()
            }
            return retrofit
        }
    interface ImageService {
        @Multipart
        @POST("/do")
        fun uploadImage(
            @Part image : MultipartBody.Part,
            @Part("voice") requestBody: RequestBody
        ): Call<ResponseBody?>?
    }
    interface VoiceService {
        @GET("/voices")
        fun listVoices(): Call<List<Voice>>?
    }
}

