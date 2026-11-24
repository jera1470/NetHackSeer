package com.example.nethackseer.ui.utils

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NetHackUtilsTest {

    // cleanNetHackName replaces color and material prefixes with lowercase words and replaces underscores
    @Test
    fun cleanNetHackName_replacesPrefixesAndUnderscores() {
        assertEquals("giant ant", cleanNetHackName("giant_ant"))
        assertEquals("red dragon", cleanNetHackName("CLR_RED dragon"))
        assertEquals("metal sword", cleanNetHackName("HI_METAL_sword"))
        assertEquals("silver saber", cleanNetHackName("HI_SILVER_saber"))
        assertEquals("killer bee", cleanNetHackName("killer_bee"))
    }

    // getDisplayChar maps monster and item symbol constants to NetHack ASCII glyphs
    @Test
    fun getDisplayChar_mapsSymbolsToGlyphs() {
        // Monster symbols
        assertEquals("a", getDisplayChar("S_ANT"))
        assertEquals("b", getDisplayChar("S_BLOB"))
        assertEquals("d", getDisplayChar("S_DOG"))
        assertEquals("D", getDisplayChar("S_DRAGON"))
        assertEquals("&", getDisplayChar("S_DEMON"))
        assertEquals(":", getDisplayChar("S_LIZARD"))

        // Item symbols
        assertEquals(")", getDisplayChar("WEAPON_CLASS"))
        assertEquals("[", getDisplayChar("ARMOR_CLASS"))
        assertEquals("=", getDisplayChar("RING_CLASS"))
        assertEquals("\"", getDisplayChar("AMULET_CLASS"))
        assertEquals("(", getDisplayChar("TOOL_CLASS"))
        assertEquals("%", getDisplayChar("FOOD_CLASS"))
        assertEquals("!", getDisplayChar("POTION_CLASS"))
        assertEquals("?", getDisplayChar("SCROLL_CLASS"))
        assertEquals("+", getDisplayChar("SPBOOK_CLASS"))
        assertEquals("/", getDisplayChar("WAND_CLASS"))
        assertEquals("$", getDisplayChar("COIN_CLASS"))
        assertEquals("*", getDisplayChar("GEM_CLASS"))

        // Unknown fallback
        assertEquals("?", getDisplayChar("UNKNOWN_CLASS"))
    }

    // getSymbolDisplayName returns human-readable group names
    @Test
    fun getSymbolDisplayName_returnsHumanReadableNames() {
        assertEquals("weapon", getSymbolDisplayName("WEAPON_CLASS"))
        assertEquals("armor", getSymbolDisplayName("ARMOR_CLASS"))
        assertEquals("potion", getSymbolDisplayName("POTION_CLASS"))
        assertEquals("ant / insect", getSymbolDisplayName("S_ANT"))
        assertEquals("canine", getSymbolDisplayName("S_DOG"))
        assertEquals("dragon", getSymbolDisplayName("S_DRAGON"))
    }

    // getNetHackColor returns valid Compose Color instances
    @Test
    fun getNetHackColor_returnsValidColors() {
        val red = getNetHackColor("CLR_RED")
        assertNotNull(red)
        assertEquals(Color(0xFFAA0000), red)

        val white = getNetHackColor("CLR_WHITE")
        assertEquals(Color(0xFFFFFFFF), white)

        val metal = getNetHackColor("HI_METAL")
        assertEquals(Color(0xFF00AAAA), metal)

        val defaultGray = getNetHackColor("NON_EXISTENT_COLOR")
        assertEquals(Color.Gray, defaultGray)
    }

    // formatItemProperty converts property constants to readable text
    @Test
    fun formatItemProperty_formatsStandardProperties() {
        assertEquals("Fire resistance", formatItemProperty("FIRE_RES"))
        assertEquals("Cold resistance", formatItemProperty("COLD_RES"))
        assertEquals("Shock resistance", formatItemProperty("SHOCK_RES"))
        assertEquals("Reflection", formatItemProperty("REFLECTING"))
        assertEquals("Telepathy", formatItemProperty("TELEPAT"))
        assertEquals("Magic cancellation", formatItemProperty("MAGIC_CANCELLATION"))
    }

    // formatArtifactProperty strips MR_/MR2_ prefixes and maps artifact-specific flags
    @Test
    fun formatArtifactProperty_stripsPrefixesAndFormats() {
        assertEquals("Shock resistance", formatArtifactProperty("MR_ELEC"))
        assertEquals("Drain resistance", formatArtifactProperty("MR_DRAIN"))
        assertEquals("Fire resistance", formatArtifactProperty("MR_FIRE"))
        assertEquals("Cold resistance", formatArtifactProperty("MR_COLD"))
        assertEquals("Searching", formatArtifactProperty("MR2_SEARCHING"))
        assertEquals("Warning", formatArtifactProperty("MR2_WARNING"))
        assertEquals("Antimagic", formatArtifactProperty("MR_ANTIMAGIC"))
        assertEquals("Hallucination resistance", formatArtifactProperty("MR2_HALLUC_RES"))
    }
}
