package com.example.nethackseer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nethackseer.ui.homescreen.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home"){
            HomeScreen(
//                onItemsClicked = { navController.navigate("items") },
//                onMonstersClicked = { navController.navigate("monsters") },
//                onSettingsClicked = { navController.navigate("settings") },
//                onAboutClicked = { navController.navigate("about") }
            )
        }
    }
}