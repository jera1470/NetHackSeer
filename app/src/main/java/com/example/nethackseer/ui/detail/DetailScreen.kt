package com.example.nethackseer.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nethackseer.NetHackSeerApplication
import com.example.nethackseer.ui.theme.*
import com.example.nethackseer.ui.utils.getDisplayChar
import com.example.nethackseer.ui.utils.getNetHackColor

/**
 * The DetailScreen UI layout for the detail screen of the app.
 *
 * @param onBack A lambda to be executed when the back button is clicked.
 * @param detailViewModel A view model for the detail screen.
 */
@Composable
fun DetailScreen(
    onBack: () -> Unit,
    detailViewModel: DetailViewModel = viewModel(
        factory = DetailViewModel.Factory(
            (LocalContext.current.applicationContext as NetHackSeerApplication).repository
        )
    )
) {
    // important as hell, gotta remember to collect the state before using it
    val uiState by detailViewModel.uiState.collectAsState()
    DetailScreenContent(uiState = uiState, onBack = onBack)
}

/**
 * The content shown for the detail screen. Needed specifically for the EntityUiState.
 *
 * @param uiState the current state of the UI
 * @param onBack A lambda to be executed when the back button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreenContent(
    uiState: EntityUiState,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        when (uiState) {
                            is EntityUiState.MonsterSuccess -> {
                                Text(text = uiState.monster.name, color = White)
                                Text(text = " (", color = White)
                                Text(
                                    text = getDisplayChar(uiState.monster.symbol),
                                    color = getNetHackColor(uiState.monster.color),
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.background(Black)
                                )
                                Text(text = ")", color = White)
                            }
                            is EntityUiState.ItemSuccess -> {
                                Text(text = uiState.item.name, color = White)
                                Text(text = " (", color = White)
                                Text(
                                    text = getDisplayChar(uiState.item.symbol),
                                    color = getNetHackColor(uiState.item.color),
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.background(Black)
                                )
                                Text(text = ")", color = White)
                            }
                            is EntityUiState.Loading -> Text(text = "wait...", color = White)
                            is EntityUiState.Error -> Text(text = "error", color = White)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            // arrow to go back ye
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkRed)
            )
        }
    ) { paddingValues ->
        // this when block checks the current state and displays the correct UI
        when (uiState) {
            is EntityUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // loading spinner
                    CircularProgressIndicator()
                }
            }
            // error message when not found
            is EntityUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = uiState.message, style = Typography.bodyLarge)
                }
            }
            // display if monster found
            is EntityUiState.MonsterSuccess -> {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = uiState.monster.name,
                            style = Typography.headlineLarge,
                            color = DarkRed,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = getDisplayChar(uiState.monster.symbol),
                            style = Typography.headlineLarge.copy(fontFamily = FontFamily.Monospace),
                            color = getNetHackColor(uiState.monster.color),
                            modifier = Modifier.background(Black)
                        )
                    }
                    Text(
                        text = "Class: ${uiState.monster.symbol}",
                        style = Typography.titleMedium,
                        color = DarkGray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Stats",
                                style = Typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem("Level", "${uiState.monster.level}")
                                StatItem("AC", "${uiState.monster.ac}")
                                StatItem("MR", "${uiState.monster.mr}")
                                StatItem("Speed", "${uiState.monster.moveRate}")
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem("Diff", "${uiState.monster.difficulty}")
                                StatItem("Weight", "${uiState.monster.weight}")
                                StatItem("Nutr", "${uiState.monster.nutritionValue}")
                                StatItem("Size", uiState.monster.size.removePrefix("MZ_").lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Resistances",
                                        style = Typography.labelLarge,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    val resistancesList = if (uiState.monster.resistances == "0") {
                                        listOf("None")
                                    } else {
                                        uiState.monster.resistances.split("|").map {
                                            it.trim().removePrefix("MR_").lowercase()
                                                .replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() }
                                        }
                                    }
                                    resistancesList.forEach { resistance ->
                                        Text(
                                            text = resistance,
                                            style = Typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                VerticalDivider(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .fillMaxHeight(),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )

                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Res. Conferred",
                                        style = Typography.labelLarge,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    val conferredRaw = uiState.monster.resistancesConferred
                                    val conferredList = if (conferredRaw == "0") {
                                        emptyList()
                                    } else {
                                        conferredRaw.split("|")
                                            .map { it.trim() }
                                            .filter { !it.contains("STONE") && !it.contains("ACID") }
                                    }

                                    if (conferredList.isEmpty()) {
                                        Text(
                                            text = "None",
                                            style = Typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    } else {
                                        val ml = uiState.monster.level.toDouble()
                                        val count = conferredList.size
                                        conferredList.forEach { id ->
                                            val pConfer = when {
                                                id.contains("TELEPATHY", ignoreCase = true) -> 1.0
                                                id.contains("TELEPORTITIS", ignoreCase = true) -> ml / 10.0
                                                id.contains("TELEPORT", ignoreCase = true) && id.contains("CONTROL", ignoreCase = true) -> ml / 12.0
                                                id.contains("POISON", ignoreCase = true) &&
                                                        (uiState.monster.name.contains("killer bee", ignoreCase = true) ||
                                                                uiState.monster.name.contains("scorpion", ignoreCase = true)) -> (ml + 5.0) / 20.0
                                                else -> ml / 15.0
                                            }
                                            val totalChance = ((1.0 / count) * pConfer).coerceIn(0.0, 1.0)
                                            val percentageExact = totalChance * 100
                                            val percentage = percentageExact.toInt()
                                            val prefix = if (percentageExact != percentage.toDouble()) "~" else ""

                                            val name = id.removePrefix("MR_").lowercase()
                                                .replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() }

                                            Text(
                                                text = "$name\n($prefix$percentage%)",
                                                style = Typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Properties section (M1, M2, M3, and Geno flags)
                    val displayProperties = uiState.properties.filter { 
                        it.id.startsWith("M1_") || 
                        it.id.startsWith("M2_") || 
                        it.id.startsWith("M3_") || 
                        it.id.startsWith("G_") 
                    }
                    
                    if (displayProperties.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                        ) {
                            Text(
                                text = "The ${uiState.monster.name}:",
                                style = Typography.titleMedium,
                                color = DarkRed,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            displayProperties.forEach { prop ->
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "• ",
                                        style = Typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkRed
                                    )
                                    Text(
                                        text = prop.summary.ifEmpty { prop.description },
                                        style = Typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
            // display if item found
            is EntityUiState.ItemSuccess -> {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = uiState.item.name,
                            style = Typography.headlineMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = getDisplayChar(uiState.item.symbol),
                            style = Typography.headlineMedium.copy(fontFamily = FontFamily.Monospace),
                            color = getNetHackColor(uiState.item.color),
                            modifier = Modifier.background(Black)
                        )
                    }
                    Text(
                        text = "Type: Item (${uiState.item.symbol})",
                        style = Typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "Weight: ${uiState.item.weight}",
                        style = Typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = Typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = Typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}
