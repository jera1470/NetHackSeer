package com.example.nethackseer

import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

// Runs locally as a unit test
class AssetJsonValidationTest {

    private fun getAssetFile(relativePath: String): File {
        val candidate1 = File("src/main/assets/$relativePath")
        if (candidate1.exists()) return candidate1

        val candidate2 = File("app/src/main/assets/$relativePath")
        if (candidate2.exists()) return candidate2

        throw AssertionError("Asset file not found at src/main/assets/$relativePath or app/src/main/assets/$relativePath")
    }

    // Check if monsters500.json exists, is a valid array, and has valid monster fields
    @Test
    fun testMonsters500Json_isValid() {
        val file = getAssetFile("nethack_data/nethack500/monsters500.json")
        assertTrue("monsters500.json should exist", file.exists())

        val jsonText = file.readText()
        val jsonElement = JsonParser.parseString(jsonText)
        assertTrue("monsters500.json root should be a JSON array", jsonElement.isJsonArray)

        val jsonArray = jsonElement.asJsonArray
        assertTrue("monsters500.json should contain monster entries", jsonArray.size() > 0)

        for (element in jsonArray) {
            val obj = element.asJsonObject
            assertTrue("Monster entry should have name", obj.has("name") && !obj["name"].asString.isNullOrBlank())
            assertTrue("Monster entry should have symbol", obj.has("symbol"))
            assertTrue("Monster entry should have attacks", obj.has("attacks") && obj["attacks"].isJsonArray)
            assertTrue("Monster entry should have level_details", obj.has("level_details") && obj["level_details"].isJsonObject)
            assertTrue("Monster entry should have size_details", obj.has("size_details") && obj["size_details"].isJsonObject)
        }
    }

    // Check if gendered monster names (e.g. gnome ruler) exist in monsters500.json
    @Test
    fun testMonsters500Json_containsGenderedMonsterNames() {
        val file = getAssetFile("nethack_data/nethack500/monsters500.json")
        val jsonText = file.readText()
        val jsonArray = JsonParser.parseString(jsonText).asJsonArray

        val gnomeRuler = jsonArray.find { it.asJsonObject["name"].asString == "gnome ruler" }?.asJsonObject
        assertNotNull("gnome ruler should exist in monsters500.json", gnomeRuler)
        assertEquals("gnome king", gnomeRuler?.get("male_name")?.asString)
        assertEquals("gnome queen", gnomeRuler?.get("female_name")?.asString)
    }

    // Check if objects500.json exists, is a valid array, and has valid item fields
    @Test
    fun testObjects500Json_isValid() {
        val file = getAssetFile("nethack_data/nethack500/objects500.json")
        assertTrue("objects500.json should exist", file.exists())

        val jsonText = file.readText()
        val jsonElement = JsonParser.parseString(jsonText)
        assertTrue("objects500.json root should be a JSON array", jsonElement.isJsonArray)

        val jsonArray = jsonElement.asJsonArray
        assertTrue("objects500.json should contain item entries", jsonArray.size() > 0)

        for (element in jsonArray) {
            val obj = element.asJsonObject
            assertTrue("Item entry should have name", obj.has("name") && !obj["name"].asString.isNullOrBlank())
            assertTrue("Item entry should have symbol", obj.has("symbol"))
            assertTrue("Item entry should have description", obj.has("description"))
        }
    }

    // Check if properties.json exists, is a valid array, and has valid property fields
    @Test
    fun testPropertiesJson_isValid() {
        val file = getAssetFile("nethack_data/properties.json")
        assertTrue("properties.json should exist", file.exists())

        val jsonText = file.readText()
        val jsonElement = JsonParser.parseString(jsonText)
        assertTrue("properties.json root should be a JSON array", jsonElement.isJsonArray)

        val jsonArray = jsonElement.asJsonArray
        assertTrue("properties.json should contain property entries", jsonArray.size() > 0)

        for (element in jsonArray) {
            val obj = element.asJsonObject
            assertTrue("Property entry should have id", obj.has("id") && !obj["id"].asString.isNullOrBlank())
            assertTrue("Property entry should have name", obj.has("name"))
        }
    }
}
