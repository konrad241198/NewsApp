package com.jurkowski.newsapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jurkowski.newsapp.database.dao.ArticleDao
import com.jurkowski.newsapp.model.Article

const val DATABASE_VERSION = 1

@Database(
  entities = [Article::class],
  version = DATABASE_VERSION
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

  companion object {
    private const val DATABASE_NAME = "article_db"

    fun buildDatabase(context: Context): ArticleDatabase {
      return Room.databaseBuilder(
        context,
        ArticleDatabase::class.java,
        DATABASE_NAME
      ).build()
    }
  }

  abstract fun articleDao(): ArticleDao
}