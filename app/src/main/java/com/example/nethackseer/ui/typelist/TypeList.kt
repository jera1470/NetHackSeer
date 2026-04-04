package com.example.nethackseer.ui.typelist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nethackseer.NetHackSeerApplication
import com.example.nethackseer.ui.theme.Black
import com.example.nethackseer.ui.theme.DarkRed
import com.example.nethackseer.ui.theme.Typography
import com.example.nethackseer.ui.theme.White
import com.example.nethackseer.ui.utils.cleanNetHackName
import com.example.nethackseer.ui.utils.getDisplayChar
import com.example.nethackseer.ui.utils.getNetHackColor

/**
 * A TypeList UI layout for listing entities in a list.
 * This is used when showing a different route for showing all possible entities when given a query,
 *
 * @param onBack A lambda to be executed when the back button is clicked.
 * @param onNavigateToDetail A lambda to be executed when an entity is clicked.
 * @param typeListViewModel A view model for the typelist screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeList(
    onBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    typeListViewModel: TypeListViewModel = viewModel(
        factory = TypeListViewModel.Factory(
            (LocalContext.current.applicationContext as NetHackSeerApplication).repository
        )
    ),
) {

    val uiState by typeListViewModel.uiState.collectAsState()

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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = state.message, style = Typography.bodyLarge)
                }
            }

            is TypeUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    items(state.listObj.size) { index ->
                        val summary = state.listObj[index]
                        Button(
                            onClick = { onNavigateToDetail(summary.name) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = cleanNetHackName(summary.name),
                                    textAlign = TextAlign.Start
                                )
                                Text(
                                    text = getDisplayChar(summary.symbol),
                                    color = getNetHackColor(summary.color),
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.background(Black)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
