package com.example.mobileandroid.gameLogic.game

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobileandroid.core.TAG
import com.example.mobileandroid.gameLogic.data.Game
import com.example.mobileandroid.gameLogic.data.GameRepository
import com.example.mobileandroid.gameLogic.data.local.GameDatabase
import kotlinx.coroutines.launch
import com.example.mobileandroid.core.Result

class GameEditViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableFetching = MutableLiveData<Boolean>().apply { value = false }
    private val mutableCompleted = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val fetching: LiveData<Boolean> = mutableFetching
    val fetchingError: LiveData<Exception> = mutableException
    val completed: LiveData<Boolean> = mutableCompleted

    private val gameRepository: GameRepository

    init {
        val gameDao = GameDatabase.getDatabase(application, viewModelScope).gameDao()
        gameRepository = GameRepository(gameDao)
    }

    fun getItemById(gameId: Long): LiveData<Game> {
        Log.v(TAG, "getGameById...")
        return gameRepository.getById(gameId)
    }

    fun saveOrUpdateGame(game: Game) {
        viewModelScope.launch {
            Log.v(TAG, "saveOrUpdateGame...")
            mutableFetching.value = true
            mutableException.value = null
            when (val result = if (game._id != 0L) gameRepository.update(game) else gameRepository.save(game)) {
                is Result.Success -> Log.d(TAG, "saveOrUpdateGame succeeded")
                is Result.Error -> {
                    Log.w(TAG, "saveOrUpdateGame failed", result.exception)
                    mutableException.value = result.exception
                }
            }
            mutableCompleted.value = true
            mutableFetching.value = false
        }
    }

    fun deleteGame(game: Game) {
        viewModelScope.launch {
            Log.v(TAG, "deleteGame...")
            mutableFetching.value = true
            mutableException.value = null
            when (val result: Result<Game> = gameRepository.delete(game)) {
                is Result.Success -> Log.d(TAG, "deleteGame succeeded")
                is Result.Error -> {
                    Log.w(TAG, "deleteGame failed", result.exception)
                    mutableException.value = result.exception
                }
            }
            mutableCompleted.value = true
            mutableFetching.value = false
        }
    }
}
