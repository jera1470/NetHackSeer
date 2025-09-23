package com.example.nethackseer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        setContent {
            NetHackSeerTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlaceHolderContent()
                }
            }
        }
    }
}

/**
 * A function that serves as a placeholder for the content of the app.
 */
@Composable
fun PlaceHolderContent(){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){
        AppNavigation()
    }

}