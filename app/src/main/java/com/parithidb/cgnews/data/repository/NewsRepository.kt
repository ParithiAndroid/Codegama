package com.parithidb.cgnews.data.repository

import ArticlesRemoteMediator
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.parithidb.cgnews.data.api.NewsApiService
import com.parithidb.cgnews.data.api.RetrofitClient
import com.parithidb.cgnews.data.database.AppDatabase
import com.parithidb.cgnews.data.database.entities.ArticleEntity
import com.parithidb.cgnews.data.mediator.SearchPagingSource
import kotlinx.coroutines.flow.Flow
import toEntity
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val context: Context,
    private val database: AppDatabase
) {
    private val newsApiService = RetrofitClient.getInstance(context).getNewsApiService()
    @OptIn(ExperimentalPagingApi::class)
    fun topHeadlinesPager(country: String = "us", pageSize: Int = 20): Pager<Int, ArticleEntity> {
        return Pager(
            config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
            remoteMediator = ArticlesRemoteMediator(database, newsApiService, country, pageSize),
            pagingSourceFactory = { database.articleDao().pagingSource() }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun refreshTopHeadlines() {
        val response = newsApiService.topHeadlines(country = "us", page = 1, pageSize = 20)
        val articles = response.articles.mapNotNull { it.toEntity() }
        database.articleDao().insertAll(articles)
    }

    fun searchNews(query: String): Flow<PagingData<ArticleEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { SearchPagingSource(newsApiService, query) }
        ).flow
    }
}