package com.jurkowski.newsapp

import android.app.Application
import com.jurkowski.newsapp.api.buildApiService
import com.jurkowski.newsapp.database.ArticleDatabase
import com.jurkowski.newsapp.repository.ArticleRepository
import com.jurkowski.newsapp.repository.ArticleRepositoryImpl

class App : Application() {

  companion object {
    private lateinit var instance: App

    val database: ArticleDatabase by lazy {
      ArticleDatabase.buildDatabase(instance)
    }

    val remoteApi by lazy { buildApiService() }

    val repository: ArticleRepository by lazy {
      ArticleRepositoryImpl()
    }

  }

  override fun onCreate() {
    super.onCreate()
    instance = this
  }
}