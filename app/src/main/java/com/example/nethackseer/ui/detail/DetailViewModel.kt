package com.example.nethackseer.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// handles all Success, Error, and Loading states, must be sealed
// i love inheritance...
sealed class EntityUiState {
    object Loading : EntityUiState()
    data class Success(val name: String, val description: String, val type: String) : EntityUiState()
    data class Error(val message: String) : EntityUiState()
}

class DetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    // amazing thing i found where ?: checks if left side is null and sets it to right side
    // otherwise it just uses the left side value
    // savedStateHandle is used to get the entityId from the navigation arguments
    private val entityId: String = savedStateHandle.get<String>("entityId") ?: "Unknown"

    // this just sets the initial state to loading
    private val _uiState = MutableStateFlow<EntityUiState>(EntityUiState.Loading)
    val uiState: StateFlow<EntityUiState> = _uiState

    init {
        fetchEntityData()
    }

    private fun fetchEntityData() {
        viewModelScope.launch {
            // hardcoded stuff for now, later will be fetched from a local database
            when (entityId.lowercase()) {
                "lichen" -> {
                    _uiState.value = EntityUiState.Success(
                        name = "lichen",
                        description = "A type of slow-moving, plant-like fungus. It is weak and can be killed easily. Its only attack is to stick on you.",
                        type = "Monster"
                    )
                }
                "ring of conflict" -> {
                    _uiState.value = EntityUiState.Success(
                        name = "ring of conflict",
                        description = "A powerful magical ring. While worn, it causes all monsters in sight to fight each other instead of attacking you.",
                        type = "Item"
                    )
                }
                "goblin" -> {
                    _uiState.value = EntityUiState.Success(
                        name = "goblin",
                        description = "A small and weak humanoid monster, often found in the early levels of the dungeon.",
                        type = "Monster"
                    )
                }
                "magic lamp" -> {
                    _uiState.value = EntityUiState.Success(
                        name = "magic lamp",
                        description = "A rare item that contains a djinni for the chance to get a wish! Should bless it for a increased 80% chance to get a wish.",
                        type = "Item"
                    )
                }
                else -> {
                    _uiState.value = EntityUiState.Error("Data for '$entityId' could not be found.")
                }
            }
        }
    }
}
