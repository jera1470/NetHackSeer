package com.example.nethackseer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.nethackseer.data.NetHackRepository
import com.example.nethackseer.data.local.entity.NetHackEntity
import com.example.nethackseer.ui.homescreen.HomeViewModel
import com.example.nethackseer.ui.homescreen.HomeViewModelFactory
import com.example.nethackseer.ui.navigation.AppNavigation
import com.example.nethackseer.ui.theme.NetHackSeerTheme

/**
 * Called when the activity is first created.
 *
 * @param savedInstanceState If the activity is being re-initialized after
 * previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // repo from application class
        val repository = (application as NetHackSeerApplication).repository

        val homeViewModel: HomeViewModel by viewModels {
            HomeViewModelFactory(repository)
        }
        setContent {
            NetHackSeerTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(homeViewModel = homeViewModel)
                }
            }
        }
    }
}