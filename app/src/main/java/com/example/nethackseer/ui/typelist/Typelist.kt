package com.example.nethackseer.ui.typelist

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Typelist(
    onBack: () -> Unit,
    typelistViewModel: TypelistViewModel = viewModel()
){

    val uiState by typelistViewModel.uiState.collectAsState()

    // this is the UI to list all element of a certain type (or all types)
    LazyColumn{

    }
}