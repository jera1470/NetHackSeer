package com.example.nethackseer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nethackseer.data.local.entity.ArtifactEntity
import kotlinx.coroutines.flow.Flow

/**
 * The data access object for NetHack artifacts.
 *
 * @see ArtifactEntity
 */
@Dao
interface ArtifactDao {

    /**
     * Inserts a list of ArtifactEntity objects into the database.
     * If an entity has the same primary key already, it will be replaced.
     * Must be called from a coroutine or another suspend function.
     *
     * @param entities the list of ArtifactEntity objects to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<ArtifactEntity>)

    /**
     * Selects and returns all entities from the 'artifacts' table.
     *
     * @return a Flow of a list of ArtifactEntity objects
     */
    @Query("SELECT * FROM artifacts ORDER BY name ASC")
    fun getAll(): Flow<List<ArtifactEntity>>

    /**
     * Selects and returns all artifact names.
     */
    @Query("SELECT name FROM artifacts ORDER BY name ASC")
    fun getAllNames(): Flow<List<String>>

    /**
     * Selects artifacts based on query given and returns all entities matching that query.
     *
     * @param query the query to search for
     * @return a Flow of a list of ArtifactEntity objects matching the query
     */
    @Query("SELECT * FROM artifacts WHERE name LIKE :query ORDER BY name ASC")
    fun search(query: String): Flow<List<ArtifactEntity>>

    /**
     * Selects and returns the ArtifactEntity object with the given name.
     * If no entity is found, returns null.
     *
     * @param name the name of the ArtifactEntity object to select
     * @return a Flow of the ArtifactEntity object with the given name, or null if not found
     */
    @Query("SELECT * FROM artifacts WHERE name = :name LIMIT 1")
    fun getArtifactByName(name: String): Flow<ArtifactEntity?>
}
