package com.example.nethackseer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import com.example.nethackseer.data.local.entity.NetHackEntity
import kotlinx.coroutines.flow.Flow

/**
 * The data access object for the NetHack entity.
 * Defines database interactions (queries, inserts, updates, etc.)
 *
 * @see NetHackEntity
 */
@Dao
interface NetHackDao {

    /**
     * Inserts a list of NetHackEntity objects into the database.
     * If an entity has the same primary key already, then it will be replaced.
     * Must be called from a coroutine or another suspend function
     *
     * @param entities the list of NetHackEntity objects to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<NetHackEntity>)

    /**
     * Selects and returns all entities from the 'nethack_entities' table.
     *
     * @return a Flow of a list of NetHackEntity objects
     */
    @Query("SELECT * FROM nethack_entities")
    fun getAll(): Flow<List<NetHackEntity>>

}