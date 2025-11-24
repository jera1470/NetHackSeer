package com.example.nethackseer.data.local

import androidx.room.TypeConverter
import com.example.nethackseer.data.local.entity.Attack
import com.google.gson.Gson

/**
 * Type converters to allow Room to reference attacks as a simple String.
 */
class Converters {
    @TypeConverter
    fun fromAttack(attack: Attack): String {
        return Gson().toJson(attack)
    }

    @TypeConverter
    fun toAttack(attackString: String): Attack {
        return Gson().fromJson(attackString, Attack::class.java)
    }
}