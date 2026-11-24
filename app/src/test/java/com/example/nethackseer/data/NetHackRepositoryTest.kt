package com.example.nethackseer.data

import com.example.nethackseer.data.local.dao.ArtifactDao
import com.example.nethackseer.data.local.dao.ItemDao
import com.example.nethackseer.data.local.dao.MonsterDao
import com.example.nethackseer.data.local.dao.PropertyDao
import com.example.nethackseer.data.local.entity.ArtifactEntity
import com.example.nethackseer.data.local.entity.Attack
import com.example.nethackseer.data.local.entity.ItemEntity
import com.example.nethackseer.data.local.entity.MonsterEntity
import com.example.nethackseer.data.local.entity.PropertyEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NetHackRepositoryTest {

    private lateinit var monsterDao: FakeMonsterDao
    private lateinit var itemDao: FakeItemDao
    private lateinit var propertyDao: FakePropertyDao
    private lateinit var artifactDao: FakeArtifactDao
    private lateinit var repository: NetHackRepository

    private fun createTestAttack() = Attack(
        type = "AT_BITE",
        damageType = "AD_PHYS",
        diceCount = 1,
        diceSides = 4
    )

    private fun createTestMonster(
        name: String,
        symbol: String,
        color: String = "CLR_BROWN",
        maleName: String? = null,
        femaleName: String? = null
    ) = MonsterEntity(
        name = name,
        symbol = symbol,
        level = 2,
        moveRate = 12,
        ac = 3,
        mr = 0,
        alignment = 0,
        genoFlags = "0",
        attack1 = createTestAttack(),
        attack2 = createTestAttack(),
        attack3 = createTestAttack(),
        attack4 = createTestAttack(),
        attack5 = createTestAttack(),
        attack6 = createTestAttack(),
        weight = 10,
        nutritionValue = 10,
        sound = "MS_SILENT",
        size = "MZ_MEDIUM",
        resistances = "0",
        resistancesConferred = "0",
        m1Flags = "M1_ANIMAL",
        m2Flags = "M2_HOSTILE",
        m3Flags = "0",
        difficulty = 4,
        color = color,
        maleName = maleName,
        femaleName = femaleName
    )

    private fun createTestItem(
        name: String,
        symbol: String,
        color: String = "HI_METAL"
    ) = ItemEntity(
        name = name,
        description = "description",
        merge = false,
        magicItem = false,
        charge = false,
        unique = false,
        notWish = false,
        tough = false,
        damageType = "AD_PHYS",
        zapDirection = "NODIR",
        subCategory = "sub",
        material = "iron",
        property = "0",
        symbol = symbol,
        probability = 100,
        delay = 0,
        weight = 40,
        value = 15,
        smallDamage = 8,
        largeDamage = 12,
        ac = 0,
        hitBonus = 0,
        magicCancellation = 0,
        spellLevel = 0,
        nutrition = 0,
        color = color
    )

    @Before
    fun setUp() {
        val testMonsters = listOf(
            createTestMonster(
                name = "giant ant",
                symbol = "S_ANT",
                color = "CLR_BROWN"
            ),
            createTestMonster(
                name = "gnome ruler",
                symbol = "S_GNOME",
                color = "CLR_MAGENTA",
                maleName = "gnome king",
                femaleName = "gnome queen"
            )
        )

        val testItems = listOf(
            createTestItem(
                name = "long sword",
                symbol = "WEAPON_CLASS",
                color = "HI_METAL"
            ),
            createTestItem(
                name = "crystal ball",
                symbol = "TOOL_CLASS",
                color = "CLR_WHITE"
            )
        )

        val testArtifacts = listOf(
            ArtifactEntity(
                name = "Excalibur",
                baseItem = "long sword",
                alignment = "Lawful",
                role = "Knight",
                wieldedProperties = "MR2_SEARCHING|MR_DRAIN",
                carriedProperties = "",
                attackToHitBonus = "1d5",
                attackModifier = "1d10",
                attackTypeModifier = "AD_PHYS",
                damageTarget = null,
                invokedPower = null,
                specialEffects = "Manual search bonus|Demon hostility"
            ),
            ArtifactEntity(
                name = "The Orb of Fate",
                baseItem = "crystal ball",
                alignment = "Neutral",
                role = "Valkyrie",
                wieldedProperties = "",
                carriedProperties = "MR2_WARNING|MR2_HALF_SPDAM|MR2_HALF_PHDAM",
                attackToHitBonus = null,
                attackModifier = null,
                attackTypeModifier = null,
                damageTarget = null,
                invokedPower = "LEV_TELE",
                specialEffects = "Functions as a luckstone while carried."
            )
        )

        val testProperties = listOf(
            PropertyEntity(
                id = "FIRE_RES",
                name = "Fire resistance",
                description = "Grants immunity to fire damage.",
                summary = "Immunity to fire",
                type = "intrinsic"
            )
        )

        monsterDao = FakeMonsterDao(testMonsters)
        itemDao = FakeItemDao(testItems)
        propertyDao = FakePropertyDao(testProperties)
        artifactDao = FakeArtifactDao(testArtifacts)

        repository = NetHackRepository(monsterDao, itemDao, propertyDao, artifactDao)
    }

    // Empty or whitespace query returns empty list
    @Test
    fun search_emptyOrWhitespaceQuery_returnsEmptyList() = runBlocking {
        val emptyResults = repository.searchMonstersItems("").first()
        assertTrue("Empty query should return empty list", emptyResults.isEmpty())

        val whitespaceResults = repository.searchMonstersItems("   ").first()
        assertTrue("Whitespace query should return empty list", whitespaceResults.isEmpty())
    }

    // Search matches monster name
    @Test
    fun search_byMonsterName_returnsMatchingMonster() = runBlocking {
        val results = repository.searchMonstersItems("giant ant").first()
        assertEquals(1, results.size)
        val result = results[0]
        assertEquals("giant ant", result.name)
        assertEquals("monster", result.category)
        assertEquals("S_ANT", result.symbol)
        assertEquals("CLR_BROWN", result.color)
    }

    // Search matches gendered names (maleName and femaleName)
    @Test
    fun search_byGenderedName_returnsParentMonster() = runBlocking {
        val femaleResults = repository.searchMonstersItems("gnome queen").first()
        assertEquals(1, femaleResults.size)
        assertEquals("gnome ruler", femaleResults[0].name)
        assertEquals("monster", femaleResults[0].category)

        val maleResults = repository.searchMonstersItems("gnome king").first()
        assertEquals(1, maleResults.size)
        assertEquals("gnome ruler", maleResults[0].name)
    }

    // Search matches glyph display character
    @Test
    fun search_byMonsterGlyph_returnsMonstersWithSymbol() = runBlocking {
        // 'a' corresponds to S_ANT
        val results = repository.searchMonstersItems("a").first()
        assertTrue("Should return giant ant for glyph 'a'", results.any { it.name == "giant ant" })
    }

    // Search matches monster class display name
    @Test
    fun search_byMonsterClassDisplayName_returnsMatchingMonsters() = runBlocking {
        // S_ANT display name is "ant / insect"
        val results = repository.searchMonstersItems("insect").first()
        assertTrue("Should return giant ant when searching 'insect'", results.any { it.name == "giant ant" })
    }

    // Search matches item name
    @Test
    fun search_byItemName_returnsMatchingItem() = runBlocking {
        val results = repository.searchMonstersItems("long sword").first()
        val item = results.find { it.category == "item" }
        assertNotNull("Should find long sword item", item)
        assertEquals("long sword", item?.name)
        assertEquals("WEAPON_CLASS", item?.symbol)
    }

    // Search matches item glyph
    @Test
    fun search_byItemGlyph_returnsItemsWithClass() = runBlocking {
        // ')' corresponds to WEAPON_CLASS
        val results = repository.searchMonstersItems(")").first()
        assertTrue("Should return long sword for glyph ')'",
            results.any { it.name == "long sword" && it.category == "item" })
    }

    // Search matches artifact name and resolves base item symbol and color
    @Test
    fun search_byArtifactName_resolvesBaseItemMetadata() = runBlocking {
        val results = repository.searchMonstersItems("Excalibur").first()
        assertEquals(1, results.size)
        val artifact = results[0]
        assertEquals("Excalibur", artifact.name)
        assertEquals("artifact", artifact.category)
        assertEquals("WEAPON_CLASS", artifact.symbol)
        assertEquals("HI_METAL", artifact.color)
    }

    // Search matches artifact by its base item name
    @Test
    fun search_byArtifactBaseItem_returnsAssociatedArtifact() = runBlocking {
        val results = repository.searchMonstersItems("crystal ball").first()
        val artifact = results.find { it.name == "The Orb of Fate" }
        assertNotNull("Searching 'crystal ball' should return The Orb of Fate", artifact)
        assertEquals("artifact", artifact?.category)
        assertEquals("TOOL_CLASS", artifact?.symbol)
        assertEquals("CLR_WHITE", artifact?.color)
    }

    // Search is case-insensitive
    @Test
    fun search_isCaseInsensitive() = runBlocking {
        val upperResults = repository.searchMonstersItems("EXCALIBUR").first()
        val lowerResults = repository.searchMonstersItems("excalibur").first()
        val mixedResults = repository.searchMonstersItems("ExCaLiBuR").first()

        assertEquals(upperResults.size, lowerResults.size)
        assertEquals(lowerResults.size, mixedResults.size)
        assertEquals("Excalibur", upperResults[0].name)
    }

    // Combined results are sorted alphabetically by name
    @Test
    fun search_combinedResults_sortedAlphabetically() = runBlocking {
        // Broad query that matches multiple categories
        val results = repository.searchMonstersItems("o").first()
        assertTrue("Should return multiple results", results.size > 1)
        for (i in 0 until results.size - 1) {
            assertTrue(
                "Results must be sorted: '${results[i].name}' <= '${results[i + 1].name}'",
                results[i].name <= results[i + 1].name
            )
        }
    }

    // allNames combines names from all 4 DAOs in sorted order
    @Test
    fun allNames_combinesAllFourDaosAlphabetically() = runBlocking {
        val allNames = repository.allNames.first()
        // 2 monsters + 2 items + 2 artifacts + 1 property = 7 total names
        assertEquals(7, allNames.size)
        assertTrue(allNames.contains("Excalibur"))
        assertTrue(allNames.contains("The Orb of Fate"))
        assertTrue(allNames.contains("giant ant"))
        assertTrue(allNames.contains("gnome ruler"))
        assertTrue(allNames.contains("long sword"))
        assertTrue(allNames.contains("crystal ball"))
        assertTrue(allNames.contains("Fire resistance"))

        for (i in 0 until allNames.size - 1) {
            assertTrue("allNames must be sorted: '${allNames[i]}' <= '${allNames[i + 1]}'", allNames[i] <= allNames[i + 1])
        }
    }

    // Direct entity getters return expected entity
    @Test
    fun directGetters_returnCorrectEntities() = runBlocking {
        val monster = repository.getMonsterByName("giant ant").first()
        assertNotNull(monster)
        assertEquals("giant ant", monster?.name)

        val item = repository.getItemByName("long sword").first()
        assertNotNull(item)
        assertEquals("long sword", item?.name)

        val artifact = repository.getArtifactByName("Excalibur").first()
        assertNotNull(artifact)
        assertEquals("Excalibur", artifact?.name)

        val property = repository.getPropertyByName("Fire resistance").first()
        assertNotNull(property)
        assertEquals("Fire resistance", property?.name)
    }
}
