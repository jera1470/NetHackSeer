package com.example.nethackseer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nethack_entities")
data class NetHackEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val type: String
)