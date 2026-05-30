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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
                                val details = uiState.monsterDetails
                                Text(text = details.name, color = White)
                                Text(text = " (", color = White)
                                Text(
                                    text = getDisplayChar(details.symbol),
                                    color = getNetHackColor(details.color),
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
        when (uiState) {
            is EntityUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }
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
            is EntityUiState.MonsterSuccess -> {
                val details = uiState.monsterDetails
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = details.name,
                            style = Typography.headlineLarge,
                            color = DarkRed,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = getDisplayChar(details.symbol),
                            style = Typography.headlineLarge.copy(fontFamily = FontFamily.Monospace),
                            color = getNetHackColor(details.color),
                            modifier = Modifier.background(Black)
                        )
                    }

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
                                StatItem("Difficulty", "${details.difficulty}")
                                StatItem("Level", details.levelText)
                                StatItem("AC", "${details.ac}")
                                StatItem("MR", "${details.mr}")
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "Speed", style = Typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row {
                                        Text(text = "(${details.speedSlow}) ", style = Typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.Red)
                                        Text(text = "${details.speedBase}", style = Typography.titleLarge, fontWeight = FontWeight.Bold)
                                        Text(text = " (${details.speedFast})", style = Typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF00AA00))
                                    }
                                }
                                StatItem("Weight", "${details.weight}")
                                StatItem("Nutr", "${details.nutrition}")
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem("Size", details.size)
                                StatItem("Alignment", details.alignmentText)
                                StatItem("Base EXP", "${details.baseExp}")
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
                                    details.resistances.forEach { resistance ->
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
                                        text = "Properties Given",
                                        style = Typography.labelLarge,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    
                                    if (details.conferredIntrinsics.isEmpty() && details.specialEffects.isEmpty() && !details.isMindFlayer && !details.isGiant) {
                                        Text(
                                            text = "None",
                                            style = Typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    } else {
                                        details.specialEffects.forEach { effect ->
                                            Text(
                                                text = effect,
                                                style = Typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                        
                                        if (details.specialEffects.isNotEmpty() && (details.conferredIntrinsics.isNotEmpty() || details.isMindFlayer || details.isGiant)) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }

                                        if (details.isMindFlayer) {
                                            Text(text = "+1 Int", style = Typography.bodyMedium, fontWeight = FontWeight.Bold)
                                            Text(text = "(1/2 or 50%)", style = Typography.bodyMedium, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }

                                        if (details.isGiant) {
                                            Text(text = "Increase strength", style = Typography.bodyMedium, fontWeight = FontWeight.Bold)
                                            Text(text = details.giantChanceText, style = Typography.bodyMedium, fontWeight = FontWeight.Bold)
                                            if (details.conferredIntrinsics.isNotEmpty()) Spacer(modifier = Modifier.height(8.dp))
                                        }

                                        details.conferredIntrinsics.forEach { intrinsic ->
                                            Text(
                                                text = intrinsic.name,
                                                style = Typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = intrinsic.chanceText,
                                                style = Typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Attacks",
                                style = Typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(bottom = 8.dp)
                            )

                            if (details.attacks.isEmpty()) {
                                Text(
                                    text = "• None",
                                    style = Typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            } else {
                                details.attacks.forEach { attackText ->
                                    Text(
                                        text = attackText,
                                        style = Typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (details.propertyBulletPoints.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                        ) {
                            Text(
                                text = "The ${details.name}:",
                                style = Typography.titleMedium,
                                color = DarkRed,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            details.propertyBulletPoints.forEach { point ->
                                PropertyBullet(point)
                            }
                        }
                    }
                }
            }
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
fun PropertyBullet(text: String) {
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
            text = text,
            style = Typography.bodyLarge
        )
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = Typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = Typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}
