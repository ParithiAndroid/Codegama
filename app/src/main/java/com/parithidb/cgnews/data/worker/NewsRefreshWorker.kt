package com.parithidb.cgnews.data.worker

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.parithidb.cgnews.data.repository.NewsRepository

class NewsRefreshWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: NewsRepository
) : CoroutineWorker(context, params) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        return try {
            repository.refreshTopHeadlines()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
