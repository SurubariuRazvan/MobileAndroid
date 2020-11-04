package com.example.mobileandroid.gameLogic.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.mobileandroid.core.Result
import com.example.mobileandroid.gameLogic.data.local.GameDao
import com.example.mobileandroid.gameLogic.data.remote.GameApi
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameRepository(private val gameDao: GameDao) {
    val games = gameDao.getAll()

    init {
        CoroutineScope(Dispatchers.Main).launch { collectEvents() }
    }

    suspend fun refresh(): Result<Boolean> {
        return try {
            val games = GameApi.service.find()
            for (game in games) {
                gameDao.insert(game)
            }
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun getById(gameId: Long): LiveData<Game> {
        return gameDao.getById(gameId)
    }

    suspend fun save(game: Game): Result<Game> {
        return try {
            val currentGame = GameApi.service.create(game)
            gameDao.insert(currentGame)
            Result.Success(currentGame)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun update(game: Game): Result<Game> {
        return try {
            val currentGame = GameApi.service.update(game.id, game)
            gameDao.update(currentGame)
            Result.Success(currentGame)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun delete(game: Game): Result<Game> {
        return try {
            GameApi.service.delete(game.id)
            gameDao.delete(game.id)
            Result.Success(game)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private suspend fun collectEvents() {
        while (true) {
            val messageData = Gson().fromJson(GameApi.eventChannel.receive(), MessageData::class.java)
            Log.d("GLF: collectEvents", "received $messageData")
            handleMessage(messageData)
        }
    }

    private suspend fun handleMessage(messageData: MessageData) {
        val game = messageData.payload.game
        when (messageData.event) {
            "created" -> gameDao.insert(game)
            "updated" -> gameDao.update(game)
            "deleted" -> gameDao.delete(game.id)
            else -> {
                Log.d("GLF: handleMessage", "received $messageData")
            }
        }
    }
}