package com.example.nethackseer.ui.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nethackseer.data.NetHackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * A simple data class to represent the "Page of the Day"
 * which can be either a monster or an item (for now).
 */
data class PageOfTheDay(
    val name: String,
    val type: String // "monster" or "item"
)

/**
 * View model for the home screen.
 *
 * @property repository The repository for the home screen.
 */
class HomeViewModel(repository: NetHackRepository) : ViewModel() {

    // This StateFlow will hold the current state of the UI in this case
    private val _pageOfTheDay = MutableStateFlow<PageOfTheDay?>(null)
    val pageOfTheDay: StateFlow<PageOfTheDay?> = _pageOfTheDay.asStateFlow()

    init {
        // Coroutine for the viewModelScope. Automatically canceled when ViewModel is cleared
        viewModelScope.launch {
            // fetch all names from the combined flow we created earlier
            val allNames = repository.allNames.first()
            if (allNames.isNotEmpty()) {
                val randomName = allNames.random()

                val isMonster = repository.getMonsterByName(randomName).first() != null
                val type = if (isMonster) "monster" else "item"
                
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