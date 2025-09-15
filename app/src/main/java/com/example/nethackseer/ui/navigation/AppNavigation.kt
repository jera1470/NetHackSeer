package com.example.nethackseer.ui.navigation

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nethackseer.ui.homescreen.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // NavHost is the container for all screens here, so tread carefully
    // what startDestination does is that it's the first screen you see
    NavHost(navController = navController, startDestination = "home") {
        // home screen route
        composable("home") {

            // the state for the search bar is created and owned here
            val textFieldState = rememberTextFieldState()

            HomeScreen(
                textFieldState = textFieldState,

                // searches for the entity and navigates to the detail screen
                onSearch = { query ->
                    if (query.isNotBlank()) {
                        navController.navigate("detail/$query")
                    }
                },

                // navigates to the detail screen when a button is pressed
                onNavigateToDetail = { entityId ->
                    navController.navigate("detail/$entityId")
                }
            )
        }
    }
}