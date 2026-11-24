package com.example.nethackseer.ui.detail

import androidx.lifecycle.SavedStateHandle
import com.example.nethackseer.data.FakeArtifactDao
import com.example.nethackseer.data.FakeItemDao
import com.example.nethackseer.data.FakeMonsterDao
import com.example.nethackseer.data.FakePropertyDao
import com.example.nethackseer.data.NetHackRepository
import com.example.nethackseer.data.TestEntityFactory
import com.example.nethackseer.data.local.entity.Attack
import com.example.nethackseer.testutils.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var monsterDao: FakeMonsterDao
    private lateinit var itemDao: FakeItemDao
    private lateinit var propertyDao: FakePropertyDao
    private lateinit var artifactDao: FakeArtifactDao
    private lateinit var repository: NetHackRepository

    @Before
    fun setUp() {
        val testMonster = TestEntityFactory.createMonster(
            name = "red dragon",
            symbol = "S_DRAGON",
            color = "CLR_RED",
            level = 15,
            moveRate = 9,
            ac = -1,
            mr = 20,
            alignment = -4,
            resistances = "MR_FIRE|MR_SLEEP",
            resistancesConferred = "MR_FIRE",
            m1Flags = "M1_FLY|M1_SEE_INVIS",
            m2Flags = "M2_HOSTILE|M2_NASTY",
            m3Flags = "0",
            attacks = listOf(
                Attack("AT_BREA", "AD_FIRE", 6, 6),
                Attack("AT_BITE", "AD_PHYS", 3, 8),
                Attack("AT_CLAW", "AD_PHYS", 1, 4)
            )
        )

        val testItem = TestEntityFactory.createItem(
            name = "long sword",
            symbol = "WEAPON_CLASS",
            color = "HI_METAL",
            description = "A versatile bladed weapon.",
            material = "iron",
            subCategory = "P_BROAD_SWORD",
            weight = 40,
            value = 15,
            smallDamage = 8,
            largeDamage = 12,
            property = "SEARCHING",
            magicItem = false,
            charge = false
        )

        val testArtifact = TestEntityFactory.createArtifact(
            name = "Excalibur",
            baseItem = "long sword",
            alignment = "Lawful",
            role = "Knight",
            wieldedProperties = "MR2_SEARCHING|MR_DRAIN",
            carriedProperties = "",
            attackToHitBonus = "1d5",
            attackModifier = "1d10",
            attackTypeModifier = "AD_PHYS",
            invokedPower = null,
            specialEffects = "Manual search bonus|Demon hostility"
        )

        val testProperty = TestEntityFactory.createProperty(
            id = "FIRE_RES",
            name = "Fire resistance",
            description = "Prevents damage from fire and heat attacks.",
            summary = "Immunity to fire damage",
            type = "intrinsic"
        )

        val attackTypeProp = TestEntityFactory.createProperty(
            id = "AT_BREA",
            name = "Breath",
            description = "Breathes an elemental blast.",
            summary = "Breath attack",
            type = "attack"
        )

        val damageTypeProp = TestEntityFactory.createProperty(
            id = "AD_FIRE",
            name = "Fire damage",
            description = "Inflicts fire damage.",
            summary = "Fire damage",
            type = "damage"
        )

        monsterDao = FakeMonsterDao(listOf(testMonster))
        itemDao = FakeItemDao(listOf(testItem))
        propertyDao = FakePropertyDao(listOf(testProperty, attackTypeProp, damageTypeProp))
        artifactDao = FakeArtifactDao(listOf(testArtifact))

        repository = NetHackRepository(monsterDao, itemDao, propertyDao, artifactDao)
    }

    // Emits MonsterSuccess with complete stats, resistances, attacks, and conferred intrinsics
    @Test
    fun loadEntity_monster_emitsMonsterSuccessWithCompleteDetails() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("entityId" to "red dragon"))
        val viewModel = DetailViewModel(savedStateHandle, repository)

        val state = viewModel.uiState.first { it !is EntityUiState.Loading }
        assertTrue("Expected MonsterSuccess but got $state", state is EntityUiState.MonsterSuccess)

        val details = (state as EntityUiState.MonsterSuccess).monsterDetails
        assertEquals("red dragon", details.name)
        assertEquals("S_DRAGON", details.symbol)
        assertEquals("CLR_RED", details.color)
        assertEquals(6, details.speedSlow)
        assertEquals(9, details.speedBase)
        assertEquals(12, details.speedFast)
        assertEquals(-1, details.ac)
        assertEquals(20, details.mr)
        assertEquals("15", details.levelText)
        assertEquals("-4 (Chaotic)", details.alignmentText)
        assertTrue(details.resistances.contains("Fire"))
        assertTrue(details.resistances.contains("Sleep"))
        assertEquals(3, details.attacks.size)
        assertTrue(details.attacks[0].contains("Breath 6d6 Fire damage"))
        assertTrue(details.conferredIntrinsics.any { it.name.contains("Fire") })
    }

    // Emits ItemSuccess with formatted properties, damage values, and category subtitle
    @Test
    fun loadEntity_item_emitsItemSuccessWithFormattedProperties() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("entityId" to "long sword"))
        val viewModel = DetailViewModel(savedStateHandle, repository)

        val state = viewModel.uiState.first { it !is EntityUiState.Loading }
        assertTrue("Expected ItemSuccess but got $state", state is EntityUiState.ItemSuccess)

        val details = (state as EntityUiState.ItemSuccess).itemDetails
        assertEquals("long sword", details.name)
        assertEquals("WEAPON_CLASS", details.symbol)
        assertEquals("HI_METAL", details.color)
        assertEquals("Iron", details.material)
        assertEquals(40, details.weight)
        assertEquals(15, details.value)
        assertEquals("1d8", details.damageSmall)
        assertEquals("1d12", details.damageLarge)
        assertTrue(details.headerSubtitle.contains("Weapon"))
        assertTrue(details.headerSubtitle.contains("Skill: Broad sword"))
        assertTrue(details.conferredProperties.contains("Searching"))
    }

    // Emits ArtifactSuccess resolving base item symbol, color, and wielded properties
    @Test
    fun loadEntity_artifact_emitsArtifactSuccessWithBaseItemSymbol() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("entityId" to "Excalibur"))
        val viewModel = DetailViewModel(savedStateHandle, repository)

        val state = viewModel.uiState.first { it !is EntityUiState.Loading }
        assertTrue("Expected ArtifactSuccess but got $state", state is EntityUiState.ArtifactSuccess)

        val details = (state as EntityUiState.ArtifactSuccess).artifactDetails
        assertEquals("Excalibur", details.name)
        assertEquals("long sword", details.baseItem)
        assertEquals("Lawful", details.alignment)
        assertEquals("Knight", details.role)
        assertEquals("WEAPON_CLASS", details.symbol) // inherited from long sword
        assertEquals("HI_METAL", details.color)     // inherited from long sword
        assertEquals("1d5", details.attackToHitBonus)
        assertEquals("1d10", details.attackModifier)
        assertEquals("Physical", details.attackTypeModifier)
        assertTrue(details.wieldedProperties.contains("Searching"))
        assertTrue(details.wieldedProperties.contains("Drain resistance"))
        assertEquals(2, details.specialEffects.size)
        assertEquals("Manual search bonus", details.specialEffects[0])
    }

    // Emits PropertySuccess with property description and summary
    @Test
    fun loadEntity_property_emitsPropertySuccess() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("entityId" to "Fire resistance"))
        val viewModel = DetailViewModel(savedStateHandle, repository)

        val state = viewModel.uiState.first { it !is EntityUiState.Loading }
        assertTrue("Expected PropertySuccess but got $state", state is EntityUiState.PropertySuccess)

        val details = (state as EntityUiState.PropertySuccess).propertyDetails
        assertEquals("Fire resistance", details.name)
        assertEquals("Prevents damage from fire and heat attacks.", details.description)
        assertEquals("Immunity to fire damage", details.summary)
        assertEquals("intrinsic", details.type)
    }

    // Calculates conferred intrinsics and giant percentage chance for giant monsters
    @Test
    fun loadEntity_monster_calculatesGiantConferredIntrinsics() = runTest {
        val giantMonster = TestEntityFactory.createMonster(
            name = "frost giant",
            symbol = "S_GIANT",
            color = "CLR_WHITE",
            level = 10,
            m2Flags = "M2_GIANT|M2_HOSTILE",
            resistancesConferred = "MR_COLD"
        )
        monsterDao.insertAll(listOf(giantMonster))

        val savedStateHandle = SavedStateHandle(mapOf("entityId" to "frost giant"))
        val viewModel = DetailViewModel(savedStateHandle, repository)

        val state = viewModel.uiState.first { it !is EntityUiState.Loading }
        assertTrue("Expected MonsterSuccess but got $state", state is EntityUiState.MonsterSuccess)

        val details = (state as EntityUiState.MonsterSuccess).monsterDetails
        assertTrue(details.isGiant)
        assertTrue("Giant chance text should not be empty", details.giantChanceText.isNotEmpty())
        assertTrue(details.conferredIntrinsics.any { it.name.contains("Cold") })
    }

    // Emits ItemSuccess mapping armor slot, magic cancellation, and special capabilities
    @Test
    fun loadEntity_item_armorCapabilitiesAndSlot() = runTest {
        val shield = TestEntityFactory.createItem(
            name = "small shield",
            symbol = "ARMOR_CLASS",
            subCategory = "ARM_SHIELD",
            magicCancellation = 2,
            magicItem = true,
            charge = true,
            merge = false,
            tough = true,
            notWish = true,
            unique = true,
            ac = 1,
            delay = 2
        )
        itemDao.insertAll(listOf(shield))

        val savedStateHandle = SavedStateHandle(mapOf("entityId" to "small shield"))
        val viewModel = DetailViewModel(savedStateHandle, repository)

        val state = viewModel.uiState.first { it !is EntityUiState.Loading }
        assertTrue("Expected ItemSuccess but got $state", state is EntityUiState.ItemSuccess)

        val details = (state as EntityUiState.ItemSuccess).itemDetails
        assertEquals("Equip Delay", details.delayLabel)
        assertEquals(2, details.mc)
        assertTrue(details.headerSubtitle.contains("Slot: Shield"))
        assertTrue(details.capabilities.contains("Magical item"))
        assertTrue(details.capabilities.contains("Chargeable / Enchantable"))
        assertTrue(details.capabilities.contains("Immune to destruction"))
        assertTrue(details.capabilities.contains("Cannot be wished for"))
        assertTrue(details.capabilities.contains("Unique"))
    }

    // Emits ArtifactSuccess mapping invoked powers, carried properties, and special effects
    @Test
    fun loadEntity_artifact_invokedPowerAndCarriedProperties() = runTest {
        val orb = TestEntityFactory.createArtifact(
            name = "The Orb of Fate",
            baseItem = "crystal ball",
            alignment = "Neutral",
            role = "Valkyrie",
            wieldedProperties = "",
            carriedProperties = "MR2_WARNING|MR2_HALF_SPDAM|MR2_HALF_PHDAM",
            invokedPower = "LEV_TELE",
            specialEffects = "Functions as a luckstone while carried."
        )
        val crystalBall = TestEntityFactory.createItem(
            name = "crystal ball",
            symbol = "TOOL_CLASS",
            color = "CLR_WHITE"
        )
        artifactDao.insertAll(listOf(orb))
        itemDao.insertAll(listOf(crystalBall))

        val savedStateHandle = SavedStateHandle(mapOf("entityId" to "The Orb of Fate"))
        val viewModel = DetailViewModel(savedStateHandle, repository)

        val state = viewModel.uiState.first { it !is EntityUiState.Loading }
        assertTrue("Expected ArtifactSuccess but got $state", state is EntityUiState.ArtifactSuccess)

        val details = (state as EntityUiState.ArtifactSuccess).artifactDetails
        assertEquals("Levitate / Teleport", details.invokedPower)
        assertEquals("TOOL_CLASS", details.symbol)
        assertEquals("CLR_WHITE", details.color)
        assertTrue(details.carriedProperties.contains("Warning"))
        assertTrue(details.carriedProperties.contains("Half spell damage"))
        assertTrue(details.carriedProperties.contains("Half physical damage"))
        assertEquals("Functions as a luckstone while carried.", details.specialEffects.first())
    }

    // Emits Error state when entity ID is not found in any repository table
    @Test
    fun loadEntity_unknownEntity_emitsNotFoundError() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("entityId" to "NonExistentThing"))
        val viewModel = DetailViewModel(savedStateHandle, repository)

        val state = viewModel.uiState.first { it !is EntityUiState.Loading }
        assertTrue("Expected Error state but got $state", state is EntityUiState.Error)
        assertEquals("Entity 'NonExistentThing' not found", (state as EntityUiState.Error).message)
    }

    // Emits Error state when entity ID is omitted from SavedStateHandle
    @Test
    fun loadEntity_missingEntityId_emitsMissingIdError() = runTest {
        val savedStateHandle = SavedStateHandle() // entityId defaults to "Unknown"
        val viewModel = DetailViewModel(savedStateHandle, repository)

        val state = viewModel.uiState.first { it !is EntityUiState.Loading }
        assertTrue("Expected Error state but got $state", state is EntityUiState.Error)
        assertEquals("Entity ID missing", (state as EntityUiState.Error).message)
    }
}
