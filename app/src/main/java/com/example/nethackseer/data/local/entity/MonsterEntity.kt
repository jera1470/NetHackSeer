package com.example.nethackseer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Attack(
    val type: String,
    val damage_type: String,
    val dice_count: Int,
    val dice_sides: Int
)

@Entity(tableName = "monsters")
data class MonsterEntity(
    @PrimaryKey
    val name: String,
    val symbol: String,

    val level: Int,
    val move_rate: Int,
    val ac: Int,
    val mr: Int,
    val alignment: Int,

    val geno_flags: String,

    val attack1: Attack,
    val attack2: Attack,
    val attack3: Attack,
    val attack4: Attack,
    val attack5: Attack,
    val attack6: Attack,

    val weight: Int,
    val nutritional_value: Int,
    val sound: String,
    val size: String,

    val resistances: String,
    val resistances_conferred: String,
    val m1_flags: String,
    val m2_flags: String,
    val m3_flags: String,
    val difficulty: Int,
    val color: String
)