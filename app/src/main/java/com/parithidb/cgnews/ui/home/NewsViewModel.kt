package com.parithidb.cgnews.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.parithidb.cgnews.data.database.entities.ArticleEntity
import com.parithidb.cgnews.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    val topHeadlines: Flow<PagingData<ArticleEntity>> =
        newsRepository.topHeadlinesPager("us", 20)
            .flow
            .cachedIn(viewModelScope)

    fun searchNews(query: String): Flow<PagingData<ArticleEntity>> {
        return newsRepository.searchNews(query)
            .cachedIn(viewModelScope)
    }
}
