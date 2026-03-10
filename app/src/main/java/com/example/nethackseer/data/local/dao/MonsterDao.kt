package com.example.nethackseer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import com.example.nethackseer.data.local.entity.MonsterEntity
import kotlinx.coroutines.flow.Flow

/**
 * The data access object for the NetHack monsters.
 * Defines database interactions (queries, inserts, updates, etc.)
 *
 * @see MonsterEntity
 */
@Dao
interface MonsterDao {

    /**
     * Inserts a list of MonsterEntity objects into the database.
     * If an entity has the same primary key already, then it will be replaced.
     * Must be called from a coroutine or another suspend function
     *
     * @param entities the list of MonsterEntity objects to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<MonsterEntity>)

    /**
     * Selects and returns all entities from the 'monsters' table.
     *
     * @return a Flow of a list of MonsterEntity objects
     */
    @Query("SELECT * FROM monsters ORDER BY name ASC")
    fun getAll(): Flow<List<MonsterEntity>>

    /**
     * Selects monsters based on query given and returns all entities with that given query
     *
     * @return a Flow of a list of MonsterEntity objects with the given query
     */
    @Query("SELECT * FROM monsters WHERE name LIKE :query ORDER BY name ASC")
    fun search(query: String): Flow<List<MonsterEntity>>

    /**
     * Selects and returns the MonsterEntity object with the given name.
     * If no entity is found, returns null.
     */
    @Query("SELECT * FROM monsters WHERE name = :name LIMIT 1")
    fun getMonsterByName(name: String): Flow<MonsterEntity?>
}