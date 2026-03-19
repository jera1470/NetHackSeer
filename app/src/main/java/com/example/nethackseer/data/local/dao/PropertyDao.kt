package com.example.nethackseer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nethackseer.data.local.entity.PropertyEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for the properties table in the database.
 *
 * @see PropertyEntity
 */
@Dao
interface PropertyDao {
    /**
     * Retrieves a property by its ID.
     *
     * @param id The ID of the property to retrieve.
     * @return The property with the specified ID, or null if not found.
     */
    @Query("SELECT * FROM properties WHERE id = :id")
    suspend fun getPropertyById(id: String): PropertyEntity?

    /**
     * Retrieves a list of properties by their IDs.
     *
     * @param ids The list of IDs of the properties to retrieve.
     * @return A flow emitting a list of properties with the specified IDs.
     */
    @Query("SELECT * FROM properties WHERE id IN (:ids)")
    fun getPropertiesByIds(ids: List<String>): Flow<List<PropertyEntity>>

    /**
     * Inserts a list of properties into the database.
     *
     * @param properties The list of properties to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperties(properties: List<PropertyEntity>)

    /**
     * Retrieves a list of all properties in the database.
     *
     * @return A flow emitting a list of all properties.
     */
    @Query("SELECT * FROM properties")
    fun getAllProperties(): Flow<List<PropertyEntity>>
}
