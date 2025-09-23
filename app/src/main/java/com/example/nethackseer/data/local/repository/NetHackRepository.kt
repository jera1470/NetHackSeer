package com.example.nethackseer.data.local.repository

import com.example.nethackseer.data.local.dao.NetHackDao
import com.example.nethackseer.data.local.entity.NetHackEntity
import kotlinx.coroutines.flow.Flow

class NetHackRepository(private val netHackDao: NetHackDao) {
    // Expose all entities in a stream from local database.
    // ViewModel collects the Flow.
    val allEntities : Flow<List<NetHackEntity>> = netHackDao.getAll()
}