package com.example.nethackseer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
   in NetHack's objects.c, the file uses BITS() and OBJECT(), which
   are macros that the file as the template for all objects (or items,
   I guess). There are many bits in BITS() that will not see use
   in this app, mainly because they are unnecessary or not relevant
   to the app
 */
@Entity(tableName = "items")
data class `ItemEntity.kt`(
    @PrimaryKey
    // both name and description have a macro OBJ() technically
    val name: String, val description: String? = null,

    // BITS() macro, relevant bits only
    val merge: Boolean, // can multiple items of the object stack?
    val magicItem: Boolean, // very important, e.g. polypiling
    val charge: Boolean, // can the item be charged or enchanted?
    val unique: Boolean,
    val notWish: Boolean,
    val tough: Boolean, // is it immune to destruction?\
    /*
        this one is actually well put. the devs saved memory by
        putting this bit as the damage type (pierce, slash, whack)
        AND also the direction type (no dir, immediate, ray)
     */
    val dirOrType: Int, val subCategory: String, // skills of weapons, spellbooks, etc.
    val material: String,

    val property: String, // extrinsic given by object (or more)
    val symbol: String, // e.g. WEAPON_CLASS for ')' for weapons
    val probability: Int, // out of 1000
    val delay: Int, // turns to put on or off objects
    val weight: Int, val value: Int, val smallDamage: Int, // 1d(smallDamage)
    val largeDamage: Int, // 1d(largeDamage)

    // part of oc1 (armor)
    val ac: Int? = null,
    // part of oc1 (weapon)
    val hitBonus: Int? = null,
    // part of oc2 (armor)
    val magicCancellation: Int? = null, // MC1, 2, or 3
    // part of oc2 (spellbook)
    val spellLevel: Int? = null, // up to level 7 spellbooks exist

    val nutrition: Int, val color: String
)