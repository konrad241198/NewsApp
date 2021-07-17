package com.jurkowski.newsapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jurkowski.newsapp.repository.ArticleRepositoryImpl

class NewsViewModelProviderFactory(
  val app: Application,
  private val articleRepositoryImpl: ArticleRepositoryImpl
) : ViewModelProvider.Factory{

  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return NewsViewModel(app, articleRepositoryImpl) as T
  }

}