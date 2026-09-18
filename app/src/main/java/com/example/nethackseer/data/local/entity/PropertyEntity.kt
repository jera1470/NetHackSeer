package com.example.nethackseer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * An entity representing a property in the database.
 */
@Entity(tableName = "properties")
data class PropertyEntity(
    @PrimaryKey
    val id: String, // e.g., "M1_ANIMAL" or "MR_FIRE"
    val name: String,
    val type: String,
    val description: String, // long description for glossary stuff
    val summary: String // short summary of the property, used in bullet points
)
