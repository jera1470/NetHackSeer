package com.example.nethackseer.data

import com.example.nethackseer.data.local.dao.MonsterDao
import com.example.nethackseer.data.local.entity.MonsterEntity
import kotlinx.coroutines.flow.Flow

class NetHackRepository(private val monsterDao: MonsterDao) {
    // Expose all monsters in a stream from local database.
    // ViewModel collects the Flow.
    val allMonsters: Flow<List<MonsterEntity>> = monsterDao.getAll()

    /**
     * Search for monsters by name.
     */
    fun searchMonsters(query: String): Flow<List<MonsterEntity>> =
        monsterDao.search(query)

}