package com.example.nethackseer.detail

import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.nethackseer.data.NetHackRepository
import com.example.nethackseer.data.local.AppDatabase
import com.example.nethackseer.ui.detail.DetailViewModel
import com.example.nethackseer.ui.detail.EntityUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
class DetailScreenFeatureTest {

    private lateinit var repository: NetHackRepository

    @Before
    fun setUp() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val scope = CoroutineScope(Dispatchers.IO)
        val database = AppDatabase.getDatabase(context, scope)

        withTimeout(10.seconds) {
            database.monsterDao().getAll().first { it.isNotEmpty() }
            database.itemDao().getAll().first { it.isNotEmpty() }
            database.artifactDao().getAll().first { it.isNotEmpty() }
        }

        repository = NetHackRepository(
            database.monsterDao(),
            database.itemDao(),
            database.propertyDao(),
            database.artifactDao()
        )
    }

    // Artifact detail loads and displays full artifact stats and special effects
    @Test
    fun artifactDetailDisplay() = runBlocking {
        val savedStateHandle = SavedStateHandle(mapOf("entityId" to "The Eye of the Aethiopica"))
        val detailViewModel = DetailViewModel(savedStateHandle, repository)

        val uiState = detailViewModel.uiState.first { it is EntityUiState.ArtifactSuccess }
        val success = uiState as EntityUiState.ArtifactSuccess
        val details = success.artifactDetails

        assertEquals("The Eye of the Aethiopica", details.name)
        assertEquals("amulet of esp", details.baseItem)
        assertEquals("Neutral", details.alignment)
        assertEquals("Wizard", details.role)
        assertEquals("Create portal", details.invokedPower)
        assertEquals(3, details.specialEffects.size)
        assertTrue("Wielded properties should contain antimagic",
            details.wieldedProperties.any { it.contains("Antimagic", ignoreCase = true) })
        assertTrue("Carried properties should contain energy regen",
            details.carriedProperties.any { it.contains("Energy regen", ignoreCase = true) })
    }
}
