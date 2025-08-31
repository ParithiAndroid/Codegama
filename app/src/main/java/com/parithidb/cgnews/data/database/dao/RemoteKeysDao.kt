package com.parithidb.cgnews.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.parithidb.cgnews.data.database.entities.RemoteKeysEntity

@Dao
interface RemoteKeysDao {
    @Query("SELECT * FROM remote_keys WHERE articleUrl = :url")
    suspend fun remoteKeysUrl(url: String): RemoteKeysEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<RemoteKeysEntity>)

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}