package com.example.nethackseer

import android.app.Application
import com.example.nethackseer.data.NetHackRepository
import com.example.nethackseer.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class NetHackSeerApplication : Application() {
    // need this for in case coroutines fail, to make others not be cancelled
    private val applicationScope = CoroutineScope(SupervisorJob())

    // 'by lazy' means that the database and repository are only created when first needed
    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { NetHackRepository(database.monsterDao()) }
}