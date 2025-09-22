package com.example.nethackseer

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nethackseer.data.local.dao.NetHackDao
import com.example.nethackseer.data.local.entity.NetHackEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [NetHackEntity::class], version = 1)
abstract class NetHackDatabase : RoomDatabase() {
    abstract fun netHackDao(): NetHackDao

    companion object {
        @Volatile
        private var INSTANCE: NetHackDatabase? = null

        fun getDatabase(context: Context): NetHackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NetHackDatabase::class.java,
                    "nethack_database"
                )
                    .addCallback(NetHackDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class NetHackDatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = database.netHackDao()
                    // Pre-populate the database with initial data
                    val initialEntities = listOf(
                        NetHackEntity(
                            id = "lichen",
                            name = "lichen",
                            description = "A type of slow-moving, plant-like fungus. It is weak and can be killed easily. Its only attack is to stick on you.",
                            type = "Monster"
                        ),
                        NetHackEntity(
                            id = "ring of conflict",
                            name = "ring of conflict",
                            description = "A powerful magical ring. While worn, it causes all monsters in sight to fight each other instead of attacking you.",
                            type = "Item"
                        ),
                        NetHackEntity(
                            id = "goblin",
                            name = "goblin",
                            description = "A small and weak humanoid monster, often found in the early levels of the dungeon.",
                            type = "Monster"
                        ),
                        NetHackEntity(
                            id = "magic lamp",
                            name = "magic lamp",
                            description = "A rare item that contains a djinni for the chance to get a wish! Should bless it for a increased 80% chance to get a wish.",
                            type = "Item"
                        )
                    )
                    dao.insertAll(initialEntities)
                }
            }
        }
    }
}