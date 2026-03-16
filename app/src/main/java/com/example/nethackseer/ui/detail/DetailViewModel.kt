package com.example.nethackseer.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.nethackseer.data.NetHackRepository
import com.example.nethackseer.data.local.entity.ItemEntity
import com.example.nethackseer.data.local.entity.MonsterEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// handles all Success, Error, and Loading states, must be sealed
sealed class EntityUiState {
    object Loading : EntityUiState()
    data class MonsterSuccess(val monster: MonsterEntity) : EntityUiState()
    data class ItemSuccess(val item: ItemEntity) : EntityUiState()
    data class Error(val message: String) : EntityUiState()
}

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: NetHackRepository, // inject this
) : ViewModel() {

    private val entityId: String = savedStateHandle.get<String>("entityId") ?: "Unknown"

    private val _uiState = MutableStateFlow<EntityUiState>(EntityUiState.Loading)
    val uiState: StateFlow<EntityUiState> = _uiState.asStateFlow()

    init {
        loadEntity()
    }

    private fun loadEntity() {
        if (entityId == "Unknown") {
            _uiState.value = EntityUiState.Error("Entity ID missing")
            return
        }

        viewModelScope.launch {
            // First, try to find a monster
            val monster = repository.getMonsterByName(entityId).first()
            if (monster != null) {
                _uiState.value = EntityUiState.MonsterSuccess(monster)
                return@launch
            }

            // If not a monster, try to find an item
            val item = repository.getItemByName(entityId).first()
            if (item != null) {
                _uiState.value = EntityUiState.ItemSuccess(item)
                return@launch
            }

            // If neither, show error
            _uiState.value = EntityUiState.Error("Entity '$entityId' not found")
        }
    }

    // Factory for creating the viewmodel with repository
    companion object {
        fun Factory(repository: NetHackRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    val savedStateHandle = extras.createSavedStateHandle()
                    return DetailViewModel(savedStateHandle, repository) as T
                }
            }
    }
}
