package com.example.nethackseer

import android.app.Application
import com.example.nethackseer.data.NetHackRepository
import com.example.nethackseer.data.local.NetHackDatabase

class NetHackSeerApplication : Application() {
    // 'by lazy' means that the database and repository are only created when first needed
    val database by lazy { NetHackDatabase.getDatabase(this) }
    val repository by lazy { NetHackRepository(database.netHackDao()) }
}