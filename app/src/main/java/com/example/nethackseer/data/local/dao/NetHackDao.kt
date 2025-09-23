package com.example.nethackseer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import com.example.nethackseer.data.local.entity.NetHackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NetHackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<NetHackEntity>)

    @Query("SELECT * FROM nethack_entities")
    fun getAll(): Flow<List<NetHackEntity>>

}