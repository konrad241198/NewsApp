package com.jurkowski.newsapp.ui

import android.app.Application
import android.net.ConnectivityManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jurkowski.newsapp.App
import com.jurkowski.newsapp.api.NetworkStatusChecker
import com.jurkowski.newsapp.model.Article
import com.jurkowski.newsapp.model.NewsResponse
import com.jurkowski.newsapp.repository.ArticleRepositoryImpl
import com.jurkowski.newsapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
  app: Application,
  private val repository: ArticleRepositoryImpl
) : AndroidViewModel(app) {

  private val networkStatusChecker by lazy {
    NetworkStatusChecker(getApplication<App>().getSystemService(ConnectivityManager::class.java))
  }

  val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
  var breakingNewsPage = 1
  var breakingNewsResponse: NewsResponse? = null

  val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
  var searchNewsPage = 1
  var searchNewsResponse: NewsResponse? = null

  init {
    getBreakingNews("pl")
  }

  fun getBreakingNews(countryCode: String) = viewModelScope.launch {
    safeBreakingNewsCall(countryCode)
  }

  fun searchNews(searchQuery: String) = viewModelScope.launch {
    safeSearchNewsCall(searchQuery)
  }

  private suspend fun safeBreakingNewsCall(countryCode: String) {
    breakingNews.postValue(Resource.Loading())
    try {
      if (networkStatusChecker.hasInternetConnection()) {
        val response = repository.getBreakingNews(countryCode, breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response))
      } else {
        breakingNews.postValue(Resource.Error("No internet connction"))
      }
    } catch (t: Throwable) {
      when (t) {
        is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
        else -> breakingNews.postValue(Resource.Error("Conversion Error"))
      }
    }
  }

  private suspend fun safeSearchNewsCall(searchQuery: String) {
    searchNews.postValue(Resource.Loading())
    try {
      if (networkStatusChecker.hasInternetConnection()) {
        val response = repository.searchNews(searchQuery, searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
      } else {
        searchNews.postValue(Resource.Error("No internet connction"))
      }
    } catch (t: Throwable) {
      when (t) {
        is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
        else -> searchNews.postValue(Resource.Error("Conversion Error"))
      }
    }
  }

  private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
    if (response.isSuccessful) {
      response.body()?.let { resultResponse ->
        breakingNewsPage++
        if (breakingNewsResponse == null) {
          breakingNewsResponse = resultResponse
        } else {
          val oldArticles = breakingNewsResponse?.articles
          val newArticles = resultResponse.articles
          oldArticles?.addAll(newArticles)
        }
        return Resource.Success(breakingNewsResponse ?: resultResponse)
      }
    }
    return Resource.Error(response.message())
  }

  private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
    if (response.isSuccessful) {
      response.body()?.let { resultResponse ->
        searchNewsPage++
        if (searchNewsResponse == null) {
          searchNewsResponse = resultResponse
        } else {
          val oldArticles = searchNewsResponse?.articles
          val newArticles = resultResponse.articles
          oldArticles?.addAll(newArticles)
        }
        return Resource.Success(searchNewsResponse ?: resultResponse)
      }
    }
    return Resource.Error(response.message())
  }

  fun saveArticle(article: Article) = viewModelScope.launch {
    repository.upsert(article)
  }

  fun getSavedNews() = repository.getSavedNews()

  fun deleteArticle(article: Article) = viewModelScope.launch {
    repository.deleteArticle(article)
  }
}