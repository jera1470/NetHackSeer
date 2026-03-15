package com.example.nethackseer.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.nethackseer.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * The data access object for the NetHack objects.
 * Defines database interactions like with MonsterDao.
 */
@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<ItemEntity>)
}