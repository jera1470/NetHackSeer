package com.example.nethackseer.ui.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nethackseer.data.NetHackRepository
import com.example.nethackseer.data.local.entity.NetHackEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * View model for the home screen.
 *
 * @property repository The repository for the home screen.
 */
class HomeViewModel (repository: NetHackRepository) : ViewModel() {

    // This StateFlow will hold the current state of the UI in this case
    private val _pageOfTheDay = MutableStateFlow<NetHackEntity?>(null)
    val pageOfTheDay: StateFlow<NetHackEntity?> = _pageOfTheDay.asStateFlow()

    init {
        // Coroutine for the viewModelScope. Automatically cancelled when ViewModel is cleared
        viewModelScope.launch {
            // Get the Flow of entities, will execute again when data changes.
            repository.allEntities.collect { entities ->
                if (entities.isNotEmpty()) {
                    _pageOfTheDay.value = entities.random()
                }
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