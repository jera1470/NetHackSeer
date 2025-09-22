package com.example.nethackseer.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nethackseer.ui.theme.DarkRed
import com.example.nethackseer.ui.theme.NetHackSeerTheme
import com.example.nethackseer.ui.theme.Typography
import com.example.nethackseer.ui.theme.White

@Composable
fun DetailScreen(
    onBack: () -> Unit,
    detailViewModel: DetailViewModel = viewModel()
) {
    // important as hell, gotta remember to collect the state before using it
    val uiState by detailViewModel.uiState.collectAsState()
    DetailScreenContent(uiState = uiState, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreenContent(
    uiState: EntityUiState,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details", color = White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            // arrow to go back ye
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkRed)
            )
        }
    ) { paddingValues ->
        // this when block checks the current state and displays the correct UI
        when (val state = uiState) {
            is EntityUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // spinner, loading y'know
                    CircularProgressIndicator()
                }
            }
            // error message when not found
            is EntityUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = state.message, style = Typography.bodyLarge)
                }
            }
            // display if found
            is EntityUiState.Success -> {
                // should know that viewmodel looked it up and got the data, but forget sometimes
                Column(modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)) {
                    Text(text = state.name,
                        style = Typography.headlineMedium)
                    Text(
                        text = "Type: ${state.type}",
                        style = Typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = state.description,
                        style = Typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    NetHackSeerTheme {
        DetailScreenContent(
            uiState = EntityUiState.Success(
                name = "magic lamp",
                type = "Item",
                description = "A rare item that contains a djinni for the chance to get a wish!"
            ),
            onBack = {}
        )
    }
}
