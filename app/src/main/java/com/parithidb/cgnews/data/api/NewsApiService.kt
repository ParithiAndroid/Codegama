package com.parithidb.cgnews.data.api

import com.parithidb.cgnews.data.api.model.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("top-headlines")
    suspend fun topHeadlines(
        @Query("country") country: String = "in",
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): NewsResponse

    @GET("everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("language") language: String = "en",
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): NewsResponse
}