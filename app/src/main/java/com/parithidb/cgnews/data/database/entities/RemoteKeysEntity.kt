package com.parithidb.cgnews.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "REMOTE_KEYS")
data class RemoteKeysEntity(
    @PrimaryKey val articleUrl: String,
    val prevKey: Int?,
    val nextKey: Int?
)