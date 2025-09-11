package com.example.nethackseer.ui.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    val buttonShape = RoundedCornerShape(16.dp)
    val buttonColors = ButtonDefaults.buttonColors(containerColor = Color.Red)
    val buttonModifier = Modifier.padding(8.dp)

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "NetHack Seer Home")
            })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFE0E0E0)), // Light gray background
            verticalArrangement = Arrangement.Top
        ) {
            Row {
                Button(
                    onClick = { /*TODO*/ },
                    shape = buttonShape,
                    colors = buttonColors,
                    modifier = buttonModifier
                ) {
                    Text(text = "Items")
                }
                Button(
                    onClick = { /*TODO*/ },
                    shape = buttonShape,
                    colors = buttonColors,
                    modifier = buttonModifier
                ) {
                    Text(text = "Monsters")
                }
            }
            Button(
                onClick = { /*TODO*/ },
                shape = buttonShape,
                colors = buttonColors,
                modifier = buttonModifier.widthIn(min = 200.dp) // Make this button wider
            ) {
                Text(text = "Dungeon Features")
            }
            Row {
                Button(
                    onClick = { /*TODO*/ },
                    shape = buttonShape,
                    colors = buttonColors,
                    modifier = buttonModifier
                ) {
                    Text(text = "Roles")
                }
                Button(
                    onClick = { /*TODO*/ },
                    shape = buttonShape,
                    colors = buttonColors,
                    modifier = buttonModifier
                ) {
                    Text(text = "Properties")
                }
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