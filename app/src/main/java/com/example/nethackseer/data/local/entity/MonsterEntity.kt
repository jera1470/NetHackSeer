package com.example.nethackseer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Attack(
    val type: String,
    val damageType: String,
    val diceCount: Int,
    val diceSides: Int
)

@Entity(tableName = "monsters")
data class MonsterEntity(
    @PrimaryKey
    val name: String,
    val symbol: String,

    val level: Int,
    val moveRate: Int,
    val ac: Int,
    val mr: Int,
    val alignment: Int,

    val genoFlags: String,

    val attack1: Attack,
    val attack2: Attack,
    val attack3: Attack,
    val attack4: Attack,
    val attack5: Attack,
    val attack6: Attack,

    val weight: Int,
    val nutritionValue: Int,
    val sound: String,
    val size: String,

    val resistances: String,
    val resistancesConferred: String,
    val m1Flags: String,
    val m2Flags: String,
    val m3Flags: String,
    val difficulty: Int,
    val color: String
)