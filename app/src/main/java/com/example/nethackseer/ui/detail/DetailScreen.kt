package com.example.nethackseer.ui.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.example.nethackseer.ui.utils.getSymbolDisplayName

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
                                val details = uiState.itemDetails
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
                            is EntityUiState.Loading -> Text(text = "wait...", color = White)
                            is EntityUiState.Error -> Text(text = "error", color = White)
                            is EntityUiState.PropertySuccess -> TODO()
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

                    val monsterType = getSymbolDisplayName(details.symbol)
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = buildString {
                            append("Symbol: $monsterType")
                            if (details.maleName != null || details.femaleName != null) {
                                append("  |  ")
                                if (details.maleName != null) append("♂ ${details.maleName}")
                                if (details.maleName != null && details.femaleName != null) append("  |  ")
                                if (details.femaleName != null) append("♀ ${details.femaleName}")
                            }
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            StatBox("Difficulty", "${details.difficulty}")
                            StatBox("Level", details.levelText)
                            StatBox("AC", "${details.ac}")
                            StatBox("MR", "${details.mr}")

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Speed",
                                        style = Typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Row {
                                        Text(
                                            text = "(${details.speedSlow}) ",
                                            style = Typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Red
                                        )
                                        Text(
                                            text = "${details.speedBase}",
                                            style = Typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = " (${details.speedFast})",
                                            style = Typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF00AA00)
                                        )
                                    }
                                }
                            }

                            StatBox("Weight", "${details.weight}")
                            StatBox("Nutr", "${details.nutrition}")
                            StatBox("Size", details.size)
                            StatBox("Alignment", details.alignmentText)
                            StatBox("Base EXP", "${details.baseExp}")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(12.dp)
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
                                            style = Typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(vertical = 1.dp)
                                        )
                                    } else {
                                        details.attacks.forEach { attackText ->
                                            Text(
                                                text = attackText,
                                                style = Typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Resistances",
                                        style = Typography.labelLarge,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    details.resistances.forEach { resistance ->
                                        Text(
                                            text = resistance,
                                            style = Typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "When Eaten",
                                        style = Typography.labelLarge,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(bottom = 8.dp)
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
                                            Text(
                                                text = "+1 Int",
                                                style = Typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "(50%)",
                                                style = Typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }

                                        if (details.isGiant) {
                                            Text(
                                                text = "Increase strength",
                                                style = Typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = details.giantChanceText,
                                                style = Typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (details.conferredIntrinsics.isNotEmpty()) Spacer(
                                                modifier = Modifier.height(8.dp)
                                            )
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
                val details = uiState.itemDetails
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

                    if (details.headerSubtitle.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = details.headerSubtitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            StatBox("Weight", "${details.weight}")
                            StatBox("Value", "${details.value}")
                            StatBox("Material", details.material)
                            if (details.probabilityText != null) {
                                StatBox("Rel. Prob.", details.probabilityText)
                            }
                            if (details.delay != null && details.delay > 0) {
                                // just in case something was missed
                                StatBox(details.delayLabel, "${details.delay} turn${if (details.delay > 1) "s" else ""}")
                            }
                            if (details.nutrition != null && details.nutrition > 0) {
                                StatBox("Nutr", "${details.nutrition}")
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            if (details.ac != null && details.symbol == "ARMOR_CLASS") {
                                StatBox("Base AC", details.ac)
                            }
                            if (details.mc != null && details.mc > 0) {
                                StatBox("MC", "MC${details.mc}")
                            }
                            if (details.damageSmall != null) {
                                StatBox("Dmg (S)", details.damageSmall)
                            }
                            if (details.damageLarge != null) {
                                StatBox("Dmg (L)", details.damageLarge)
                            }
                            if (details.hitBonus != null && details.hitBonus != 0) {
                                val sign = if (details.hitBonus > 0) "+" else ""
                                StatBox("To-Hit", "$sign${details.hitBonus}")
                            }
                            if (details.spellLevel != null && details.spellLevel > 0) {
                                StatBox("Spell Lvl", "Lvl ${details.spellLevel}")
                            }
                            if (details.zapDirectionText != null) {
                                StatBox("Zap Dir", details.zapDirectionText)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Properties Conferred",
                                    style = Typography.labelLarge,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                if (details.conferredProperties.isEmpty()) {
                                    Text(
                                        text = "None",
                                        style = Typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    details.conferredProperties.forEach { prop ->
                                        Text(
                                            text = prop,
                                            style = Typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Capabilities",
                                    style = Typography.labelLarge,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                if (details.capabilities.isEmpty()) {
                                    Text(
                                        text = "None",
                                        style = Typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    details.capabilities.forEach { cap ->
                                        Text(
                                            text = cap,
                                            style = Typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            is EntityUiState.PropertySuccess -> TODO()
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
fun StatBox(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = Typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = value,
                style = Typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
