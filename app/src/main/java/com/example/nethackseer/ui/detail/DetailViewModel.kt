package com.example.nethackseer.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.nethackseer.data.NetHackRepository
import com.example.nethackseer.data.local.entity.MonsterEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// handles all Success, Error, and Loading states, must be sealed
// i love inheritance...
sealed class EntityUiState {
    object Loading : EntityUiState()
    data class Success(val monsterEntity: MonsterEntity, val type: String) : EntityUiState()
    data class Error(val message: String) : EntityUiState()
}

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: NetHackRepository, // Inject this
) : ViewModel() {

    private val entityId: String = savedStateHandle.get<String>("entityId") ?: "Unknown"

    // Transform the database flow into UI State
    val uiState: StateFlow<EntityUiState> = if (entityId == "Unknown") {
        MutableStateFlow(EntityUiState.Error("Entity ID missing"))
    } else {
        repository.getMonsterByName(entityId)
            .map { monster ->
                if (monster != null) {
                    EntityUiState.Success(monster, "Monster")
                } else {
                    EntityUiState.Error("Monster '$entityId' not found")
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = EntityUiState.Loading
            )
    }

    // Factory for creating the viewmodel with repository
    companion object {
        fun Factory(repository: NetHackRepository) : ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass : Class<T>,
                                                    extras: CreationExtras): T {
                    val savedStateHandle = extras.createSavedStateHandle()
                    return DetailViewModel(savedStateHandle, repository) as T
                }
            }
    }
}
