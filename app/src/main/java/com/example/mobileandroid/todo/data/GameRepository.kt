package com.example.mobileandroid.todo.data

import android.util.Log
import com.example.mobileandroid.core.TAG
import com.example.mobileandroid.todo.data.remote.GameApi
import com.example.mobileandroid.todo.games.GameListAdapter

object GameRepository {
    private var cachedGames: MutableList<Game>? = mutableListOf()

    suspend fun loadAll(): List<Game> {
        Log.i(TAG, "loadAll")
        if (cachedGames != null) {
            return cachedGames as List<Game>
        }
        val games = GameApi.service.find()
        cachedGames?.addAll(games)
        return cachedGames as List<Game>
    }

    suspend fun load(gameId: Long): Game {
        Log.i(TAG, "load")
        val game = cachedGames?.find { it.id == gameId }
        if (game != null) {
            return game
        }
        return GameApi.service.read(gameId)
    }

    suspend fun save(game: Game): Game {
        Log.i(TAG, "save")
        println(game)
        val createdGame = GameApi.service.create(game)
        println(createdGame)
        cachedGames?.add(createdGame)
        return createdGame
    }

    suspend fun update(game: Game): Game {
        Log.i(TAG, "update")
        println(game)
        val updatedGame = GameApi.service.update(game.id, game)
        println(updatedGame)
        val index = cachedGames?.indexOfFirst { it.id == game.id }
        if (index != null) {
            cachedGames?.set(index, updatedGame)
        }
        return updatedGame
    }

    suspend fun updateCachedGame(game: Game) {
        val index = cachedGames?.indexOfFirst { it.id == game.id }
        if (index != null) {
            cachedGames?.set(index, game)
        }
    }

    suspend fun saveCachedGame(game: Game) {
        val index = cachedGames?.indexOfFirst { it.id == game.id }
        if (index == null) {
            cachedGames?.add(game)
        }
    }

    suspend fun deleteCachedGame(gameId: Long) {
        val index = cachedGames?.indexOfFirst { it.id == gameId }
        if (index != null) {
            cachedGames?.removeAt(index)
        }
    }

    suspend fun delete(gameId: Long) {
        Log.i(TAG, "delete")
        GameApi.service.delete(gameId)
        val index = cachedGames?.indexOfFirst { it.id == gameId }
        if (index != null) {
            cachedGames?.removeAt(index)
        }
    }
}