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

    /**
     * Inserts a list of ItemEntity objects into the database.
     * If an entity has the same primary key already, then it will be replaced.
     * Must be called from a coroutine or another suspend function
     *
     * @param entities the list of ItemEntity objects to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<ItemEntity>)

    /**
     * Selects and returns all entities from the 'items' table.
     *
     * @return a Flow of a list of ItemEntity objects
     */
    @Query("SELECT * FROM items ORDER BY name ASC")
    fun getAll(): Flow<List<ItemEntity>>

    /**
     * Selects and returns all item names.
     */
    @Query("SELECT name FROM items ORDER BY name ASC")
    fun getAllNames(): Flow<List<String>>

    /**
     * Selects items based on query given and returns all entities with that given query
     *
     * @return a Flow of a list of ItemEntity objects with the given query
     */
    @Query("SELECT * FROM items WHERE name LIKE :query ORDER BY name ASC")
    fun search(query: String): Flow<List<ItemEntity>>

    /**
     * Selects and returns the ItemEntity object with the given name.
     * If no entity is found, returns null.
     *
     * @param name the name of the ItemEntity object to select
     */
    @Query("SELECT * FROM items WHERE name = :name LIMIT 1")
    fun getItemByName(name: String): Flow<ItemEntity?>
}