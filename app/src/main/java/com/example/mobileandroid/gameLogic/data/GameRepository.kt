package com.example.mobileandroid.gameLogic.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.mobileandroid.core.Constants
import com.example.mobileandroid.core.Result
import com.example.mobileandroid.gameLogic.data.local.GameDao
import com.example.mobileandroid.gameLogic.data.remote.GameApi
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameRepository(private val gameDao: GameDao) {
    //    val games = gameDao.getAll()
    val games = MediatorLiveData<List<Game>>().apply { postValue(emptyList()) }

    init {
        CoroutineScope(Dispatchers.Main).launch { collectEvents() }
    }

    suspend fun refresh(): Result<Boolean> {
        Log.d("refresh", " is refreshing");
        return try {
            val gamesApi = GameApi.service.find()
            games.value = gamesApi
            for (game in gamesApi) {
                gameDao.insert(game)
            }
            Result.Success(true)
        } catch (e: Exception) {
            val userId = Constants.instance()?.fetchValueString("_id")?.toLong()
            games.addSource(gameDao.getAll(userId!!)) {
                games.value = it
            }
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
            val currentGame = GameApi.service.update(game._id, game)
            gameDao.update(currentGame)
            Result.Success(currentGame)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun delete(game: Game): Result<Game> {
        return try {
            GameApi.service.delete(game._id)
            gameDao.delete(game._id)
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
        val game = messageData.payload
        when (messageData.event) {
            "created" -> {
                gameDao.insert(game)
                refresh()
            }
            "updated" -> {
                gameDao.update(game)
                refresh()
            }
            "deleted" -> {
                gameDao.delete(game._id)
                refresh()
            }
            else -> {
                Log.d("GLF: handleMessage", "received $messageData")
            }
        }
    }
}