package com.example.mobileandroid.gameLogic.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mobileandroid.gameLogic.data.Game
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Game::class], version = 1)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): GameDatabase {
            val inst = INSTANCE
            if (inst != null) {
                return inst
            }
            val instance = Room.databaseBuilder(context.applicationContext, GameDatabase::class.java, "game_db").addCallback(WordDatabaseCallback(scope)).build()
            INSTANCE = instance
            return instance
        }

        private class WordDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.gameDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(gameDao: GameDao) {
            gameDao.deleteAll()
        }
    }

}
