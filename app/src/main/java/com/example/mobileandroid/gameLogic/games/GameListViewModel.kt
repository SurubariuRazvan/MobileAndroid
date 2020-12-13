package com.example.mobileandroid.gameLogic.games

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

class GameListViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val games: LiveData<List<Game>>
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

    private val gameRepository: GameRepository

    init {
        val gameDao = GameDatabase.getDatabase(application, viewModelScope).gameDao()
        gameRepository = GameRepository(gameDao)
        games = gameRepository.games
    }

    fun refresh() {
        viewModelScope.launch {
            Log.v(TAG, "loadGames...")
            mutableLoading.value = true
            mutableException.value = null
            when (val result = gameRepository.refresh()) {
                is Result.Success -> Log.d(TAG, "refresh succeeded")
                is Result.Error -> {
                    Log.w(TAG, "refresh failed", result.exception)
                    mutableException.value = result.exception
                }
            }
            mutableLoading.value = false
        }
    }
}
