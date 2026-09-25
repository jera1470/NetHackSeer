package com.example.nethackseer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * An entity representing an artifact in the database.
 */
@Entity(tableName = "artifacts")
data class ArtifactEntity(
    @PrimaryKey
    val name: String,
    val baseItem: String,
    val alignment: String,
    val role: String? = null,
    val wieldedProperties: String,
    val carriedProperties: String,
    val attackToHitBonus: String? = null,
    val attackModifier: String? = null,
    val attackTypeModifier: String? = null,
    val damageTarget: String? = null, // target creature type/class if conditional
    val invokedPower: String? = null // will make separate summaries of invoked powers later
)
