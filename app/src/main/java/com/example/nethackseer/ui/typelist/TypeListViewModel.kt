package com.example.nethackseer.ui.typelist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.nethackseer.data.NetHackRepository
import com.example.nethackseer.ui.utils.cleanNetHackName
import com.example.nethackseer.ui.utils.getSymbolDisplayName
import com.example.nethackseer.ui.utils.getDisplayChar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class EntitySummary(
    val name: String,
    val symbol: String,
    val color: String
)

sealed class TypeUiState {
    object Loading : TypeUiState()

    data class Success(val listObj: List<EntitySummary> = emptyList(), val type: String) : TypeUiState()

    data class Error(val message: String) : TypeUiState()
}

class TypeListViewModel(
    savedStateHandle: SavedStateHandle,
    repository: NetHackRepository
) : ViewModel() {
    private val typeId: String = savedStateHandle.get<String>("typeId") ?: "Unknown"

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _rawEntities = when (typeId.lowercase()) {
        "monster" -> repository.allMonsters.map { monsters ->
            monsters.map { EntitySummary(it.name, it.symbol, it.color) }
        }
        "item" -> repository.allItems.map { items ->
            items.map { EntitySummary(it.name, it.symbol, it.color) }
        }
        else -> flowOf(null)
    }

    val uiState: StateFlow<TypeUiState> = combine(_rawEntities, _searchQuery) { entities, query ->
        if (entities == null) {
            TypeUiState.Error("Invalid type ID: $typeId")
        } else {
            val trimmed = query.trim().lowercase()
            val filtered = if (trimmed.isEmpty()) {
                entities
            } else {
                entities.filter { summary ->
                    summary.name.contains(trimmed, ignoreCase = true) ||
                            cleanNetHackName(summary.name).contains(trimmed, ignoreCase = true) ||
                            getDisplayChar(summary.symbol).equals(trimmed, ignoreCase = true) ||
                            getSymbolDisplayName(summary.symbol).contains(trimmed, ignoreCase = true)
                }
            }
            TypeUiState.Success(listObj = filtered, type = typeId)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TypeUiState.Loading
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    // factory for creating the viewmodel with repository
    companion object {
        fun Factory(repository: NetHackRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    val savedStateHandle = extras.createSavedStateHandle()
                    return TypeListViewModel(savedStateHandle, repository) as T
                }
            }
    }
}
