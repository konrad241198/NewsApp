package com.jurkowski.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jurkowski.newsapp.R
import com.jurkowski.newsapp.adapters.NewsAdapter
import com.jurkowski.newsapp.ui.NewsActivity
import com.jurkowski.newsapp.ui.NewsViewModel
import com.jurkowski.newsapp.utils.Constants.BREAKING_NEWS_TAG
import com.jurkowski.newsapp.utils.Constants.QUERY_PAGE_SIZE
import com.jurkowski.newsapp.utils.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

  private lateinit var viewModel: NewsViewModel
  private lateinit var newsAdapter: NewsAdapter
  private var isLoading = false
  private var isLastPage = false
  private var isScrolling = false

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel = (activity as NewsActivity).viewModel
    setupRecyclerView()

    newsAdapter.setOnItemClickListener {
      val bundle = Bundle().apply {
        putSerializable("article", it)
      }
      findNavController().navigate(
        R.id.action_breakingNewsFragment_to_articleFragment,
        bundle
      )
    }

    viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
      when (response) {
        is Resource.Success -> {
          hideProgressBar()
          response.data?.let { newsResponse ->
            newsAdapter.differ.submitList(newsResponse.articles.toList())
            val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
            isLastPage = viewModel.breakingNewsPage == totalPages
            if (isLastPage) {
              breakingNewsRecyclerView.setPadding(0, 0, 0 ,0)
            }
          }
        }
        is Resource.Error -> {
          hideProgressBar()
          response.message?.let { message ->
            Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_SHORT).show()
            Log.e(BREAKING_NEWS_TAG, "An error occured: $message")
          }
        }
        is Resource.Loading -> {
          showProgressBar()
        }
      }
    })
  }

  private fun hideProgressBar() {
    breakingNewsProgressBar.visibility = View.INVISIBLE
    isLoading = false
  }

  private fun showProgressBar() {
    breakingNewsProgressBar.visibility = View.VISIBLE
    isLoading = true
  }

  private fun setupRecyclerView() {
    newsAdapter = NewsAdapter()
    breakingNewsRecyclerView.apply {
      adapter = newsAdapter
      layoutManager = LinearLayoutManager(activity)
      addOnScrollListener(scrollListener)
    }
  }

  private val scrollListener = object : RecyclerView.OnScrollListener() {
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
      super.onScrollStateChanged(recyclerView, newState)
      if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
        isScrolling = true
      }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
      super.onScrolled(recyclerView, dx, dy)

      val layoutManager = recyclerView.layoutManager as LinearLayoutManager
      val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
      val visibleItemCount = layoutManager.childCount
      val totalItemCount = layoutManager.itemCount

      val isNotLoadingAndLastPage = !isLoading && !isLastPage
      val isAtLisItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
      val isNotAtBeginning = firstVisibleItemPosition >= 0
      val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
      val shouldPaginate = isNotLoadingAndLastPage && isAtLisItem && isNotAtBeginning
          && isTotalMoreThanVisible && isScrolling

      if (shouldPaginate) {
        viewModel.getBreakingNews("pl")
        isScrolling = false
      }
    }
  }
}