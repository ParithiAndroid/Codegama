package com.parithidb.cgnews.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.parithidb.cgnews.data.database.dao.ArticleDao
import com.parithidb.cgnews.data.database.dao.RemoteKeysDao
import com.parithidb.cgnews.data.database.entities.ArticleEntity
import com.parithidb.cgnews.data.database.entities.RemoteKeysEntity
import com.parithidb.cgnews.util.Converters

@Database(
    entities = [ArticleEntity::class, RemoteKeysEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun remoteKeysDao(): RemoteKeysDao

   suspend fun clearDatabase() {
        clearAllTables()
    }
}