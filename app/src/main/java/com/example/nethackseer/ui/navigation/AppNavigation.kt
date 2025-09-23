package com.example.nethackseer.ui.navigation

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nethackseer.ui.detail.DetailScreen
import com.example.nethackseer.ui.homescreen.HomeScreen
import com.example.nethackseer.ui.homescreen.HomeViewModel
import com.example.nethackseer.ui.typelist.TypeList

/**
 * Function that handles all the places the screen will show to the user.
 * Through here, there is a NavHost that handles all the routes a composable will go through.
 */
@Composable
fun AppNavigation(homeViewModel: HomeViewModel) {
    val navController = rememberNavController()
    // NavHost is the container for all screens here, so tread carefully
    // startDestination is the first screen you see
    NavHost(navController = navController, startDestination = "home") {
        // home screen route
        composable(
            route = "home"
        ) {

            // the state for the search bar is created and owned here
            val textFieldState = rememberTextFieldState()

            HomeScreen(
                homeViewModel = homeViewModel,

                textFieldState = textFieldState,

                // searches for the entity and navigates to the detail screen
                onSearch = { query ->
                    if (query.isNotBlank()) {
                        navController.navigate("detail/$query")
                    }
                },

                // navigates to the typelist screen when a button is pressed
                onNavigateToDetail = { entityId ->
                    navController.navigate("detail/$entityId")
                },

                // navigates to the detail screen when a button is pressed
                onNavigateToType = { typeId ->
                    navController.navigate("type/$typeId")
                }
            )
        }

        // detail screen route
        composable(
            route = "detail/{entityId}", // expects an argument to make for this screen
            arguments = listOf(navArgument("entityId") { type = NavType.StringType })
        ) {
            // when navigating to the detail screen, this lambda is called
            DetailScreen(
                onBack = { navController.popBackStack() } // important for going back
            )
        }

        // typelist screen route
        composable(
            route = "type/{typeId}",
            arguments = listOf(navArgument("typeId") { type = NavType.StringType })
        ){
            TypeList(
                onBack = { navController.popBackStack() }, // again, for going back

                onNavigateToDetail = { entityId ->
                    navController.navigate("detail/$entityId")
                }
            )
        }
    }
}