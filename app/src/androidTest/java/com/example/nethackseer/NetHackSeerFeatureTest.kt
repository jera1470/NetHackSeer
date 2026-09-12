package com.example.nethackseer

import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.nethackseer.data.NetHackRepository
import com.example.nethackseer.data.local.AppDatabase
import com.example.nethackseer.ui.detail.DetailViewModel
import com.example.nethackseer.ui.detail.EntityUiState
import com.example.nethackseer.ui.homescreen.HomeViewModel
import com.example.nethackseer.ui.typelist.TypeListViewModel
import com.example.nethackseer.ui.typelist.TypeUiState
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
        }

        repository = NetHackRepository(
            database.monsterDao(),
            database.itemDao(),
            database.propertyDao(),
        )
    }

    // 1a. Check if the query sends correctly (from anywhere)
    @Test
    fun test1a_querySendsCorrectly() {
        val homeViewModel = HomeViewModel(repository)
        homeViewModel.onSearchQueryChange("plate mail")
        assertEquals("plate mail", homeViewModel.searchQuery.value)

        val typeListViewModel = TypeListViewModel(
            SavedStateHandle(mapOf("typeId" to "item")),
            repository
        )
        typeListViewModel.onSearchQueryChange("amulet")
        assertEquals("amulet", typeListViewModel.searchQuery.value)
    }

    // 1b. Check if the query receives items (from anywhere)
    @Test
    fun test1b_queryReceivesItems() = runBlocking {
        val homeViewModel = HomeViewModel(repository)
        homeViewModel.onSearchQueryChange("plate mail")

        val results = homeViewModel.searchResults.first { it.isNotEmpty() }
        assertTrue("SearchResults should contain items", results.isNotEmpty())
    }

    // 1c. Query "plate mail" should return three items ("plate mail", "crystal plate mail", and "bronze plate mail") in HomeScreen
    @Test
    fun test1c_queryPlateMail_returnsThreeItemsInHomeScreen() = runBlocking {
        val homeViewModel = HomeViewModel(repository)
        homeViewModel.onSearchQueryChange("plate mail")

        val results = homeViewModel.searchResults.first { it.size == 3 }
        val names = results.map { it.name }

        assertEquals(3, results.size)
        assertTrue("Should contain 'plate mail'", names.contains("plate mail"))
        assertTrue("Should contain 'crystal plate mail'", names.contains("crystal plate mail"))
        assertTrue("Should contain 'bronze plate mail'", names.contains("bronze plate mail"))
    }

    // 1d. Query "bruh" should return nothing for HomeScreen
    @Test
    fun test1d_queryBruh_returnsNothingInHomeScreen() = runBlocking {
        val homeViewModel = HomeViewModel(repository)
        homeViewModel.onSearchQueryChange("bruh")

        val results = repository.searchMonstersItems("bruh").first()
        assertTrue("Query 'bruh' should return empty list", results.isEmpty())
    }

    // 1e. Query "trice" should return three monsters ("chickatrice", "cockatrice", and "pyrolisk") (both in HomeScreen and Item typelist)
    @Test
    fun test1e_queryTrice_returnsThreeMonstersInHomeScreenAndItemTypelist() = runBlocking {
        val homeViewModel = HomeViewModel(repository)
        homeViewModel.onSearchQueryChange("trice")

        val homeResults = homeViewModel.searchResults.first { it.size == 3 }
        val homeNames = homeResults.map { it.name }

        assertEquals(3, homeResults.size)
        assertTrue("HomeScreen should contain 'chickatrice'", homeNames.contains("chickatrice"))
        assertTrue("HomeScreen should contain 'cockatrice'", homeNames.contains("cockatrice"))
        assertTrue("HomeScreen should contain 'pyrolisk'", homeNames.contains("pyrolisk"))

        val monsterTypeListViewModel = TypeListViewModel(
            SavedStateHandle(mapOf("typeId" to "monster")),
            repository
        )
        monsterTypeListViewModel.onSearchQueryChange("trice")

        val uiState = monsterTypeListViewModel.uiState.first { it is TypeUiState.Success && it.listObj.size == 3 } as TypeUiState.Success
        val monsterNames = uiState.listObj.map { it.name }

        assertEquals(3, uiState.listObj.size)
        assertTrue("Monster typelist should contain 'chickatrice'", monsterNames.contains("chickatrice"))
        assertTrue("Monster typelist should contain 'cockatrice'", monsterNames.contains("cockatrice"))
        assertTrue("Monster typelist should contain 'pyrolisk'", monsterNames.contains("pyrolisk"))
    }

    // 1f. Query "amulet" should return 13 items (HomeScreen and Item typelist)
    @Test
    fun test1f_queryAmulet_returnsThirteenItemsInHomeScreenAndItemTypelist() = runBlocking {
        val homeViewModel = HomeViewModel(repository)
        homeViewModel.onSearchQueryChange("amulet")

        val homeResults = homeViewModel.searchResults.first { it.size == 13 }
        assertEquals(13, homeResults.size)

        val itemTypeListViewModel = TypeListViewModel(
            SavedStateHandle(mapOf("typeId" to "item")),
            repository
        )
        itemTypeListViewModel.onSearchQueryChange("amulet")

        val uiState = itemTypeListViewModel.uiState.first { (it is TypeUiState.Success) && it.listObj.size == 13 } as TypeUiState.Success
        assertEquals(13, uiState.listObj.size)
    }

    // 2a. Checking if the detail screen is routed correctly
    @Test
    fun test2a_detailScreen_routedCorrectly() = runBlocking {
        val detailViewModel = DetailViewModel(
            SavedStateHandle(mapOf("entityId" to "giant ant")),
            repository
        )

        val uiState = detailViewModel.uiState.first { it is EntityUiState.MonsterSuccess }
        assertTrue("Detail screen should route successfully to MonsterSuccess", uiState is EntityUiState.MonsterSuccess)
    }

    // 2b. Check if "giant ant" exists as a monster detail
    @Test
    fun test2b_giantAnt_existsAsMonsterDetail() = runBlocking {
        val detailViewModel = DetailViewModel(
            SavedStateHandle(mapOf("entityId" to "giant ant")),
            repository
        )

        val uiState = detailViewModel.uiState.first { it is EntityUiState.MonsterSuccess } as EntityUiState.MonsterSuccess
        assertEquals("giant ant", uiState.monsterDetails.name)
    }

    // 2c. Check if "giant ant" displays correctly its AC (giant ant has an AC of 3)
    @Test
    fun test2c_giantAnt_displaysCorrectAC() = runBlocking {
        val detailViewModel = DetailViewModel(
            SavedStateHandle(mapOf("entityId" to "giant ant")),
            repository
        )

        val uiState = detailViewModel.uiState.first { it is EntityUiState.MonsterSuccess } as EntityUiState.MonsterSuccess
        assertEquals(3, uiState.monsterDetails.ac)
    }

    // 2d. Check if "giant ant" has present properties below its stats (bullet points describing its behavior and appearance)
    @Test
    fun test2d_giantAnt_hasPropertiesBelowStats() = runBlocking {
        val detailViewModel = DetailViewModel(
            SavedStateHandle(mapOf("entityId" to "giant ant")),
            repository
        )

        val uiState = detailViewModel.uiState.first { it is EntityUiState.MonsterSuccess } as EntityUiState.MonsterSuccess
        assertTrue("giant ant should have property bullet points below stats", uiState.monsterDetails.propertyBulletPoints.isNotEmpty())
    }

    // 2e. Check if "Chromatic Dragon" has all eight resistances (Fire, Cold, Sleep, Disintegrate, Shock, Poison, Acid, and Stone)
    @Test
    fun test2e_chromaticDragon_hasAllEightResistances() = runBlocking {
        val detailViewModel = DetailViewModel(
            SavedStateHandle(mapOf("entityId" to "Chromatic Dragon")),
            repository
        )

        val uiState = detailViewModel.uiState.first { it is EntityUiState.MonsterSuccess } as EntityUiState.MonsterSuccess
        val resistances = uiState.monsterDetails.resistances

        val expectedResistances = listOf("Fire", "Cold", "Sleep", "Disintegrate", "Shock", "Poison", "Acid", "Stone")
        for (expected in expectedResistances) {
            assertTrue("Chromatic Dragon should have $expected resistance", resistances.contains(expected))
        }
    }

    // 2f. Check if "master mind flayer" has six attacks (one weapon attack and five tentacle attacks)
    @Test
    fun test2f_masterMindFlayer_hasSixAttacks_oneWeaponAndFiveTentacle() = runBlocking {
        val detailViewModel = DetailViewModel(
            SavedStateHandle(mapOf("entityId" to "master mind flayer")),
            repository
        )

        val uiState = detailViewModel.uiState.first { it is EntityUiState.MonsterSuccess } as EntityUiState.MonsterSuccess
        val attacks = uiState.monsterDetails.attacks

        assertEquals("master mind flayer should have 6 attacks", 6, attacks.size)
        val weaponAttacks = attacks.filter { it.contains("weapon", ignoreCase = true) }
        val tentacleAttacks = attacks.filter { it.contains("tentacle", ignoreCase = true) }

        assertEquals("master mind flayer should have 1 weapon attack", 1, weaponAttacks.size)
        assertEquals("master mind flayer should have 5 tentacle attacks", 5, tentacleAttacks.size)
    }

    // 2g. Check if "newt" has increase energy
    @Test
    fun test2g_newt_hasIncreaseEnergy() = runBlocking {
        val detailViewModel = DetailViewModel(
            SavedStateHandle(mapOf("entityId" to "newt")),
            repository
        )

        val uiState = detailViewModel.uiState.first { it is EntityUiState.MonsterSuccess } as EntityUiState.MonsterSuccess
        val specialEffects = uiState.monsterDetails.specialEffects

        val hasIncreaseEnergy = specialEffects.any { it.contains("Increase energy", ignoreCase = true) }
        assertTrue("newt should have 'Increase energy' in specialEffects", hasIncreaseEnergy)
    }
}
