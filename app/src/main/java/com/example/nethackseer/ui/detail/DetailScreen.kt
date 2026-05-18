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
                                            val id = it.trim()
                                            when (id) {
                                                // better than just elec or disint
                                                "MR_ELEC" -> "Shock"
                                                "MR_DISINT" -> "Disintegrate"
                                                else -> id.removePrefix("MR_").lowercase()
                                                    .replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() }
                                            }
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
                                        text = "Properties Given",
                                        style = Typography.labelLarge,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    val conferredRaw = uiState.monster.resistancesConferred
                                    val baseConferred = if (conferredRaw == "0") {
                                        mutableListOf()
                                    } else {
                                        conferredRaw.split("|").map { it.trim() }.toMutableList()
                                    }
                                    
                                    // Add intrinsics from M1 flags and special cases
                                    if (uiState.monster.m1Flags.contains("M1_TPORT")) {
                                        if (!baseConferred.contains("MR_TELEPORT")) baseConferred.add("MR_TELEPORT")
                                    }
                                    if (uiState.monster.m1Flags.contains("M1_TPORT_CNTRL")) {
                                        if (!baseConferred.contains("MR_TELEPORT_CONTROL")) baseConferred.add("MR_TELEPORT_CONTROL")
                                    }
                                    // Special post-eating effects (cpostfx function in eat.c)
                                    val specialEffects = mutableListOf<String>()
                                    val lowerName = uiState.monster.name.lowercase()
                                    if (lowerName == "wraith") specialEffects.add("Gain level")
                                    if (lowerName.contains("were")) specialEffects.add("Contract lycanthropy")
                                    if (lowerName.contains("mimic")) specialEffects.add("Mimic an object (20-50 turns)")
                                    if (listOf("chameleon", "doppelganger", "genetic engineer").any { lowerName == it }) specialEffects.add("Polymorph")
                                    if (lowerName == "nurse") specialEffects.add("Full heal/cure blindness")
                                    if (lowerName == "lizard") specialEffects.add("Reduce stun/confusion")
                                    if (lowerName == "stalker") specialEffects.add("Temp. Invis. (+50-149 turns)\nPerm. Invis./See Invis if already invis.")
                                    if (lowerName == "displacer beast") specialEffects.add("Temp. displacement (+6-36 turns)")
                                    if (listOf("yellow light", "bat", "giant bat").any { lowerName == it }) specialEffects.add("Stun (+30 turns)")
                                    if (lowerName == "quantum mechanic") specialEffects.add("Toggle speed")
                                    if (lowerName == "disenchanter") specialEffects.add("Lose a random intrinsic")
                                    val hasHaluAttack = listOf(uiState.monster.attack1, uiState.monster.attack2, uiState.monster.attack3, uiState.monster.attack4, uiState.monster.attack5, uiState.monster.attack6)
                                        .any { it.damageType == "AD_HALU" || it.damageType == "AD_STUN" }
                                    if (lowerName == "violet fungus" || hasHaluAttack) specialEffects.add("Hallucination (+200 turns)")
                                    val isMagical = listOf(uiState.monster.attack1, uiState.monster.attack2, uiState.monster.attack3, uiState.monster.attack4, uiState.monster.attack5, uiState.monster.attack6)
                                        .any { it.type == "AT_MAGC" }
                                    if (lowerName == "newt" || isMagical) specialEffects.add("Increase energy")
                                    if (listOf("death", "pestilence", "famine").any { lowerName == it }) specialEffects.add("Death when eaten")

                                    // Neater list for intrinsics
                                    val conferredList = baseConferred

                                    if (conferredList.isEmpty() && specialEffects.isEmpty() && !lowerName.contains("mind flayer") && uiState.monster.symbol != "S_GIANT") {
                                        Text(
                                            text = "None",
                                            style = Typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    } else {
                                        // Display additive special effects first
                                        specialEffects.forEach { effect ->
                                            Text(
                                                text = effect,
                                                style = Typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                        
                                        if (specialEffects.isNotEmpty() && (conferredList.isNotEmpty() || lowerName.contains("mind flayer") || uiState.monster.symbol == "S_GIANT")) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }

                                        // Competitive pool calculation (giant and mind flayers)
                                        val isGiant = uiState.monster.symbol == "S_GIANT"
                                        val isMindFlayer = lowerName.contains("mind flayer")
                                        
                                        // Mind flayer is 50/50 +1 int and telepathy
                                        if (isMindFlayer) {
                                            Text(text = "+1 Int", style = Typography.bodyMedium, fontWeight = FontWeight.Bold)
                                            Text(text = "(1/2 or 50%)", style = Typography.bodyMedium, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }

                                        val poolMultiplier = if (isMindFlayer) 2L else 1L
                                        val poolSize = conferredList.size + (if (isGiant) 1 else 0)
                                        
                                        // Handle giants with only strength (50% fail rate)
                                        val giantFailMultiplier = if (isGiant && conferredList.isEmpty()) 2L else 1L
                                        val finalDenomBase = poolSize.toLong() * poolMultiplier * giantFailMultiplier

                                        if (isGiant) {
                                            fun getGcd(a: Long, b: Long): Long = if (b == 0L) a else getGcd(b, a % b)
                                            val common = getGcd(1, finalDenomBase)
                                            val dNum = 1 / common
                                            val dDen = finalDenomBase / common
                                            val perc = ((dNum.toDouble() / dDen.toDouble()) * 100.0).toInt()

                                            Text(text = "Increase strength", style = Typography.bodyMedium, fontWeight = FontWeight.Bold)
                                            Text(text = "($dNum/$dDen or $perc%)", style = Typography.bodyMedium, fontWeight = FontWeight.Bold)
                                            if (conferredList.isNotEmpty()) Spacer(modifier = Modifier.height(8.dp))
                                        }

                                        conferredList.forEach { id ->
                                            val isBeeOrScorpion = lowerName.contains("killer bee") || lowerName.contains("scorpion")

                                            // Logic from NetHack 5.0.0 per-intrinsic check
                                            val (num, den) = when {
                                                id.contains("TELEPATHY", ignoreCase = true) -> 1L to 1L
                                                id.contains("TELEPORT", ignoreCase = true) && !id.contains("CONTROL", ignoreCase = true) -> 
                                                    uiState.monster.level.toLong().coerceAtMost(10L) to 10L
                                                id.contains("TELEPORT", ignoreCase = true) && id.contains("CONTROL", ignoreCase = true) -> 
                                                    uiState.monster.level.toLong().coerceAtMost(12L) to 12L
                                                id.contains("POISON", ignoreCase = true) && isBeeOrScorpion -> 
                                                    (uiState.monster.level.toLong() + 5).coerceAtMost(20L) to 20L
                                                id.contains("ACID", ignoreCase = true) -> {
                                                    val lvl = uiState.monster.level.toLong()
                                                    if (lvl >= 3) 1L to 1L
                                                    else (18 * lvl - lvl * lvl) to 45L
                                                }
                                                id.contains("STONE", ignoreCase = true) -> {
                                                    val lvl = uiState.monster.level.toLong()
                                                    if (lvl >= 6) 1L to 1L
                                                    else (21 * lvl - lvl * lvl) to 90L
                                                }
                                                else -> uiState.monster.level.toLong().coerceAtMost(15L) to 15L
                                            }

                                            val finalNum = num
                                            val finalDen = finalDenomBase * den

                                            // Simplify fraction using GCD (courtesy of a friend)
                                            fun getGcd(a: Long, b: Long): Long = if (b == 0L) a else getGcd(b, a % b)
                                            val common = getGcd(finalNum, finalDen)
                                            val displayNum = finalNum / common
                                            val displayDen = finalDen / common

                                            val totalChanceExact = (displayNum.toDouble() / displayDen.toDouble()) * 100.0
                                            val percentage = totalChanceExact.toInt().coerceIn(0, 100)
                                            val prefix = if (totalChanceExact > percentage.toDouble() && percentage < 100) "~" else ""

                                            var name = when (id) {
                                                "MR_ELEC" -> "Shock"
                                                "MR_DISINT" -> "Disintegrate"
                                                else -> id.removePrefix("MR_").lowercase().replace("_", " ")
                                                    .replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() }
                                            }
                                            
                                            if (id.contains("ACID") || id.contains("STONE")) {
                                                name += " (3-18 turns)"
                                            }

                                            Text(
                                                text = name,
                                                style = Typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "($displayNum/$displayDen or $prefix$percentage%)",
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
