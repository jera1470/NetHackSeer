package com.example.nethackseer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// ATTK() macro, for the attacks of the monster
data class Attack(
    val type: String, // type of contact (normal, gaze, touch...)
    val damageType: String, // type of damage (cold, fire, physical...)
    val diceCount: Int, // number of dice (3 in 3d6)
    val diceSides: Int // number of sides (6 in 3d6)
)

/**
 * An entity representing a monster in the database.
 */
@Entity(tableName = "monsters")
data class MonsterEntity(
    @PrimaryKey
    val name: String,
    val symbol: String, // e.g, S_ANT for 'a' for ants

    // LVL() macro, for combat and movement stats
    val level: Int, // monsters lvl 50 or above have different HP calc.
    val moveRate: Int, // 12 is normal speed
    val ac: Int, // 10 is bare AC, lower AC is better
    val mr: Int, // magic resistance (different from MR from player)
    val alignment: Int, // lawful, neutral, chaotic, or unaligned

    val genoFlags: String, // flags for creation and genocide

    // A() macro, a wrapper for all six ATTK() structs
    val attack1: Attack,
    val attack2: Attack,
    val attack3: Attack,
    val attack4: Attack,
    val attack5: Attack,
    val attack6: Attack,

    // SIZ() macro, for the body of the monster
    val weight: Int,
    val nutritionValue: Int,
    val sound: String, // MS_SILENT, MS_BUZZ, etc.
    val size: String, // from tiny to gargantuan

    val resistances: String,
    val resistancesConferred: String,
    val m1Flags: String, // flags for physical traits (animal, fly...)
    val m2Flags: String, // flags for behavioral traits (hostile, peaceful...)
    val m3Flags: String, // flags for mental/misc traits (infravision...)
    val difficulty: Int,
    val color: String
)