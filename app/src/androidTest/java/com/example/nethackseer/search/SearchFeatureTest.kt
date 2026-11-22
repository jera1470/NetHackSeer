package com.example.nethackseer.search

import androidx.compose.foundation.text.input.TextFieldState
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.nethackseer.data.NetHackRepository
import com.example.nethackseer.data.local.AppDatabase
import com.example.nethackseer.ui.homescreen.HomeViewModel
import com.example.nethackseer.ui.utils.getDisplayChar
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

/**
 * Instrumented tests for Search bar operations, query matching,
 * debouncing, and category results across Monsters, Items, and Artifacts.
 */
@RunWith(AndroidJUnit4::class)
class SearchFeatureTest {

    private lateinit var repository: NetHackRepository

    @Before
    fun setUp() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val scope = CoroutineScope(Dispatchers.IO)
        val database = AppDatabase.getDatabase(context, scope)

        // Wait for database population from assets
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

    // 1a. Check if the query sends correctly
    @Test
    fun test1a_querySendsCorrectly() {
        val homeViewModel = HomeViewModel(repository)
        homeViewModel.onSearchQueryChange("plate mail")
        assertEquals("plate mail", homeViewModel.searchQuery.value)
    }

    // 1b. Check if the text changes based on text field state
    @Test
    fun test1b_textFieldStateSyncsCorrectly() {
        val textFieldState = TextFieldState()
        textFieldState.edit {
            replace(0, length, "long sword")
        }
        assertEquals("long sword", textFieldState.text.toString())
    }

    // 2. Check if a query finds relevant results
    @Test
    fun test2_queryFindsRelevantResults() = runBlocking {
        val homeViewModel = HomeViewModel(repository)
        homeViewModel.onSearchQueryChange("giant")

        val results = homeViewModel.searchResults.first { it.isNotEmpty() }
        assertTrue("Should return items containing 'giant'", results.any { it.name.contains("giant", ignoreCase = true) })
    }

    // 6. Check if filtering updates the UI
    @Test
    fun test6_filteringUpdatesResults() = runBlocking {
        val homeViewModel = HomeViewModel(repository)

        // Initial broad search
        homeViewModel.onSearchQueryChange("dragon")
        val broadResults = homeViewModel.searchResults.first { it.isNotEmpty() }

        // Specific filter
        homeViewModel.onSearchQueryChange("red dragon")
        val filteredResults = homeViewModel.searchResults.first { results ->
            results.isNotEmpty() && results.all { it.name.contains("red dragon", ignoreCase = true) }
        }

        assertTrue(broadResults.size >= filteredResults.size)
        assertTrue(filteredResults.all { it.name.contains("red dragon", ignoreCase = true) })
    }

    // 11. Search by glyph returns appropriate results
    @Test
    fun test11_searchByGlyph() = runBlocking {
        val homeViewModel = HomeViewModel(repository)
        homeViewModel.onSearchQueryChange("d") // 'd' is S_DOG

        val results = homeViewModel.searchResults.first { it.isNotEmpty() }
        assertTrue("Should return dog-class monsters when searching 'd'",
            results.any { it.symbol == "S_DOG" })
    }

    // 12. Search by gendered name returns the root monster
    @Test
    fun test12_searchByGenderedName() = runBlocking {
        val homeViewModel = HomeViewModel(repository)
        homeViewModel.onSearchQueryChange("succubus")

        val results = homeViewModel.searchResults.first { it.isNotEmpty() }
        assertTrue("Searching 'succubus' should find 'amorous demon'",
            results.any { it.name.equals("amorous demon", ignoreCase = true) })
    }

    // 13. Search by item glyph returns items of that class
    @Test
    fun test13_searchByItemGlyph() = runBlocking {
        val homeViewModel = HomeViewModel(repository)
        homeViewModel.onSearchQueryChange(")") // ')' is WEAPON_CLASS

        val results = homeViewModel.searchResults.first { it.isNotEmpty() }
        assertTrue("Should return weapons when searching ')'",
            results.any { it.category == "item" && it.symbol == "WEAPON_CLASS" })
    }

    // 14. Search by monster class name returns that monster group
    @Test
    fun test14_searchByMonsterClassName() = runBlocking {
        val homeViewModel = HomeViewModel(repository)
        homeViewModel.onSearchQueryChange("ant")

        val results = homeViewModel.searchResults.first { it.isNotEmpty() }
        assertTrue("Searching 'ant' should return ant group monsters",
            results.any { it.symbol == "S_ANT" })
    }

    // 15. Empty query emits empty results
    @Test
    fun test15_emptyQueryEmitsEmptyList() = runBlocking {
        val homeViewModel = HomeViewModel(repository)
        homeViewModel.onSearchQueryChange("")

        val results = homeViewModel.searchResults.value
        assertTrue("Empty query should yield empty results", results.isEmpty())
    }

    // 16. Searching non-existent query yields empty results
    @Test
    fun test16_nonExistentQueryYieldsEmptyList() = runBlocking {
        val homeViewModel = HomeViewModel(repository)
        homeViewModel.onSearchQueryChange("xyz123nonexistent")

        val results = homeViewModel.searchResults.first { it.isEmpty() }
        assertTrue("No entity should match random gibberish", results.isEmpty())
    }

    // 17. Case insensitivity in search
    @Test
    fun test17_searchIsCaseInsensitive() = runBlocking {
        val homeViewModelLower = HomeViewModel(repository)
        homeViewModelLower.onSearchQueryChange("orc")
        val lowerResults = homeViewModelLower.searchResults.first { it.isNotEmpty() }

        val homeViewModelUpper = HomeViewModel(repository)
        homeViewModelUpper.onSearchQueryChange("ORC")
        val upperResults = homeViewModelUpper.searchResults.first { it.isNotEmpty() }

        assertEquals("Upper and lower search should produce same size results",
            lowerResults.size, upperResults.size)
    }

    // 19. Search finds artifacts by name and base item
    @Test
    fun test19_artifactSearch() = runBlocking {
        // Search by artifact name
        val excaliburResults = repository.searchMonstersItems("Excalibur").first { it.isNotEmpty() }
        val excalibur = excaliburResults.find { it.name == "Excalibur" }
        assertNotNull("Should find Excalibur artifact", excalibur)
        assertEquals("artifact", excalibur?.category)
        assertEquals(")", getDisplayChar(excalibur?.symbol ?: ""))

        // Search by base item finds associated artifacts
        val orbResults = repository.searchMonstersItems("crystal ball").first { it.isNotEmpty() }
        val fateOrb = orbResults.find { it.name == "The Orb of Fate" }
        assertNotNull("Searching base item 'crystal ball' should return The Orb of Fate", fateOrb)
        assertEquals("artifact", fateOrb?.category)
    }
}
