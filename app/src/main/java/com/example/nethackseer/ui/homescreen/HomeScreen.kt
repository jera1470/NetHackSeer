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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nethackseer.ui.theme.*


/**
 * Layout for a button shown multiple times.
 *
 * @param text A string to be displayed.
 * @param onClick A lambda to be executed when the button is clicked.
 * @param modifier Modifier to be applied to the button.
 */
@Composable
private fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Red),
        modifier = modifier.padding(8.dp)
    ) {
        Text(text = text)
    }
}

/**
 * The HomeScreen UI layout for the home screen of the app.
 *
 * @param textFieldState An editable text field
 * @param onSearch A lambda to be executed when the search button is clicked.
 * @param onNavigateToDetail A lambda to be executed when the detail button is clicked.
 * This is only used for the page of the day currently.
 * @param onNavigateToType A lambda to be executed when the type button is clicked.
 * @param homeViewModel A view model for the home screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    onNavigateToType: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    homeViewModel: HomeViewModel
) {

    val pageOfTheDay by homeViewModel.pageOfTheDay.collectAsState()
    // search bar stuff, mutableStateOf is used to track changes to the state
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
                .background(LightGray),
            verticalArrangement = Arrangement.Top
        ) {
            /* TODO: implement search bar for later use of searching up stuff (very obvious)
            *   currently justs opens and closes, but it works for now*/
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
                ActionButton("Items",
                    onClick = {onNavigateToType("item")},
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f))
                ActionButton("Monsters",
                    onClick = {onNavigateToType("monster")},
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f))
            }
            ActionButton("Dungeon Features",
                onClick = { /*TODO*/},
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth())
            Row {
                ActionButton("Roles",
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f))
                ActionButton("Properties",
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f))
            }
            Card (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = LightRed),
                // lambda here to navigate to the detail screen when card is pressed
                onClick = {
                    pageOfTheDay?.let { page ->
                        onNavigateToDetail(page.id)
                    }
                }
            ) {
                pageOfTheDay?.let { page ->
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ){
                        Text(
                            text = page.type.replaceFirstChar{ it.titlecase() } + " of the day",
                            color = Black,
                            style = Typography.titleLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(0.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        Text(
                            text = page.name,
                            color = Black,
                            style = Typography.headlineMedium
                        )
                        Text(
                            text = page.description,
                            color = Black,
                            style = Typography.bodyLarge
                        )
                    }
                } ?: run {
                    Box (
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                       Text(text = "oops.")
                    }
                }
            }
        }
    }
}

// Shows a preview of the home screen.
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    NetHackSeerTheme {
        HomeScreen(textFieldState = rememberTextFieldState(),
            onSearch = {},
            onNavigateToDetail = {},
            onNavigateToType = {},
            homeViewModel = viewModel())
    }
}