package com.example.nethackseer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * An entity representing a property in the database (e.g., M1_ANIMAL, MR_FIRE).
 */
@Entity(tableName = "properties")
data class PropertyEntity(
    @PrimaryKey
    val id: String, // e.g., "M1_ANIMAL" or "MR_FIRE"
    val name: String, // more descriptive name, e.g., "Animal" or "Fire resistance"
    val description: String
)
