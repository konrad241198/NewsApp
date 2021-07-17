package com.jurkowski.newsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.jurkowski.newsapp.App
import com.jurkowski.newsapp.R
import com.jurkowski.newsapp.repository.ArticleRepositoryImpl
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity() {

  private val repository by lazy { App.repository }

  private val viewModelProviderFactory by lazy {
    NewsViewModelProviderFactory(application, repository as ArticleRepositoryImpl)
  }

  val viewModel by lazy {
    ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_news)


    bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())
  }
}