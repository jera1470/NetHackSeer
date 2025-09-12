package com.example.nethackseer.ui.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nethackseer.ui.theme.Black
import com.example.nethackseer.ui.theme.DarkRed
import com.example.nethackseer.ui.theme.LightRed
import com.example.nethackseer.ui.theme.NetHackSeerTheme
import com.example.nethackseer.ui.theme.Red
import com.example.nethackseer.ui.theme.Typography
import com.example.nethackseer.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
//    onItemsClicked: () -> Unit,
//    onMonstersClicked: () -> Unit,
//    onSettingsClicked: () -> Unit,
//    onAboutClicked: () -> Unit
) {
    val buttonShape = RoundedCornerShape(16.dp)
    val buttonColors = ButtonDefaults.buttonColors(Red)
    val buttonModifier = Modifier.padding(8.dp)

    var expanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Hello, welcome to NetHackSeer!",
                        color = White,
                        style = Typography.titleLarge)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkRed)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFE0E0E0)), // Light gray background
            verticalArrangement = Arrangement.Top
        ) {
            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = textFieldState.text.toString(),
                        onQueryChange = { textFieldState.edit { replace(0, length, it) }},
                        onSearch = {
                            onSearch(textFieldState.text.toString())
                            expanded = false
                        },
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        placeholder = { Text(text = "Search") }
                    )
                },
                expanded = expanded,
                onExpandedChange = { expanded = it },
            ) {}
            Row (Modifier.fillMaxWidth()){
                Button(
                    onClick = { /*TODO*/ },
                    shape = buttonShape,
                    colors = buttonColors,
                    modifier = buttonModifier.weight(1f)
                ) {
                    Text(text = "Items")
                }
                Button(
                    onClick = { /*TODO*/ },
                    shape = buttonShape,
                    colors = buttonColors,
                    modifier = buttonModifier.weight(1f)
                ) {
                    Text(text = "Monsters")
                }
            }
            Button(
                onClick = { /*TODO*/ },
                shape = buttonShape,
                colors = buttonColors,
                modifier = buttonModifier.fillMaxWidth()
            ) {
                Text(text = "Dungeon Features")
            }
            Row {
                Button(
                    onClick = { /*TODO*/ },
                    shape = buttonShape,
                    colors = buttonColors,
                    modifier = buttonModifier.weight(1f)
                ) {
                    Text(text = "Roles")
                }
                Button(
                    onClick = { /*TODO*/ },
                    shape = buttonShape,
                    colors = buttonColors,
                    modifier = buttonModifier.weight(1f)
                ) {
                    Text(text = "Properties")
                }
            }
            Card (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(LightRed)
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Today's Page",
                        color = Black,
                        style = Typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
                Text("yo, NetHack is awesome lol",
                    color = Black,
                    style = Typography.bodyLarge,
                    modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    NetHackSeerTheme {
        HomeScreen(textFieldState = rememberTextFieldState(), onSearch = {})
    }
}