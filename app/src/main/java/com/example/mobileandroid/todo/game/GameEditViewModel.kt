package com.example.mobileandroid.todo.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileandroid.core.TAG
import com.example.mobileandroid.todo.data.Game
import com.example.mobileandroid.todo.data.GameRepository
import kotlinx.coroutines.launch

class GameEditViewModel : ViewModel() {
    private val mutableGame =
        MutableLiveData<Game>().apply { value = Game(0, 555, "vcbghf", "gfhfghfg", 0, 0, "fgfdgfd", 0.0f) }
    private val mutableFetching = MutableLiveData<Boolean>().apply { value = false }
    private val mutableCompleted = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val game: LiveData<Game> = mutableGame
    val fetching: LiveData<Boolean> = mutableFetching
    val fetchingError: LiveData<Exception> = mutableException
    val completed: LiveData<Boolean> = mutableCompleted

    fun loadGame(gameId: Long) {
        viewModelScope.launch {
            Log.i(TAG, "loadGame...")
            mutableFetching.value = true
            mutableException.value = null
            try {
                mutableGame.value = GameRepository.load(gameId)
                Log.i(TAG, "loadGame succeeded")
                mutableFetching.value = false
            } catch (e: Exception) {
                Log.w(TAG, "loadGame failed", e)
                mutableException.value = e
                mutableFetching.value = false
            }
        }
    }

    fun saveOrUpdateGame(name: String) {
        viewModelScope.launch {
            Log.i(TAG, "saveOrUpdateGame...")
            val game = mutableGame.value ?: return@launch
            game.name = name
            mutableFetching.value = true
            mutableException.value = null
            try {
                if (game.id != 0L) {
                    mutableGame.value = GameRepository.update(game)
                } else {
                    mutableGame.value = GameRepository.save(game)
                }
                Log.i(TAG, "saveOrUpdateGame succeeded")
                mutableCompleted.value = true
                mutableFetching.value = false
            } catch (e: Exception) {
                Log.w(TAG, "saveOrUpdateGame failed", e)
                mutableException.value = e
                mutableFetching.value = false
            }
        }
    }
}
