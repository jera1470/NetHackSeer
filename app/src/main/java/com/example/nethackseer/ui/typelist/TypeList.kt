package com.example.nethackseer.ui.typelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nethackseer.ui.theme.DarkRed
import com.example.nethackseer.ui.theme.Typography
import com.example.nethackseer.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Typelist(
    onBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    typelistViewModel: TypelistViewModel = viewModel()
){

    val uiState by typelistViewModel.uiState.collectAsState()

    // this is the UI to list all element of a certain type (or all types)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Typelist", color = DarkRed) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = White
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is TypeUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }
            // error message when not found
            is TypeUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = state.message, style = Typography.bodyLarge)
                }
            }

            is TypeUiState.Success -> {
                LazyColumn (modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                        .fillMaxSize()) {
                    items(state.listObj.size) { index ->
                        Button(onClick = { onNavigateToDetail(state.listObj[index]) }) {
                            Text(text = state.listObj[index])
                        }
                    }
                }
            }
        }

    }
}