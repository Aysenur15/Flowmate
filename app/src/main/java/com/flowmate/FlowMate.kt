package com.flowmate

import android.app.Application
import com.flowmate.data.AppDatabase

// FlowMateApp is the main application class for the FlowMate app.
class FlowMateApp : Application() {

    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.DatabaseBuilder.getInstance(applicationContext)
    }
}
