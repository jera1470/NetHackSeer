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
import com.example.nethackseer.data.local.entity.PropertyEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// handles all Success, Error, and Loading states, must be sealed
sealed class EntityUiState {
    object Loading : EntityUiState()
    data class MonsterSuccess(
        val monster: MonsterEntity,
        val properties: List<PropertyEntity> = emptyList()
    ) : EntityUiState()
    data class ItemSuccess(val item: ItemEntity) : EntityUiState()
    data class Error(val message: String) : EntityUiState()
}

/**
 * View model for the detail screen.
 *
 * @property savedStateHandle The saved state handle for the view model.
 */
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
                // Collect all flag IDs
                val flagIds = mutableListOf<String>()
                if (monster.m1Flags != "0") flagIds.addAll(monster.m1Flags.split("|").map { it.trim() })
                if (monster.m2Flags != "0") flagIds.addAll(monster.m2Flags.split("|").map { it.trim() })
                if (monster.m3Flags != "0") flagIds.addAll(monster.m3Flags.split("|").map { it.trim() })
                // Also resistances for consistency if needed later, but they are currently handled by text in UI
                
                val properties = if (flagIds.isNotEmpty()) {
                    repository.getPropertiesByIds(flagIds).first()
                } else {
                    emptyList()
                }

                _uiState.value = EntityUiState.MonsterSuccess(monster, properties)
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
