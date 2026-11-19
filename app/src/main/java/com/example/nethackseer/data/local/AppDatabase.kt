package com.example.nethackseer.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nethackseer.data.local.dao.ArtifactDao
import com.example.nethackseer.data.local.dao.ItemDao
import com.example.nethackseer.data.local.dao.MonsterDao
import com.example.nethackseer.data.local.dao.PropertyDao
import com.example.nethackseer.data.local.entity.ArtifactEntity
import com.example.nethackseer.data.local.entity.Attack
import com.example.nethackseer.data.local.entity.ItemEntity
import com.example.nethackseer.data.local.entity.MonsterEntity
import com.example.nethackseer.data.local.entity.PropertyEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.IOException

@Database(
    entities = [MonsterEntity::class, ItemEntity::class, PropertyEntity::class, ArtifactEntity::class],
    version = 9,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun monsterDao(): MonsterDao
    abstract fun itemDao(): ItemDao
    abstract fun propertyDao(): PropertyDao
    abstract fun artifactDao(): ArtifactDao

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
                    .fallbackToDestructiveMigration(true)
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
                    // Clear tables first so "ghost" entries from old/buggy JSONs are removed
                    // TODO: remove this when releasing app
                    db.execSQL("DELETE FROM monsters")
                    db.execSQL("DELETE FROM items")
                    db.execSQL("DELETE FROM properties")
                    db.execSQL("DELETE FROM artifacts")

                    val monsterDao = database.monsterDao()
                    prePopulateMonsters(context, monsterDao)
                    val itemDao = database.itemDao()
                    prePopulateItem(context, itemDao)
                    val propertyDao = database.propertyDao()
                    prePopulateProperties(context, propertyDao)
                    val artifactDao = database.artifactDao()
                    prePopulateArtifacts(context, artifactDao)
                }
            }
        }

        private suspend fun prePopulateMonsters(context: Context, dao: MonsterDao) {
            val jsonString: String
            try {
                jsonString = context.assets.open("nethack_data/nethack500/monsters500.json").bufferedReader()
                    .use { it.readText() }
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
                        if (type != "NO_ATTK") {
                            Attack(
                                type = type,
                                damageType = attackObject.getString("damage_type"),
                                diceCount = attackObject.getInt("dice_count"),
                                diceSides = attackObject.getInt("dice_sides")
                            )
                        } else {
                            noAttack
                        }
                    } else {
                        noAttack // only two monsters have no attacks, so this applies to them
                    }
                }

                val levelDetails = jsonObject.getJSONObject("level_details")
                val sizeDetails = jsonObject.getJSONObject("size_details")
                val name = jsonObject.getString("name")

                val sizeRaw = sizeDetails.getString("size")
                val monsterEntity = MonsterEntity(
                    name = name, // primary key for the repo
                    symbol = jsonObject.getString("symbol"),
                    level = levelDetails.getInt("level"),
                    moveRate = levelDetails.getInt("move_rate"),
                    ac = levelDetails.getInt("ac"),
                    mr = levelDetails.getInt("mr"),
                    alignment = levelDetails.getInt("alignment"),
                    genoFlags = jsonObject.getString("geno_flags"),
                    attack1 = attacks[0],
                    attack2 = attacks[1],
                    attack3 = attacks[2],
                    attack4 = attacks[3],
                    attack5 = attacks[4],
                    attack6 = attacks[5],
                    weight = sizeDetails.getInt("weight"),
                    nutritionValue = sizeDetails.getInt("nutritional_value"),
                    sound = sizeDetails.getString("sound"),
                    size = if (sizeRaw == "MZ_HUMAN") "MZ_MEDIUM" else sizeRaw,
                    resistances = jsonObject.getString("resistances"),
                    resistancesConferred = jsonObject.getString("resistances_conferred"),
                    m1Flags = jsonObject.getString("m1_flags"),
                    m2Flags = jsonObject.getString("m2_flags"),
                    m3Flags = jsonObject.getString("m3_flags"),
                    difficulty = jsonObject.getInt("difficulty"),
                    color = jsonObject.getString("color"),
                    maleName = if (jsonObject.has("male_name")) jsonObject.getString("male_name") else null,
                    femaleName = if (jsonObject.has("female_name")) jsonObject.getString("female_name") else null
                )

                monsterList.add(monsterEntity)
            }
            dao.insertAll(monsterList)
        }

        private suspend fun prePopulateItem(context: Context, dao: ItemDao){
            val jsonString: String
            try {
                jsonString = context.assets.open("nethack_data/nethack500/objects500.json").bufferedReader()
                    .use { it.readText() }
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                return
            }

            val jsonArray = JSONArray(jsonString)
            val itemList = mutableListOf<ItemEntity>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)

                val itemEntity = ItemEntity(
                    name = jsonObject.getString("name"),
                    description = if (jsonObject.isNull("description")) null else jsonObject.optString("description"),
                    merge = jsonObject.getBoolean("merge"),
                    magicItem = jsonObject.getBoolean("magicItem"),
                    charge = jsonObject.getBoolean("charge"),
                    unique = jsonObject.getBoolean("unique"),
                    notWish = jsonObject.getBoolean("notWish"),
                    tough = jsonObject.getBoolean("tough"),
                    damageType = if (jsonObject.isNull("damageType")) "0" else jsonObject.optString("damageType"),
                    zapDirection = if (jsonObject.isNull("zapDirection")) "0" else jsonObject.optString("zapDirection"),
                    subCategory = jsonObject.getString("subCategory"),
                    material = jsonObject.getString("material"),
                    property = if (jsonObject.isNull("property")) "0" else jsonObject.optString("property"),
                    symbol = jsonObject.getString("symbol"),
                    probability = jsonObject.getInt("probability"),
                    delay = jsonObject.getInt("delay"),
                    weight = jsonObject.getInt("weight"),
                    value = jsonObject.getInt("value"),
                    smallDamage = jsonObject.getInt("smallDamage"),
                    largeDamage = jsonObject.getInt("largeDamage"),
                    ac = jsonObject.getInt("ac"),
                    hitBonus = jsonObject.getInt("hitBonus"),
                    magicCancellation = jsonObject.getInt("magicCancellation"),
                    spellLevel = jsonObject.getInt("spellLevel"),
                    nutrition = jsonObject.getInt("nutrition"),
                    color = jsonObject.getString("color")
                )

                itemList.add(itemEntity)
            }
            dao.insertAll(itemList)
        }

        private suspend fun prePopulateProperties(context: Context, dao: PropertyDao) {
            val jsonString: String
            try {
                jsonString = context.assets.open("nethack_data/properties.json").bufferedReader()
                    .use { it.readText() }
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                return
            }

            val jsonArray = JSONArray(jsonString)
            val propertyList = mutableListOf<PropertyEntity>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                propertyList.add(
                    PropertyEntity(
                        id = jsonObject.getString("id"),
                        name = jsonObject.getString("name"),
                        type = jsonObject.getString("type"),
                        description = jsonObject.getString("description"),
                        summary = if (jsonObject.has("summary")) jsonObject.getString("summary") else ""
                    )
                )
            }
            dao.insertProperties(propertyList)
        }

        private suspend fun prePopulateArtifacts(context: Context, dao: ArtifactDao) {
            val jsonString: String
            try {
                jsonString = context.assets.open("nethack_data/nethack500/artifact500.json").bufferedReader()
                    .use { it.readText() }
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                return
            }

            val jsonArray = JSONArray(jsonString)
            val artifactList = mutableListOf<ArtifactEntity>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)

                val wieldedArray = jsonObject.optJSONArray("wieldedProperties")
                val wieldedList = mutableListOf<String>()
                if (wieldedArray != null) {
                    for (j in 0 until wieldedArray.length()) {
                        wieldedList.add(wieldedArray.getString(j))
                    }
                }

                val carriedArray = jsonObject.optJSONArray("carriedProperties")
                val carriedList = mutableListOf<String>()
                if (carriedArray != null) {
                    for (j in 0 until carriedArray.length()) {
                        carriedList.add(carriedArray.getString(j))
                    }
                }

                val specialEffectsArray = jsonObject.optJSONArray("specialEffects")
                val specialEffectsList = mutableListOf<String>()
                if (specialEffectsArray != null) {
                    for (j in 0 until specialEffectsArray.length()) {
                        specialEffectsList.add(specialEffectsArray.getString(j))
                    }
                }

                val artifactEntity = ArtifactEntity(
                    name = jsonObject.getString("name"),
                    baseItem = jsonObject.getString("baseItem"),
                    alignment = jsonObject.getString("alignment"),
                    role = if (jsonObject.isNull("role")) null else jsonObject.optString("role"),
                    wieldedProperties = wieldedList.joinToString("|"),
                    carriedProperties = carriedList.joinToString("|"),
                    attackToHitBonus = if (jsonObject.isNull("attackToHitBonus")) null else jsonObject.optString("attackToHitBonus"),
                    attackModifier = if (jsonObject.isNull("attackModifier")) null else jsonObject.optString("attackModifier"),
                    attackTypeModifier = if (jsonObject.isNull("attackTypeModifier")) null else jsonObject.optString("attackTypeModifier"),
                    damageTarget = if (jsonObject.isNull("damageTarget")) null else jsonObject.optString("damageTarget"),
                    invokedPower = if (jsonObject.isNull("invokedPower")) null else jsonObject.optString("invokedPower"),
                    specialEffects = specialEffectsList.joinToString("|")
                )
                artifactList.add(artifactEntity)
            }
            dao.insertAll(artifactList)
        }
    }
}
