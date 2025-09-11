package com.example.nethackseer.ui.homescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nethackseer.ui.theme.NetHackSeerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
//    onItemsClicked: () -> Unit,
//    onMonstersClicked: () -> Unit,
//    onSettingsClicked: () -> Unit,
//    onAboutClicked: () -> Unit
) {
    Scaffold (
        topBar = {
            TopAppBar(title = {
                Text(text = "NetHack Seer Home")
            })
        }
    ){
        innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Button(onClick = { homeViewModel.onItemsClicked() }) {
                Text(text = "Items")
            }
            Button(onClick = { homeViewModel.onMonstersClicked() }) {
                Text(text = "Monsters")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    NetHackSeerTheme {
        HomeScreen()
    }
}