package com.example.nethackseer.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.nethackseer.data.local.AppDatabase
import com.example.nethackseer.data.local.dao.ArtifactDao
import com.example.nethackseer.data.local.dao.ItemDao
import com.example.nethackseer.data.local.dao.MonsterDao
import com.example.nethackseer.data.local.dao.PropertyDao
import com.example.nethackseer.data.local.entity.ArtifactEntity
import com.example.nethackseer.data.local.entity.Attack
import com.example.nethackseer.data.local.entity.ItemEntity
import com.example.nethackseer.data.local.entity.MonsterEntity
import com.example.nethackseer.data.local.entity.PropertyEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var monsterDao: MonsterDao
    private lateinit var itemDao: ItemDao
    private lateinit var propertyDao: PropertyDao
    private lateinit var artifactDao: ArtifactDao

    private fun createAttack() = Attack(
        type = "AT_BITE",
        damageType = "AD_PHYS",
        diceCount = 1,
        diceSides = 4
    )

    private fun createMonster(
        name: String,
        symbol: String = "S_ANT",
        color: String = "CLR_BROWN",
        maleName: String? = null,
        femaleName: String? = null
    ) = MonsterEntity(
        name = name,
        symbol = symbol,
        level = 2,
        moveRate = 12,
        ac = 3,
        mr = 0,
        alignment = 0,
        genoFlags = "0",
        attack1 = createAttack(),
        attack2 = Attack("NO_ATTK", "AD_NONE", 0, 0),
        attack3 = Attack("NO_ATTK", "AD_NONE", 0, 0),
        attack4 = Attack("NO_ATTK", "AD_NONE", 0, 0),
        attack5 = Attack("NO_ATTK", "AD_NONE", 0, 0),
        attack6 = Attack("NO_ATTK", "AD_NONE", 0, 0),
        weight = 10,
        nutritionValue = 10,
        sound = "MS_SILENT",
        size = "MZ_MEDIUM",
        resistances = "0",
        resistancesConferred = "0",
        m1Flags = "M1_ANIMAL",
        m2Flags = "M2_HOSTILE",
        m3Flags = "0",
        difficulty = 4,
        color = color,
        maleName = maleName,
        femaleName = femaleName
    )

    private fun createItem(
        name: String,
        symbol: String = "WEAPON_CLASS",
        color: String = "HI_METAL"
    ) = ItemEntity(
        name = name,
        description = "A test item.",
        merge = false,
        magicItem = false,
        charge = false,
        unique = false,
        notWish = false,
        tough = false,
        damageType = "AD_PHYS",
        zapDirection = "NODIR",
        subCategory = "P_SWORD",
        material = "iron",
        property = "0",
        symbol = symbol,
        probability = 100,
        delay = 0,
        weight = 40,
        value = 15,
        smallDamage = 8,
        largeDamage = 12,
        ac = 0,
        hitBonus = 0,
        magicCancellation = 0,
        spellLevel = 0,
        nutrition = 0,
        color = color
    )

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        monsterDao = db.monsterDao()
        itemDao = db.itemDao()
        propertyDao = db.propertyDao()
        artifactDao = db.artifactDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    // Verifies MonsterDao entity insertion, exact name query, gendered wildcard search, and alphabetical name listing
    @Test
    fun monsterDao_insertSearchAndGetByName() = runBlocking {
        val monsters = listOf(
            createMonster("giant ant", symbol = "S_ANT"),
            createMonster("gnome ruler", symbol = "S_GNOME", maleName = "gnome king", femaleName = "gnome queen")
        )
        monsterDao.insertAll(monsters)

        val retrieved = monsterDao.getMonsterByName("giant ant").first()
        assertNotNull(retrieved)
        assertEquals("giant ant", retrieved?.name)

        val nonExistent = monsterDao.getMonsterByName("unicorn").first()
        assertNull(nonExistent)

        val searchDirect = monsterDao.search("%ant%").first()
        assertEquals(1, searchDirect.size)
        assertEquals("giant ant", searchDirect.first().name)

        // Test search matching femaleName
        val searchFemale = monsterDao.search("%queen%").first()
        assertEquals(1, searchFemale.size)
        assertEquals("gnome ruler", searchFemale.first().name)

        val allNames = monsterDao.getAllNames().first()
        assertEquals(listOf("giant ant", "gnome ruler"), allNames)
    }

    // Verifies ItemDao entity insertion, exact lookup, wildcard query search, and sorted name retrieval
    @Test
    fun itemDao_insertSearchAndGetByName() = runBlocking {
        val items = listOf(
            createItem("long sword", symbol = "WEAPON_CLASS"),
            createItem("crystal ball", symbol = "TOOL_CLASS")
        )
        itemDao.insertAll(items)

        val retrieved = itemDao.getItemByName("long sword").first()
        assertNotNull(retrieved)
        assertEquals("long sword", retrieved?.name)

        val searchResult = itemDao.search("%sword%").first()
        assertEquals(1, searchResult.size)
        assertEquals("long sword", searchResult.first().name)

        val allNames = itemDao.getAllNames().first()
        assertEquals(listOf("crystal ball", "long sword"), allNames)
    }

    // Verifies ArtifactDao insertion, name retrieval, base item association, and search functionality
    @Test
    fun artifactDao_insertSearchAndGetByName() = runBlocking {
        val artifacts = listOf(
            ArtifactEntity(
                name = "Excalibur",
                baseItem = "long sword",
                alignment = "Lawful",
                role = "Knight",
                wieldedProperties = "MR2_SEARCHING|MR_DRAIN",
                carriedProperties = "",
                attackToHitBonus = "1d5",
                attackModifier = "1d10",
                attackTypeModifier = "AD_PHYS",
                damageTarget = null,
                invokedPower = null,
                specialEffects = "Manual search bonus"
            ),
            ArtifactEntity(
                name = "The Orb of Fate",
                baseItem = "crystal ball",
                alignment = "Neutral",
                role = "Valkyrie",
                wieldedProperties = "",
                carriedProperties = "MR2_WARNING",
                attackToHitBonus = null,
                attackModifier = null,
                attackTypeModifier = null,
                damageTarget = null,
                invokedPower = "LEV_TELE",
                specialEffects = "Luckstone effect"
            )
        )
        artifactDao.insertAll(artifacts)

        val excalibur = artifactDao.getArtifactByName("Excalibur").first()
        assertNotNull(excalibur)
        assertEquals("Excalibur", excalibur?.name)
        assertEquals("long sword", excalibur?.baseItem)

        val searchOrb = artifactDao.search("%Orb%").first()
        assertEquals(1, searchOrb.size)
        assertEquals("The Orb of Fate", searchOrb.first().name)

        val allNames = artifactDao.getAllNames().first()
        assertEquals(listOf("Excalibur", "The Orb of Fate"), allNames)
    }

    // Verifies PropertyDao insertion, lookup by ID, SQL IN clause batch queries, and name retrieval
    @Test
    fun propertyDao_insertGetByIdsAndName() = runBlocking {
        val properties = listOf(
            PropertyEntity("FIRE_RES", "Fire resistance", "Immunity to fire.", "Fire immune", "intrinsic"),
            PropertyEntity("COLD_RES", "Cold resistance", "Immunity to cold.", "Cold immune", "intrinsic"),
            PropertyEntity("SLEEP_RES", "Sleep resistance", "Immunity to sleep.", "Sleep immune", "intrinsic")
        )
        propertyDao.insertProperties(properties)

        val fire = propertyDao.getPropertyById("FIRE_RES")
        assertNotNull(fire)
        assertEquals("Fire resistance", fire?.name)

        val fireByName = propertyDao.getPropertyByName("Fire resistance").first()
        assertNotNull(fireByName)
        assertEquals("FIRE_RES", fireByName?.id)

        val selected = propertyDao.getPropertiesByIds(listOf("FIRE_RES", "SLEEP_RES")).first()
        assertEquals(2, selected.size)
        assertTrue(selected.any { it.id == "FIRE_RES" })
        assertTrue(selected.any { it.id == "SLEEP_RES" })

        val allNames = propertyDao.getAllNames().first()
        assertEquals(3, allNames.size)
    }
}
