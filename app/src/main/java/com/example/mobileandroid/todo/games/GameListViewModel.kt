package com.example.mobileandroid.todo.games

import com.example.mobileandroid.todo.data.GameRepository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileandroid.core.TAG
import com.example.mobileandroid.todo.data.Game
import kotlinx.coroutines.launch

class GameListViewModel : ViewModel() {
    private val mutableGames = MutableLiveData<List<Game>>().apply { value = emptyList() }
    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val games: LiveData<List<Game>> = mutableGames
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

    fun createGame(position: Int) {
        val list = mutableListOf<Game>()
        list.addAll(mutableGames.value!!)
        list.add(Game(position.toLong(), 5665 , "Game $position", "yttyt", 0, 0, "", 0.0f))
        mutableGames.value = list
    }

    fun loadGames() {
        viewModelScope.launch {
            Log.v(TAG, "loadGames...")
            mutableLoading.value = true
            mutableException.value = null
            try {
                mutableGames.value = GameRepository.loadAll()
                Log.d(TAG, "loadGames succeeded")
                mutableLoading.value = false
            } catch (e: Exception) {
                Log.w(TAG, "loadGames failed", e)
                mutableException.value = e
                mutableLoading.value = false
            }
        }
    }
}
