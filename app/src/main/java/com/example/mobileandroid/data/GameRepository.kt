package com.example.mobileandroid.data

import androidx.lifecycle.LiveData
import com.example.mobileandroid.data.local.GameDao
import com.example.mobileandroid.data.remote.GameApi
import com.example.mobileandroid.core.Result


class GameRepository(private val gameDao: GameDao) {
    val games = gameDao.getAll()

    suspend fun refresh(): Result<Boolean> {
        try {
            val games = GameApi.service.find()
            for (game in games) {
                gameDao.insert(game)
            }
            return Result.Success(true)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    fun getById(gameId: Long): LiveData<Game> {
        return gameDao.getById(gameId)
    }

    suspend fun save(game: Game, sendToServer: Boolean): Result<Game> {
        var currentGame = game
        try {
            if (sendToServer)
                currentGame = GameApi.service.create(game)
            gameDao.insert(currentGame)
            return Result.Success(currentGame)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun update(game: Game, sendToServer: Boolean): Result<Game> {
        var currentGame = game
        try {
            if (sendToServer)
                currentGame = GameApi.service.update(game.id, game)
            gameDao.update(currentGame)
            return Result.Success(currentGame)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun delete(game: Game, sendToServer: Boolean): Result<Game> {
        try {
            if (sendToServer)
                GameApi.service.delete(game.id)
            gameDao.delete(game.id)
            return Result.Success(game)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }
}