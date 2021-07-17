package com.jurkowski.newsapp.repository

import androidx.lifecycle.LiveData
import com.jurkowski.newsapp.model.NewsResponse
import com.jurkowski.newsapp.model.Article
import retrofit2.Response

interface ArticleRepository {

  suspend fun getBreakingNews(countryCode: String, pageNumber: Int): Response<NewsResponse>

  suspend fun searchNews(searchQuery: String, pageNumber: Int): Response<NewsResponse>

  suspend fun upsert(article: Article)

  fun getSavedNews(): LiveData<List<Article>>

  suspend fun deleteArticle(article: Article)
}