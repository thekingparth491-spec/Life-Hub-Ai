package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.repository.LifeHubRepository

class LifeHubApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var repository: LifeHubRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getDatabase(this)
        repository = LifeHubRepository(database, this)
    }

    companion object {
        lateinit var instance: LifeHubApplication
            private set
    }
}
