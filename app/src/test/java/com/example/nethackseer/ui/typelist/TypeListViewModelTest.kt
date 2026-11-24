package com.example.nethackseer.ui.typelist

import androidx.lifecycle.SavedStateHandle
import com.example.nethackseer.data.FakeArtifactDao
import com.example.nethackseer.data.FakeItemDao
import com.example.nethackseer.data.FakeMonsterDao
import com.example.nethackseer.data.FakePropertyDao
import com.example.nethackseer.data.NetHackRepository
import com.example.nethackseer.data.TestEntityFactory
import com.example.nethackseer.testutils.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TypeListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var monsterDao: FakeMonsterDao
    private lateinit var itemDao: FakeItemDao
    private lateinit var propertyDao: FakePropertyDao
    private lateinit var artifactDao: FakeArtifactDao
    private lateinit var repository: NetHackRepository

    @Before
    fun setUp() {
        val monsters = listOf(
            TestEntityFactory.createMonster("giant ant", symbol = "S_ANT", color = "CLR_BROWN"),
            TestEntityFactory.createMonster("red dragon", symbol = "S_DRAGON", color = "CLR_RED"),
            TestEntityFactory.createMonster("gnome ruler", symbol = "S_GNOME", color = "CLR_MAGENTA")
        )

        val items = listOf(
            TestEntityFactory.createItem("long sword", symbol = "WEAPON_CLASS", color = "HI_METAL"),
            TestEntityFactory.createItem("crystal ball", symbol = "TOOL_CLASS", color = "CLR_WHITE"),
            TestEntityFactory.createItem("leather armor", symbol = "ARMOR_CLASS", color = "CLR_BROWN")
        )

        val artifacts = listOf(
            TestEntityFactory.createArtifact("Excalibur", baseItem = "long sword"),
            TestEntityFactory.createArtifact("The Orb of Fate", baseItem = "crystal ball")
        )

        monsterDao = FakeMonsterDao(monsters)
        itemDao = FakeItemDao(items)
        propertyDao = FakePropertyDao(emptyList())
        artifactDao = FakeArtifactDao(artifacts)

        repository = NetHackRepository(monsterDao, itemDao, propertyDao, artifactDao)
    }

    // Emits Success state containing mapped monster entities
    @Test
    fun loadType_monster_emitsMonsterList() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("typeId" to "monster"))
        val viewModel = TypeListViewModel(savedStateHandle, repository)

        val state = viewModel.uiState.first { it !is TypeUiState.Loading }
        assertTrue("Expected Success state but got $state", state is TypeUiState.Success)

        val success = state as TypeUiState.Success
        assertEquals("monster", success.type)
        assertEquals(3, success.listObj.size)
        assertTrue(success.listObj.any { it.name == "giant ant" && it.symbol == "S_ANT" })
        assertTrue(success.listObj.any { it.name == "red dragon" && it.symbol == "S_DRAGON" })
        assertTrue(success.listObj.any { it.name == "gnome ruler" && it.symbol == "S_GNOME" })
    }

    // Emits Success state containing mapped item entities
    @Test
    fun loadType_item_emitsItemList() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("typeId" to "item"))
        val viewModel = TypeListViewModel(savedStateHandle, repository)

        val state = viewModel.uiState.first { it !is TypeUiState.Loading }
        assertTrue("Expected Success state but got $state", state is TypeUiState.Success)

        val success = state as TypeUiState.Success
        assertEquals("item", success.type)
        assertEquals(3, success.listObj.size)
        assertTrue(success.listObj.any { it.name == "long sword" && it.symbol == "WEAPON_CLASS" })
        assertTrue(success.listObj.any { it.name == "crystal ball" && it.symbol == "TOOL_CLASS" })
        assertTrue(success.listObj.any { it.name == "leather armor" && it.symbol == "ARMOR_CLASS" })
    }

    // Emits Success state containing artifacts with symbols and colors resolved from base items
    @Test
    fun loadType_artifact_emitsArtifactListWithInheritedSymbolAndColor() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("typeId" to "artifact"))
        val viewModel = TypeListViewModel(savedStateHandle, repository)

        val state = viewModel.uiState.first { it !is TypeUiState.Loading }
        assertTrue("Expected Success state but got $state", state is TypeUiState.Success)

        val success = state as TypeUiState.Success
        assertEquals("artifact", success.type)
        assertEquals(2, success.listObj.size)

        val excalibur = success.listObj.first { it.name == "Excalibur" }
        assertEquals("WEAPON_CLASS", excalibur.symbol)
        assertEquals("HI_METAL", excalibur.color)

        val orb = success.listObj.first { it.name == "The Orb of Fate" }
        assertEquals("TOOL_CLASS", orb.symbol)
        assertEquals("CLR_WHITE", orb.color)
    }

    // Emits Error state when provided an invalid or unknown category type ID
    @Test
    fun loadType_invalidTypeId_emitsError() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("typeId" to "invalid_category"))
        val viewModel = TypeListViewModel(savedStateHandle, repository)

        val state = viewModel.uiState.first { it !is TypeUiState.Loading }
        assertTrue("Expected Error state but got $state", state is TypeUiState.Error)
        assertEquals("Invalid type ID: invalid_category", (state as TypeUiState.Error).message)
    }

    // Filters entities by matching text against entity name
    @Test
    fun onSearchQueryChange_filtersByName() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("typeId" to "monster"))
        val viewModel = TypeListViewModel(savedStateHandle, repository)

        viewModel.onSearchQueryChange("dragon")
        val state = viewModel.uiState.first { it is TypeUiState.Success && it.listObj.size == 1 }
        val success = state as TypeUiState.Success
        assertEquals("red dragon", success.listObj.first().name)
    }

    // Filters entities by matching query against symbol display glyph
    @Test
    fun onSearchQueryChange_filtersByGlyph() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("typeId" to "monster"))
        val viewModel = TypeListViewModel(savedStateHandle, repository)

        viewModel.onSearchQueryChange("a") // S_ANT maps to 'a'
        val state = viewModel.uiState.first { it is TypeUiState.Success && it.listObj.any { e -> e.symbol == "S_ANT" } }
        val success = state as TypeUiState.Success
        assertEquals("giant ant", success.listObj.first().name)
    }

    // Restores complete entity list when search query is cleared
    @Test
    fun onSearchQueryChange_emptyQueryRestoresAllEntities() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("typeId" to "item"))
        val viewModel = TypeListViewModel(savedStateHandle, repository)

        viewModel.onSearchQueryChange("sword")
        val filtered = viewModel.uiState.first { it is TypeUiState.Success && it.listObj.size == 1 }
        assertEquals(1, (filtered as TypeUiState.Success).listObj.size)

        viewModel.onSearchQueryChange("")
        val restored = viewModel.uiState.first { it is TypeUiState.Success && it.listObj.size == 3 }
        assertEquals(3, (restored as TypeUiState.Success).listObj.size)
    }
}
