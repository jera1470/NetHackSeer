package com.example.nethackseer.ui.typelist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.collections.List

sealed class TypeUiState {
    object Loading : TypeUiState()

    data class Success(val listObj: List<String> = emptyList(), val type: String) : TypeUiState()

    data class Error(val message: String) : TypeUiState()
}

class TypelistViewModel(savedStateHandle: SavedStateHandle) : ViewModel(){
    private val typeId: String = savedStateHandle.get<String>("typeId") ?: "Unknown"
    private val _uiState = MutableStateFlow<TypeUiState>(TypeUiState.Loading)
    val uiState: StateFlow<TypeUiState> = _uiState

    init {
        fetchTypeData()
    }

    private fun fetchTypeData(){
        viewModelScope.launch {
            when (typeId.lowercase()) {
                /* TODO: fix the list problem, as it currently just gets the second
                *   item (magic lamp) twice, instead of it being {ring of conflict, magic lamp} */
                "item" -> {
                    _uiState.value = TypeUiState.Success(
                        listObj = List(2) {"ring of conflict"; "magic lamp"},
                        type = "Item"
                    )
                }
                "monster" -> {
                    _uiState.value = TypeUiState.Success(
                        listObj = List(2) {"goblin"; "lichen"},
                        type = "Monster"
                    )
                }
                else -> {
                    _uiState.value = TypeUiState.Error("$typeId not found.")
                }
            }
        }
    }
}

