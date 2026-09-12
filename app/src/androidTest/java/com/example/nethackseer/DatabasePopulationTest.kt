package com.example.nethackseer

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.nethackseer.data.local.AppDatabase
import com.example.nethackseer.data.local.entity.ItemEntity
import com.example.nethackseer.data.local.entity.MonsterEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.seconds

// runs on Android device
@RunWith(AndroidJUnit4::class)
class DatabasePopulationTest {

    private lateinit var database: AppDatabase
    private lateinit var monsters: List<MonsterEntity>
    private lateinit var items: List<ItemEntity>

    @Before
    fun setUp() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val scope = CoroutineScope(Dispatchers.IO)
        database = AppDatabase.getDatabase(context, scope)

        // Wait for population to complete from assets
        withTimeout(10.seconds) {
            monsters = database.monsterDao().getAll().first { it.isNotEmpty() }
            items = database.itemDao().getAll().first { it.isNotEmpty() }
        }
    }

    @Test
    fun testDatabasePopulation_tablesNotEmpty() {
        assertTrue("Monsters table should not be empty", monsters.isNotEmpty())
        assertTrue("Items table should not be empty", items.isNotEmpty())
    }

    @Test
    fun testDatabasePopulation_containsSampleMonsterCroesus() {
        val croesus = monsters.find { it.name.equals("Croesus", ignoreCase = true) }
        assertNotNull("Croesus monster should exist in database", croesus)
    }

    @Test
    fun testDatabasePopulation_containsGenderedGnomeRuler() {
        val gnomeRuler = monsters.find { it.name.equals("gnome ruler", ignoreCase = true) }
        assertNotNull("gnome ruler monster should exist in database", gnomeRuler)
        assertEquals("gnome king", gnomeRuler?.maleName)
        assertEquals("gnome queen", gnomeRuler?.femaleName)
    }

    @Test
    fun testDatabasePopulation_containsSampleItemAmuletOfYendor() {
        val amulet = items.find { it.name.contains("Amulet of Yendor", ignoreCase = true) }
        assertNotNull("Amulet of Yendor item should exist in database", amulet)
    }
}
