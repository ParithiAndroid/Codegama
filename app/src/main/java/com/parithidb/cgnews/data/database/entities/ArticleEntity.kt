package com.parithidb.cgnews.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ARTICLES")
data class ArticleEntity(
    @PrimaryKey val url: String,
    val title: String?,
    val description: String?,
    val imageUrl: String?,
    val sourceName: String?,
    val publishedAt: Long,
    val content: String?
)