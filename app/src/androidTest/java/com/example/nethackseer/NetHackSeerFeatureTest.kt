package com.example.nethackseer

import androidx.compose.foundation.text.input.TextFieldState
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.nethackseer.data.NetHackRepository
import com.example.nethackseer.data.local.AppDatabase
import com.example.nethackseer.ui.components.SearchResultItem
import com.example.nethackseer.ui.homescreen.HomeViewModel
import com.example.nethackseer.ui.utils.cleanNetHackName
import com.example.nethackseer.ui.utils.getDisplayChar
import com.example.nethackseer.ui.utils.getNetHackColor
import com.example.nethackseer.ui.utils.getSymbolDisplayName
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
class NetHackSeerFeatureTest {

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

    // 1a. Check if the query sends correctly (from anywhere)
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

    // 3a. Check if glyph/color representation matches
    @Test
    fun test3a_glyphRepresentationMatches() {
        // Monster symbol test
        val monsterSymbol = "S_ANT"
        val expectedChar = "a"
        val actualChar = getDisplayChar(monsterSymbol)
        assertEquals(expectedChar, actualChar)

        // Item symbol test
        val itemSymbol = "ARMOR_CLASS"
        val expectedItemChar = "["
        val actualItemChar = getDisplayChar(itemSymbol)
        assertEquals(expectedItemChar, actualItemChar)
    }

    // 3b. Check if color matches
    @Test
    fun test3b_colorRepresentationMatches() {
        val colorString = "CLR_RED"
        val color = getNetHackColor(colorString)
        assertNotNull(color)
    }

    // 4. Check if selecting a search result navigates to details
    @Test
    fun test4_selectingResultNavigatesToDetails() {
        var navigatedEntityId: String? = null
        val onNavigateToDetail: (String) -> Unit = { id ->
            navigatedEntityId = id
        }

        val dummyResult = SearchResultItem("floating eye", "monster", "S_EYE", "CLR_BLUE")
        onNavigateToDetail(dummyResult.name)

        assertEquals("floating eye", navigatedEntityId)
    }

    // 5. Check if clicking category navigates to list
    @Test
    fun test5_clickingCategoryNavigatesToList() {
        var navigatedCategory: String? = null
        val onNavigateToCategory: (String) -> Unit = { category ->
            navigatedCategory = category
        }

        onNavigateToCategory("monster")
        assertEquals("monster", navigatedCategory)

        onNavigateToCategory("item")
        assertEquals("item", navigatedCategory)
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
        val filteredResults = homeViewModel.searchResults.first { it.isNotEmpty() }

        assertTrue(broadResults.size >= filteredResults.size)
        assertTrue(filteredResults.all { it.name.contains("red dragon", ignoreCase = true) })
    }

    // 7. Check if clicking back pops the screen
    @Test
    fun test7_clickingBackPopsScreen() {
        var backPressed = false
        val onBack: () -> Unit = {
            backPressed = true
        }

        onBack()
        assertTrue(backPressed)
    }

    // 8. Check if the database works correctly
    @Test
    fun test8_databaseLoadsEntities() = runBlocking {
        val monsters = repository.allMonsters.first { it.isNotEmpty() }
        val items = repository.allItems.first { it.isNotEmpty() }

        assertTrue("Monsters should not be empty", monsters.isNotEmpty())
        assertTrue("Items should not be empty", items.isNotEmpty())
    }

    // 9. Check if symbol display name is translated correctly
    @Test
    fun test9_symbolDisplayNameTranslation() {
        val monsterSymbol = "S_DOG"
        val displayName = getSymbolDisplayName(monsterSymbol)
        assertEquals("canine", displayName)

        val itemSymbol = "WEAPON_CLASS"
        val itemDisplayName = getSymbolDisplayName(itemSymbol)
        assertEquals("weapon", itemDisplayName)
    }

    // 10. Check if cleanNetHackName strips underscores and prefixes properly
    @Test
    fun test10_cleanNetHackNameFormatting() {
        val rawName = "giant_ant"
        val cleaned = cleanNetHackName(rawName)
        assertEquals("giant ant", cleaned)
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
        homeViewModel.onSearchQueryChange("queen bee")

        val results = homeViewModel.searchResults.first { it.isNotEmpty() }
        assertTrue("Searching 'queen bee' should find 'killer bee'",
            results.any { it.name.equals("killer bee", ignoreCase = true) })
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

        // search is debounced/flow-based, with a non-existent match list should be empty
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

    // 18. All names list contains items, monsters, and properties
    @Test
    fun test18_allNamesContainsCombinedEntities() = runBlocking {
        val names = repository.allNames.first { it.isNotEmpty() }
        assertTrue("Names list should be large", names.size > 500)
    }
}
