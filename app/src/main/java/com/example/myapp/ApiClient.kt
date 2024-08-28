package com.example.myapp

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiClient {
    private const val MY_KEY = "7c020d82-368e-4d63-abbc-be98dc7e7730"
    private const val BASEURL = "https://todos.simpleapi.dev"

    /*
    private val loggingInterceptor:HttpLoggingInterceptor  = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)
    */

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original: Request = chain.request()
            val request: Request = original.newBuilder()
                .header("apikey", MY_KEY)
                .method(method = original.method, body = original.body)
                .build()
            chain.proceed(request)
        }
        .build()

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASEURL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okHttpClient)
        .build()

    val apiService:ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}