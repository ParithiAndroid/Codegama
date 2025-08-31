package com.parithidb.cgnews.data.di

import android.content.Context
import com.parithidb.cgnews.data.database.AppDatabase
import com.parithidb.cgnews.data.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object ActivityModule {

    @Provides
    @ActivityRetainedScoped
    fun provideNewsRepository(
        @ApplicationContext context: Context,
        database: AppDatabase
    ): NewsRepository {
        return NewsRepository(context, database)
    }

}