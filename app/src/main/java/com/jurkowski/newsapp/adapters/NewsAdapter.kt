package com.jurkowski.newsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jurkowski.newsapp.R
import com.jurkowski.newsapp.model.Article
import kotlinx.android.synthetic.main.article_item.view.*

class NewsAdapter(): RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

  inner class ArticleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

  }

  private val differCallback = object : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
      return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
      return oldItem == newItem
    }
  }

  val differ = AsyncListDiffer(this, differCallback)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
    return ArticleViewHolder(LayoutInflater.from(parent.context).inflate(
      R.layout.article_item,
      parent,
      false))
  }

  override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
    val article = differ.currentList[position]
    holder.itemView.apply {
      Glide.with(this).load(article.urlToImage).into(articleImageView)
      articleTitle.text = article.title
      articleDescription.text = article.description
      articleSource.text = article.source?.name
      articlePublished.text = article.publishedAt
      setOnClickListener {
        onItemClickListener?.let { it(article) }
      }
    }
  }

  override fun getItemCount(): Int {
    return differ.currentList.size
  }

  private var onItemClickListener: ((Article) -> Unit)? = null

  fun setOnItemClickListener(listener: (Article) -> Unit) {
    onItemClickListener = listener
  }
}