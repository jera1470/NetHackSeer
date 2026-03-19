package com.example.nethackseer.data

import com.example.nethackseer.data.local.dao.ItemDao
import com.example.nethackseer.data.local.dao.MonsterDao
import com.example.nethackseer.data.local.dao.PropertyDao
import com.example.nethackseer.data.local.entity.ItemEntity
import com.example.nethackseer.data.local.entity.MonsterEntity
import com.example.nethackseer.data.local.entity.PropertyEntity
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
     * Get all names from both monsters and items, combined and sorted.
     */
    val allNames: Flow<List<String>> = combine(
        monsterDao.getAllNames(),
        itemDao.getAllNames()
    ) { monsters, items ->
        (monsters + items).sorted()
    }

    /**
     * Search for monsters by name.
     */
    fun searchMonsters(query: String): Flow<List<MonsterEntity>> =
        monsterDao.search(query)

    /**
     * Get a monster by name.
     */
    fun getMonsterByName(name: String): Flow<MonsterEntity?> = monsterDao.getMonsterByName(name)

    /**
     * Search for items by name.
     */
    fun searchItems(query: String): Flow<List<ItemEntity>> = itemDao.search(query)

    /**
     * Get an item by name.
     */
    fun getItemByName(name: String): Flow<ItemEntity?> = itemDao.getItemByName(name)

    /**
     * Resolves a list of property IDs (e.g., ["M1_ANIMAL", "M1_NOHANDS"]) into full PropertyEntity objects.
     */
    fun getPropertiesByIds(ids: List<String>): Flow<List<PropertyEntity>> =
        propertyDao.getPropertiesByIds(ids)

    /**
     * Gets all available properties.
     */
    val allProperties: Flow<List<PropertyEntity>> = propertyDao.getAllProperties()
}
