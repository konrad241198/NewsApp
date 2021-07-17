package com.jurkowski.newsapp.repository

import androidx.lifecycle.LiveData
import com.jurkowski.newsapp.model.NewsResponse
import com.jurkowski.newsapp.App
import com.jurkowski.newsapp.model.Article
import retrofit2.Response

class ArticleRepositoryImpl : ArticleRepository {

  private val api by lazy { App.remoteApi }
  private val database by lazy { App.database }

  override suspend fun getBreakingNews(
    countryCode: String,
    pageNumber: Int
  ): Response<NewsResponse> {
    return api.getBreakingNews(countryCode, pageNumber)
  }

  override suspend fun searchNews(searchQuery: String, pageNumber: Int): Response<NewsResponse> {
    return api.searchForNews(searchQuery, pageNumber)
  }

  override suspend fun upsert(article: Article) {
    database.articleDao().upsert(article)
  }

  override fun getSavedNews(): LiveData<List<Article>> {
    return database.articleDao().getAllArticles()
  }

  override suspend fun deleteArticle(article: Article) {
    database.articleDao().deleteArticle(article)
  }
}