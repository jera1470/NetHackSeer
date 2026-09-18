package com.example.nethackseer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * An entity representing an item in the database.
 */
@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey
    val name: String,
    val description: String? = null, // unidentified name of the object

    val merge: Boolean, // can be stacked together if entirely the same
    val magicItem: Boolean, // important for polypiling
    val charge: Boolean,
    val unique: Boolean,
    val notWish: Boolean,
    val tough: Boolean, // immune to destruction
    val damageType: String,
    val zapDirection: String, // ray type produced from any object (e.g. wands)
    val subCategory: String, // skills of weapons, spellbooks, etc.
    val material: String,

    val property: String,
    val symbol: String,
    val probability: Int, // out of 1000 (e.g., 175/1000 is its 17.5% *relative* probability)
    val delay: Int, // turns to put on or off armor, or turns to read a spellbook
    val weight: Int,
    val value: Int,
    // TODO: any damage bonuses are located in weapon.c for specific weapons, need to implement
    val smallDamage: Int, // 1d(smallDamage)
    val largeDamage: Int, // 1d(largeDamage)

    val ac: Int,
    val hitBonus: Int,
    val magicCancellation: Int,
    val spellLevel: Int,

    val nutrition: Int,
    val color: String
)