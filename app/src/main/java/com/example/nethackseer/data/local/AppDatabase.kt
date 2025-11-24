package com.example.nethackseer.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nethackseer.data.local.dao.MonsterDao
import com.example.nethackseer.data.local.entity.Attack
import com.example.nethackseer.data.local.entity.MonsterEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.IOException

@Database(entities = [MonsterEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun monsterDao(): MonsterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nethack_database"
                )
                    .fallbackToDestructiveMigration(false)
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
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val dao = database.monsterDao()
                    prePopulateMonsters(context, dao)
                }
            }
        }

        private suspend fun prePopulateMonsters(context: Context, dao: MonsterDao){
            val jsonString: String
            try {
                jsonString = context.assets.open("nethack_data/monsters.json").bufferedReader().use{it.readText()}
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                return
            }

            val jsonArray = JSONArray(jsonString)


            val monsterList = mutableListOf<MonsterEntity>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)

                // no attack val for getting rid of the rest of the attacks
                val noAttack = Attack("NO_ATTK", "AD_NONE", 0, 0)
                val attacksJSON = jsonObject.getJSONArray("attacks")
                val attacks = List(6) { index ->
                    if (index < attacksJSON.length()) {
                        val attackObject = attacksJSON.getJSONObject(index)
                        val type = attackObject.getString("type")
                        if (type != "NO_ATTK"){
                            Attack(
                                type = type,
                                damage_type = attackObject.getString("damage_type"),
                                dice_count = attackObject.getInt("dice_count"),
                                dice_sides = attackObject.getInt("dice_sides")
                            )
                        }
                        else {
                            noAttack
                        }
                    }
                    else {
                        noAttack // only two monsters have no attacks, so this applies to them
                    }
                }

                val levelDetails = jsonObject.getJSONObject("level_details")
                val sizeDetails = jsonObject.getJSONObject("size_details")
                val name = jsonObject.getString("name")

                val monsterEntity = MonsterEntity(
                    name = name, // primary key for the repo
                    symbol = jsonObject.getString("symbol"),
                    level = levelDetails.getInt("level"),
                    move_rate = levelDetails.getInt("move_rate"),
                    ac = levelDetails.getInt("ac"),
                    mr = levelDetails.getInt("mr"),
                    alignment = levelDetails.getInt("alignment"),
                    geno_flags = jsonObject.getString("geno_flags"),
                    attack1 = attacks[0],
                    attack2 = attacks[1],
                    attack3 = attacks[2],
                    attack4 = attacks[3],
                    attack5 = attacks[4],
                    attack6 = attacks[5],
                    weight = sizeDetails.getInt("weight"),
                    nutritional_value = sizeDetails.getInt("nutritional_value"),
                    sound = sizeDetails.getString("sound"),
                    size = sizeDetails.getString("size"),
                    resistances = jsonObject.getString("resistances"),
                    resistances_conferred = jsonObject.getString("resistances_conferred"),
                    m1_flags = jsonObject.getString("m1_flags"),
                    m2_flags = jsonObject.getString("m2_flags"),
                    m3_flags = jsonObject.getString("m3_flags"),
                    difficulty = jsonObject.getInt("difficulty"),
                    color = jsonObject.getString("color"),
                )

                monsterList.add(monsterEntity)
                print(monsterEntity.name)
            }
            dao.insertAll(monsterList)
        }
    }
}

