package com.example.mobileandroid.games

import android.app.Application
import com.example.mobileandroid.data.GameRepository

import android.util.Log
import androidx.lifecycle.*
import com.example.mobileandroid.core.TAG
import com.example.mobileandroid.data.Game
import com.example.mobileandroid.data.local.GameDatabase
import kotlinx.coroutines.launch
import com.example.mobileandroid.core.Result

class GameListViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val games: LiveData<List<Game>>
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

    val gameRepository: GameRepository

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
                is Result.Success -> {
                    Log.d(TAG, "refresh succeeded")
                }
                is Result.Error -> {
                    Log.w(TAG, "refresh failed", result.exception)
                    mutableException.value = result.exception
                }
            }
            mutableLoading.value = false
        }
    }
}
