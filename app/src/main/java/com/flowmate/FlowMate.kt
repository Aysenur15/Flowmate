package com.flowmate

import android.app.Application
import com.flowmate.data.AppDatabase

class FlowMateApp : Application() {

    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.DatabaseBuilder.getInstance(applicationContext)
    }
    /*
    override fun onCreate() {
        super.onCreate()
        // Room DB oluştur
        val db = Room.databaseBuilder(
            applicationContext,
            FlowMateDatabase::class.java,
            "flowmate.db"
        ).build()

        // Repository oluştur
        val authRepository = AuthRepository(
            userDao = db.userDao(),
            auth = FirebaseAuth.getInstance(),
            firestore = FirebaseFirestore.getInstance()
        )
      }-*/
}
