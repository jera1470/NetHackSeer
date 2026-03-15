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
data class ItemEntity(
    @PrimaryKey
    // both name and descri ption have a macro OBJ() technically
    val name: String,
    val description: String? = null,

    // BITS() macro, relevant bits only
    val merge: Boolean, // can multiple items of the object stack?
    val magicItem: Boolean, // very important, e.g. polypiling
    val charge: Boolean, // can the item be charged or enchanted?
    val unique: Boolean,
    val notWish: Boolean,
    val tough: Boolean, // is it immune to destruction?\
    val dirOrType: Int, // clever use of a bit, saving either damage or direction type
    val subCategory: String, // skills of weapons, spellbooks, etc.
    val material: String,

    val property: String, // extrinsic given by object (or more)
    val symbol: String, // e.g. WEAPON_CLASS for ')' for weapons
    val probability: Int, // out of 1000 (e.g., 175/1000 is its 17.5% relative probability)
    val delay: Int, // turns to put on or off objects, seen with armor
    val weight: Int,
    val value: Int,
    // for the next two fields, any damage bonuses are located in weapon.c for specific weapons
    val smallDamage: Int, // 1d(smallDamage)
    val largeDamage: Int, // 1d(largeDamage)

    // 1st use of oc1 (armor)
    val ac: Int,
    // 2nd use of oc1 (weapon)
    val hitBonus: Int,
    // 1st use of oc2 (armor)
    val magicCancellation: Int, // MC1, 2, or 3
    // 2nd use of oc2 (spellbooks)
    val spellLevel: Int, // up to level 7 spellbooks exist

    val nutrition: Int,
    val color: String
)