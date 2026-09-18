package com.example.nethackseer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// ATTK() macro, for the attacks of the monster
data class Attack(
    val type: String, // type of contact (e.g. normal, gaze, touch...)
    val damageType: String, // type of damage (e.g. cold, fire, physical...)
    val diceCount: Int,
    val diceSides: Int
)

/**
 * An entity representing a monster in the database.
 */
@Entity(tableName = "monsters")
data class MonsterEntity(
    @PrimaryKey
    val name: String,
    val symbol: String, // e.g, S_ANT for 'a' (ants)

    val level: Int, // monsters lvl 50 or above have different HP calc
    val moveRate: Int,
    val ac: Int,
    val mr: Int, // monster magic resistance (different from MR from player)
    val alignment: Int,

    val genoFlags: String, // flags for creation and genocide

    // all six ATTK() structs possible
    val attack1: Attack,
    val attack2: Attack,
    val attack3: Attack,
    val attack4: Attack,
    val attack5: Attack,
    val attack6: Attack,

    val weight: Int,
    val nutritionValue: Int,
    val sound: String, // MS_SILENT, MS_BUZZ, etc.
    val size: String,

    val resistances: String,
    val resistancesConferred: String,
    val m1Flags: String, // flags for physical traits (e.g. animal, fly...)
    val m2Flags: String, // flags for behavioral traits (e.g. hostile, peaceful...)
    val m3Flags: String, // flags for mental/misc traits (e.g. infravision...)
    val difficulty: Int,
    val color: String,
    val maleName: String? = null,
    val femaleName: String? = null,
    // effects from eating before, during, and after corpse consumption
    val corpseEffects: String = "0",
    // specific effects outside m1, 2, 3 flags
    val extraEffects: String = "0"
)