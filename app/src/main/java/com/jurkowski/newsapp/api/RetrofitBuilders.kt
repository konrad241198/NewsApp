package com.jurkowski.newsapp.api

import com.jurkowski.newsapp.utils.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun buildClient(): OkHttpClient =
  OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor())
    .build()

fun buildRetrofit(): Retrofit {
  return Retrofit.Builder()
    .client(buildClient())
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
}

fun buildApiService(): NewsApi =
  buildRetrofit().create(NewsApi::class.java)