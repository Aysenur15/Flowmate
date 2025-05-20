package com.flowmate.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    object DatabaseBuilder {
        private const val DATABASE_NAME = "word-db"

        private var instance: AppDatabase? = null

        /*private val scope = kotlinx.coroutines.CoroutineScope(Dispatchers.IO)*/

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                synchronized(AppDatabase::class) {
                    instance = buildRoomDB(context)
                }
            }

            /* Populate Database
            * Uncomment the following code to populate the database with words from the animations file
            */
            /*      scope.launch(Dispatchers.IO) {
                      populateDatabase(context, instance!!.wordDao())
                  }*/


            return instance!!
        }

        private fun buildRoomDB(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
            /* Uncomment the following line to create the database in memory
            *
             */
        )//.createFromAsset("database/word-db.db")
            .build()
        /**
         * Populate the database with words from the animations file
         * Uncomment the following code to populate the database with words from the animations file
         *
         */
        /*private suspend fun populateDatabase(context: Context, wordDao: WordDao) {

            try {
                fillWordsAndExams(context, wordDao)
                fillStatisticTable(wordDao)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/


}

}
