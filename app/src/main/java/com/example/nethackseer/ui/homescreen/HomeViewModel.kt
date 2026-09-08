package com.example.nethackseer.ui.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nethackseer.data.NetHackRepository
import com.example.nethackseer.ui.components.SearchResultItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PageOfTheDay(
    val name: String,
    val type: String // "monster", "item", or "property"
)

/**
 * View model for the home screen.
 *
 * @property repository The repository for the home screen.
 */
class HomeViewModel(private val repository: NetHackRepository) : ViewModel() {
    // will make this better in the future
    private val _pageOfTheDay = MutableStateFlow<PageOfTheDay?>(null)
    val pageOfTheDay: StateFlow<PageOfTheDay?> = _pageOfTheDay.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<SearchResultItem>> = _searchQuery
        // used to switch from one flow to another without having race conditions
        .flatMapLatest { query -> repository.searchMonstersItems(query) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    init {
        // Coroutine for the viewModelScope. Automatically canceled when ViewModel is cleared
        viewModelScope.launch {
            val allNames = repository.allNames.first()
            if (allNames.isNotEmpty()) {
                val randomName = allNames.random()

                val isMonster = repository.getMonsterByName(randomName).first() != null
                val isItem = if (!isMonster) repository.getItemByName(randomName).first() != null else false
                val type = when {
                    isMonster -> "monster"
                    isItem -> "item"
                    else -> "property"
                }
                
                _pageOfTheDay.value = PageOfTheDay(randomName, type)
            }
        }
    }
}

/**
 * Factory for creating HomeViewModel with a constructor that takes a NetHackRepository.
 */
class HomeViewModelFactory(private val repository: NetHackRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            // Suppressing this since we know this must be a HomeViewModel from isAssignableFrom()
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}