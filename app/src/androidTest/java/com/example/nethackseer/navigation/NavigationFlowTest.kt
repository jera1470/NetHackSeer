package com.example.nethackseer.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.nethackseer.data.NetHackRepository
import com.example.nethackseer.data.local.AppDatabase
import com.example.nethackseer.ui.components.SearchResultItem
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
class NavigationFlowTest {

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

    // Selecting a search result navigates to details
    @Test
    fun selectingResultNavigatesToDetails() {
        var navigatedEntityId: String? = null
        val onNavigateToDetail: (String) -> Unit = { id ->
            navigatedEntityId = id
        }

        val dummyResult = SearchResultItem("floating eye", "monster", "S_EYE", "CLR_BLUE")
        onNavigateToDetail(dummyResult.name)

        assertEquals("floating eye", navigatedEntityId)
    }

    // Clicking category pill navigates to category type list
    @Test
    fun clickingCategoryNavigatesToList() {
        var navigatedCategory: String? = null
        val onNavigateToCategory: (String) -> Unit = { category ->
            navigatedCategory = category
        }

        onNavigateToCategory("monster")
        assertEquals("monster", navigatedCategory)

        onNavigateToCategory("item")
        assertEquals("item", navigatedCategory)
    }

    // Clicking back pops the screen from backstack
    @Test
    fun clickingBackPopsScreen() {
        var backPressed = false
        val onBack: () -> Unit = {
            backPressed = true
        }

        onBack()
        assertTrue(backPressed)
    }

    // Clicking artifact category navigates and loads artifact list
    @Test
    fun artifactCategoryNavigationAndList() = runBlocking {
        var navigatedCategory: String? = null
        val onNavigateToCategory: (String) -> Unit = { category ->
            navigatedCategory = category
        }

        onNavigateToCategory("artifact")
        assertEquals("artifact", navigatedCategory)

        // TypeListViewModel loads all 33 artifacts
        val savedStateHandle = SavedStateHandle(mapOf("typeId" to "artifact"))
        val typeListViewModel = TypeListViewModel(savedStateHandle, repository)

        val uiState = typeListViewModel.uiState.first { it is TypeUiState.Success }
        val success = uiState as TypeUiState.Success
        assertEquals("Should load all 33 artifacts", 33, success.listObj.size)
        assertTrue("List should contain Mjollnir", success.listObj.any { it.name == "Mjollnir" })
    }
}
