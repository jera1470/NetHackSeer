package com.example.nethackseer.data

import com.example.nethackseer.data.local.dao.ItemDao
import com.example.nethackseer.data.local.dao.MonsterDao
import com.example.nethackseer.data.local.dao.PropertyDao
import com.example.nethackseer.data.local.entity.ItemEntity
import com.example.nethackseer.data.local.entity.MonsterEntity
import com.example.nethackseer.data.local.entity.PropertyEntity
import com.example.nethackseer.ui.components.SearchResultItem
import com.example.nethackseer.ui.utils.getDisplayChar
import com.example.nethackseer.ui.utils.getSymbolDisplayName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class NetHackRepository(
    private val monsterDao: MonsterDao,
    private val itemDao: ItemDao,
    private val propertyDao: PropertyDao
) {
    val allMonsters: Flow<List<MonsterEntity>> = monsterDao.getAll()
    val allItems: Flow<List<ItemEntity>> = itemDao.getAll()

    /**
     * Get all names from monsters, items, and properties, combined and sorted.
     */
    val allNames: Flow<List<String>> = combine(
        monsterDao.getAllNames(),
        itemDao.getAllNames(),
        propertyDao.getAllNames()
    ) { monsters, items, properties ->
        (monsters + items + properties).sorted()
    }

    /**
     * Search for monsters and items matching a text query (name, gendered name, or glyph display character).
     *
     * @param query The text query to search with
     *
     * @return a Flow giving a list of SearchResultItem objects that match the query
     */
    fun searchMonstersItems(query: String): Flow<List<SearchResultItem>> = combine(
        allMonsters,
        allItems
    ) { monsters, items ->
        val trimmedQuery = query.trim().lowercase()
        if (trimmedQuery.isEmpty()) {
            emptyList()
        } else {
            val monsterResults = monsters.filter { monster ->
                monster.name.contains(trimmedQuery, ignoreCase = true) ||
                        monster.maleName?.contains(trimmedQuery, ignoreCase = true) == true ||
                        monster.femaleName?.contains(trimmedQuery, ignoreCase = true) == true ||
                        getDisplayChar(monster.symbol).equals(trimmedQuery, ignoreCase = true) ||
                        getSymbolDisplayName(monster.symbol).contains(trimmedQuery, ignoreCase = true)
            }.map { monster ->
                SearchResultItem(
                    name = monster.name,
                    category = "monster",
                    symbol = monster.symbol,
                    color = monster.color
                )
            }

            val itemResults = items.filter { item ->
                item.name.contains(trimmedQuery, ignoreCase = true) ||
                        getDisplayChar(item.symbol).equals(trimmedQuery, ignoreCase = true)
            }.map { item ->
                SearchResultItem(
                    name = item.name,
                    category = "item",
                    symbol = item.symbol,
                    color = item.color
                )
            }

            (monsterResults + itemResults).sortedBy { it.name }
        }
    }

    /**
     * Get a monster by name.
     */
    fun getMonsterByName(name: String): Flow<MonsterEntity?> = monsterDao.getMonsterByName(name)

    /**
     * Get an item by name.
     */
    fun getItemByName(name: String): Flow<ItemEntity?> = itemDao.getItemByName(name)

    /**
     * Get a property by name.
     */
    fun getPropertyByName(name: String): Flow<PropertyEntity?> = propertyDao.getPropertyByName(name)

    /**
     * Resolves a list of property IDs (e.g., ["M1_ANIMAL", "M1_NOHANDS"]) into full PropertyEntity objects.
     */
    fun getPropertiesByIds(ids: List<String>): Flow<List<PropertyEntity>> =
        propertyDao.getPropertiesByIds(ids)
}
