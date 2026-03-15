package com.example.nethackseer.ui.typelist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.nethackseer.data.NetHackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class TypeUiState {
    object Loading : TypeUiState()

    data class Success(val listObj: List<String> = emptyList(), val type: String) : TypeUiState()

    data class Error(val message: String) : TypeUiState()
}

class TypeListViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: NetHackRepository
) : ViewModel() {
    private val typeId: String = savedStateHandle.get<String>("typeId") ?: "Unknown"
    private val _uiState = MutableStateFlow<TypeUiState>(TypeUiState.Loading)
    val uiState: StateFlow<TypeUiState> = _uiState

    init {
        fetchTypeData()
    }

    private fun fetchTypeData() {
        viewModelScope.launch {
            when (typeId.lowercase()) {
                "monster" -> {
                    repository.allMonsters.collect { monsters ->
                        _uiState.value = TypeUiState.Success(
                            listObj = monsters.map { it.name },
                            type = typeId
                        )
                    }
                }

                else -> {
                    _uiState.value = TypeUiState.Error("not done with this yet")
                }
            }
        }
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

