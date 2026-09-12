package com.example.nethackseer

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.nethackseer.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.milliseconds

// runs on Android device
@RunWith(AndroidJUnit4::class)
class DatabasePopulationTest {

    @Test
    fun testAppDatabase_populatesMonstersItemsAndProperties() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val scope = CoroutineScope(Dispatchers.IO)
        val database = AppDatabase.getDatabase(context, scope)

        // wait for population to complete (up to 10 seconds)
        withTimeout(10_000L.milliseconds) {
            val monsters = database.monsterDao().getAll().first { it.isNotEmpty() }
            val items = database.itemDao().getAll().first { it.isNotEmpty() }

            assertTrue("Monsters table should not be empty", monsters.isNotEmpty())
            assertTrue("Items table should not be empty", items.isNotEmpty())

            val sampleMonster = monsters.find { it.name.equals("Croesus", ignoreCase = true) }
            assertNotNull("Croesus monster should exist in database", sampleMonster)

            val gnomeRuler = monsters.find { it.name.equals("gnome ruler", ignoreCase = true) }
            assertNotNull("gnome ruler monster should exist in database", gnomeRuler)
            assertTrue("gnome ruler should have maleName 'gnome king'", gnomeRuler?.maleName == "gnome king")
            assertTrue("gnome ruler should have femaleName 'gnome queen'", gnomeRuler?.femaleName == "gnome queen")

            val sampleItem = items.find { it.name.contains("Amulet of Yendor", ignoreCase = true) }
            assertNotNull("Amulet of Yendor item should exist in database", sampleItem)
        }
    }
}
