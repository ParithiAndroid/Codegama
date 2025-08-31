package com.parithidb.cgnews.data.mediator

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.parithidb.cgnews.data.api.NewsApiService
import com.parithidb.cgnews.data.database.entities.ArticleEntity
import toEntity

class SearchPagingSource(
    private val apiService: NewsApiService,
    private val query: String
) : PagingSource<Int, ArticleEntity>() {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleEntity> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize

            val response = apiService.searchNews(query = query, page = page, pageSize = pageSize)
            val articles = response.articles.mapNotNull { it.toEntity() }

            LoadResult.Page(
                data = articles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (articles.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ArticleEntity>): Int? {
        return state.anchorPosition?.let { anchor ->
            val page = state.closestPageToPosition(anchor)
            page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
        }
    }
}
