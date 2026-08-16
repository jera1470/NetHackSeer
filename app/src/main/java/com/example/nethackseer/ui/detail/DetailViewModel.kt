package com.example.nethackseer.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.nethackseer.data.NetHackRepository
import com.example.nethackseer.data.local.entity.ItemEntity
import com.example.nethackseer.data.local.entity.MonsterEntity
import com.example.nethackseer.data.local.entity.PropertyEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class IntrinsicChance(
    val name: String,
    val chanceText: String
)

data class MonsterDetails(
    val name: String,
    val symbol: String,
    val color: String,
    val difficulty: Int,
    val levelText: String,
    val ac: Int,
    val mr: Int,
    val speedSlow: Int,
    val speedBase: Int,
    val speedFast: Int,
    val weight: Int,
    val nutrition: Int,
    val size: String,
    val alignmentText: String,
    val baseExp: Int,
    val resistances: List<String>,
    val specialEffects: List<String>,
    val conferredIntrinsics: List<IntrinsicChance>,
    val attacks: List<String>,
    val propertyBulletPoints: List<String>,
    val isMindFlayer: Boolean = false,
    val isGiant: Boolean = false,
    val giantChanceText: String = ""
)

// handles all Success, Error, and Loading states, must be sealed
sealed class EntityUiState {
    object Loading : EntityUiState()
    data class MonsterSuccess(
        val monsterDetails: MonsterDetails
    ) : EntityUiState()
    data class ItemSuccess(val item: ItemEntity) : EntityUiState()
    data class Error(val message: String) : EntityUiState()
}

/**
 * View model for the detail screen.
 */
class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: NetHackRepository, // inject this
) : ViewModel() {

    private val entityId: String = savedStateHandle.get<String>("entityId") ?: "Unknown"

    private val _uiState = MutableStateFlow<EntityUiState>(EntityUiState.Loading)
    val uiState: StateFlow<EntityUiState> = _uiState.asStateFlow()

    init {
        loadEntity()
    }

    private fun loadEntity() {
        if (entityId == "Unknown") {
            _uiState.value = EntityUiState.Error("Entity ID missing")
            return
        }

        viewModelScope.launch {
            // First, try to find a monster
            val monster = repository.getMonsterByName(entityId).first()
            if (monster != null) {
                // Collect all flag IDs
                val flagIds = mutableListOf<String>()
                if (monster.m1Flags != "0") flagIds.addAll(monster.m1Flags.split("|").map { it.trim() })
                if (monster.m2Flags != "0") flagIds.addAll(monster.m2Flags.split("|").map { it.trim() })
                if (monster.m3Flags != "0") flagIds.addAll(monster.m3Flags.split("|").map { it.trim() })
                if (monster.genoFlags != "0") flagIds.addAll(monster.genoFlags.split("|").map { it.trim() })

                // Add attack and damage types to fetch readable names
                val monsterAttacks = listOf(monster.attack1, monster.attack2, monster.attack3, monster.attack4, monster.attack5, monster.attack6)
                monsterAttacks.forEach {
                    if (it.type != "NO_ATTK") {
                        flagIds.add(it.type)
                        if (it.damageType != "AD_NONE") flagIds.add(it.damageType)
                    }
                }
                
                val properties = if (flagIds.isNotEmpty()) {
                    repository.getPropertiesByIds(flagIds).first()
                } else {
                    emptyList()
                }

                _uiState.value = EntityUiState.MonsterSuccess(mapToMonsterDetails(monster, properties))
                return@launch
            }

            // If not a monster, try to find an item
            val item = repository.getItemByName(entityId).first()
            if (item != null) {
                _uiState.value = EntityUiState.ItemSuccess(item)
                return@launch
            }

            // If neither, show error
            _uiState.value = EntityUiState.Error("Entity '$entityId' not found")
        }
    }

    private fun mapToMonsterDetails(monster: MonsterEntity, properties: List<PropertyEntity>): MonsterDetails {
        val mlevel = monster.level
        val levelText = if (mlevel > 49) {
            val hp = 2 * (mlevel - 6)
            "${hp / 4} ($mlevel)"
        } else {
            "$mlevel"
        }

        val mmove = monster.moveRate
        val slow = if (mmove < 12) (2 * mmove + 1) / 3 else 4 + (mmove / 3)
        val fast = (4 * mmove + 2) / 3

        val size = monster.size.removePrefix("MZ_").lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

        val alignmentText = when {
            monster.alignment == -128 -> "Unaligned"
            monster.alignment == 0 -> "0 (Neutral)"
            monster.alignment > 0 -> "${monster.alignment} (Lawful)"
            else -> "${monster.alignment} (Chaotic)"
        }

        val baseExp = calculateExperience(monster)

        val resistancesList = if (monster.resistances == "0") {
            listOf("None")
        } else {
            monster.resistances.split("|").map {
                when (val id = it.trim()) {
                    "MR_ELEC" -> "Shock"
                    "MR_DISINT" -> "Disintegrate"
                    else -> id.removePrefix("MR_").lowercase()
                        .replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() }
                }
            }
        }

        // Conferred intrinsics and special effects logic
        val conferredRaw = monster.resistancesConferred
        val baseConferred = if (conferredRaw == "0") {
            mutableListOf()
        } else {
            conferredRaw.split("|").map { it.trim() }.toMutableList()
        }
        
        if (monster.m1Flags.contains("M1_TPORT")) {
            if (!baseConferred.contains("MR_TELEPORT")) baseConferred.add("MR_TELEPORT")
        }
        if (monster.m1Flags.contains("M1_TPORT_CNTRL")) {
            if (!baseConferred.contains("MR_TELEPORT_CONTROL")) baseConferred.add("MR_TELEPORT_CONTROL")
        }

        val specialEffects = mutableListOf<String>()
        val lowerName = monster.name.lowercase()
        if (lowerName == "wraith") specialEffects.add("Gain level")
        if (lowerName.contains("were")) specialEffects.add("Contract lycanthropy")
        when (lowerName) {
            "small mimic" -> specialEffects.add("Mimic an object (20 turns)")
            "large mimic" -> specialEffects.add("Mimic an object (40 turns)")
            "giant mimic" -> specialEffects.add("Mimic an object (50 turns)")
        }
        if (listOf("chameleon", "doppelganger", "genetic engineer").any { lowerName == it }) specialEffects.add("Polymorph")
        if (lowerName == "nurse") specialEffects.add("Full heal/cure blindness")
        if (lowerName == "lizard") specialEffects.add("Reduce stun/confusion")
        if (lowerName == "stalker") specialEffects.add("Temp. Invis. (+50-149 turns)\nPerm. Invis./See Invis if already invis.")
        if (lowerName == "displacer beast") specialEffects.add("Temp. displacement (+6-36 turns)")
        if (listOf("yellow light", "bat", "giant bat").any { lowerName == it }) specialEffects.add("Stun (+30 turns)")
        if (lowerName == "quantum mechanic") specialEffects.add("Toggle speed")
        if (lowerName == "disenchanter") specialEffects.add("Lose a random intrinsic")
        
        val monsterAttacks = listOf(monster.attack1, monster.attack2, monster.attack3, monster.attack4, monster.attack5, monster.attack6)
        val hasHaluAttack = monsterAttacks.any { it.damageType == "AD_HALU" || it.damageType == "AD_STUN" }
        if (lowerName == "violet fungus" || hasHaluAttack) specialEffects.add("Hallucination (+200 turns)")
        val isMagical = monsterAttacks.any { it.type == "AT_MAGC" }
        if (lowerName == "newt" || isMagical) specialEffects.add("Increase energy")
        if (listOf("death", "pestilence", "famine").any { lowerName == it }) specialEffects.add("Death when eaten")

        val isGiant = monster.symbol == "S_GIANT"
        val isMindFlayer = lowerName.contains("mind flayer")
        
        val poolMultiplier = if (isMindFlayer) 2L else 1L
        val poolSize = baseConferred.size + (if (isGiant) 1 else 0)
        val giantFailMultiplier = if (isGiant && baseConferred.isEmpty()) 2L else 1L
        val finalDenomBase = poolSize.toLong() * poolMultiplier * giantFailMultiplier

        var giantChanceText = ""
        if (isGiant) {
            val common = getGcd(1, finalDenomBase)
            val dNum = 1 / common
            val dDen = finalDenomBase / common
            val perc = ((dNum.toDouble() / dDen.toDouble()) * 100.0).toInt()
            giantChanceText = "($dNum/$dDen or $perc%)"
        }

        val conferredIntrinsics = baseConferred.map { id ->
            val isBeeOrScorpion = lowerName.contains("killer bee") || lowerName.contains("scorpion")
            val (num, den) = when {
                id.contains("TELEPATHY", ignoreCase = true) -> 1L to 1L
                id.contains("TELEPORT", ignoreCase = true) && !id.contains(
                    "CONTROL",
                    ignoreCase = true
                ) ->
                    monster.level.toLong().coerceAtMost(10L) to 10L

                id.contains("TELEPORT", ignoreCase = true) && id.contains(
                    "CONTROL",
                    ignoreCase = true
                ) ->
                    monster.level.toLong().coerceAtMost(12L) to 12L

                id.contains("POISON", ignoreCase = true) && isBeeOrScorpion ->
                    (monster.level.toLong() + 5).coerceAtMost(20L) to 20L

                id.contains("ACID", ignoreCase = true) -> {
                    val lvl = monster.level.toLong()
                    if (lvl >= 3) 1L to 1L
                    else (18 * lvl - lvl * lvl) to 45L
                }

                id.contains("STONE", ignoreCase = true) -> {
                    val lvl = monster.level.toLong()
                    if (lvl >= 6) 1L to 1L
                    else (21 * lvl - lvl * lvl) to 90L
                }

                else -> monster.level.toLong().coerceAtMost(15L) to 15L
            }

            val finalDen = finalDenomBase * den
            val common = getGcd(num, finalDen)
            val displayNum = num / common
            val displayDen = finalDen / common
            val totalChanceExact = (displayNum.toDouble() / displayDen.toDouble()) * 100.0
            val percentage = totalChanceExact.toInt().coerceIn(0, 100)
            val prefix =
                if (totalChanceExact > percentage.toDouble() && percentage < 100) "~" else ""

            var name = when (id) {
                "MR_ELEC" -> "Shock"
                "MR_DISINT" -> "Disintegrate"
                else -> id.removePrefix("MR_").lowercase().replace("_", " ")
                    .replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() }
            }
            if (id.contains("ACID") || id.contains("STONE")) {
                name += " (3-18 turns)"
            }
            IntrinsicChance(name, "($displayNum/$displayDen or $prefix$percentage%)")
        }

        val attacksFormatted = monsterAttacks.filter { it.type != "NO_ATTK" }.map { attack ->
            val typeName = properties.find { it.id == attack.type }?.name
                ?: attack.type.removePrefix("AT_").lowercase().replace("_", " ")
            val damageName = if (attack.damageType == "AD_NONE") {
                ""
            } else {
                properties.find { it.id == attack.damageType }?.name
                    ?: attack.damageType.removePrefix("AD_").lowercase().replace("_", " ")
            }
            val diceString = if (attack.diceCount == 0 && attack.diceSides == 0) {
                ""
            } else {
                "${attack.diceCount}d${attack.diceSides} "
            }
            "• ${typeName.replaceFirstChar { it.uppercase() }} $diceString$damageName".trimEnd()
        }

        val propertyBulletPoints = mutableListOf<String>()
        val m1Properties = properties.filter { it.id.startsWith("M1_") }
        val otherProperties = properties.filter { 
            it.id.startsWith("M2_") || 
            it.id.startsWith("M3_") || 
            it.id.startsWith("G_") 
        }
        val isInediate = !monster.m1Flags.contains("M1_CARNIVORE") && 
                        !monster.m1Flags.contains("M1_HERBIVORE") && 
                        !monster.m1Flags.contains("M1_OMNIVORE")
        
        m1Properties.forEach { prop -> propertyBulletPoints.add(prop.summary.ifEmpty { prop.description }) }
        if (isInediate) propertyBulletPoints.add("does not need to eat.")
        otherProperties.forEach { prop -> propertyBulletPoints.add(prop.summary.ifEmpty { prop.description }) }

        return MonsterDetails(
            name = monster.name,
            symbol = monster.symbol,
            color = monster.color,
            difficulty = monster.difficulty,
            levelText = levelText,
            ac = monster.ac,
            mr = monster.mr,
            speedSlow = slow,
            speedBase = mmove,
            speedFast = fast,
            weight = monster.weight,
            nutrition = monster.nutritionValue,
            size = size,
            alignmentText = alignmentText,
            baseExp = baseExp,
            resistances = resistancesList,
            specialEffects = specialEffects,
            conferredIntrinsics = conferredIntrinsics,
            attacks = attacksFormatted,
            propertyBulletPoints = propertyBulletPoints,
            isMindFlayer = isMindFlayer,
            isGiant = isGiant,
            giantChanceText = giantChanceText
        )
    }

    private fun getGcd(a: Long, b: Long): Long = if (b == 0L) a else getGcd(b, a % b)

    private fun calculateExperience(monster: MonsterEntity): Int {
        if (monster.name.equals("mail daemon", ignoreCase = true)) return 1

        var level = monster.level
        if (level > 49) {
            val hp = 2 * (level - 6)
            level = hp / 4
        }

        var tmp = 1 + level * level

        val ac = monster.ac
        if (ac < 3) {
            tmp += (7 - ac) * (if (ac < 0) 2 else 1)
        }

        if (monster.moveRate > 12) {
            tmp += if (monster.moveRate > 18) 5 else 3
        }

        val attacks = listOf(
            monster.attack1, monster.attack2, monster.attack3,
            monster.attack4, monster.attack5, monster.attack6
        )

        val lowAttackTypes = setOf("NO_ATTK", "AT_ANY", "AT_NONE", "AT_CLAW", "AT_BITE", "AT_KICK", "AT_BUTT")
        attacks.forEach { attack ->
            if (attack.type != "NO_ATTK" && !lowAttackTypes.contains(attack.type)) {
                tmp += when (attack.type) {
                    "AT_WEAP" -> 5
                    "AT_MAGC" -> 10
                    else -> 3
                }
            }
        }

        val elementalDamageTypes = setOf(
            "AD_MAGM", "AD_FIRE", "AD_COLD", "AD_SLEE",
            "AD_DISN", "AD_ELEC", "AD_DRST", "AD_ACID", "AD_SPC1"
        )
        attacks.forEach { attack ->
            val adtyp = attack.damageType
            if (elementalDamageTypes.contains(adtyp)) {
                tmp += 2 * level
            } else if (adtyp == "AD_DRLI" || adtyp == "AD_STON" || adtyp == "AD_SLIM") {
                tmp += 50
            } else if (adtyp != "AD_PHYS" && adtyp != "AD_NONE") {
                tmp += level
            }

            if (attack.diceCount * attack.diceSides > 23) {
                tmp += level
            }

            if (adtyp == "AD_WRAP" && monster.symbol == "S_EEL") {
                tmp += 1000
            }
        }

        if (monster.m2Flags.contains("M2_NASTY") ||
            monster.m2Flags.contains("M2_LORD") ||
            monster.m2Flags.contains("M2_PRINCE") ||
            monster.symbol == "S_DEMON") {
            tmp += 7 * level
        }

        if (level > 8) {
            tmp += 50
        }

        return tmp
    }

    // Factory for creating the viewmodel with repository
    companion object {
        fun Factory(repository: NetHackRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    val savedStateHandle = extras.createSavedStateHandle()
                    return DetailViewModel(savedStateHandle, repository) as T
                }
            }
    }
}
