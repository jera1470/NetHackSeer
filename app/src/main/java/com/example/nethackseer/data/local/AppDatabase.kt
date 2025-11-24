package com.example.nethackseer.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nethackseer.data.local.dao.NetHackDao
import com.example.nethackseer.data.local.entity.MonsterEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [MonsterEntity::class], version = 1)
abstract class NetHackDatabase : RoomDatabase() {
    abstract fun netHackDao(): NetHackDao

    companion object {
        @Volatile
        private var INSTANCE: NetHackDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): NetHackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NetHackDatabase::class.java,
                    "nethack_database"
                )
                    .addCallback(NetHackDatabaseCallback(context, scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class NetHackDatabaseCallback(
        private val context: Context,
        private val scope: CoroutineScope
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val dao = database.netHackDao()
                    prePopulateMonsters(context, dao)
                }
            }
        }

        private suspend fun prePopulateMonsters(context: Context, dao: NetHackDao){
            val jsonString: String
            try {
                jsonString = context.assets.open("nethack_data/monsters.json").bufferedReader().use{it.readText()}
            } catch (ioException: java.io.IOException) {
                ioException.printStackTrace()
                return
            }

            val jsonArray = org.json.JSONArray(jsonString)
            val monsterList = mutableListOf<MonsterEntity>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("name")
                val symbol = jsonObject.getString("symbol")
                val level = jsonObject.getJSONObject("level_details").getInt("level")

            }
            dao.insertAll(monsterList)
        }
    }
}

