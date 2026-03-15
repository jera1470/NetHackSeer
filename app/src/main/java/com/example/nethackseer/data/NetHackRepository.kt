package com.example.nethackseer.data

import com.example.nethackseer.data.local.dao.ItemDao
import com.example.nethackseer.data.local.dao.MonsterDao
import com.example.nethackseer.data.local.entity.ItemEntity
import com.example.nethackseer.data.local.entity.MonsterEntity
import kotlinx.coroutines.flow.Flow

class NetHackRepository(private val monsterDao: MonsterDao, private val itemDao: ItemDao) {
    val allMonsters: Flow<List<MonsterEntity>> = monsterDao.getAll()
    val allItems: Flow<List<ItemEntity>> = itemDao.getAll()

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

}