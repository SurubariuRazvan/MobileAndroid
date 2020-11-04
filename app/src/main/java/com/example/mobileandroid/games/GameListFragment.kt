package com.example.mobileandroid.games

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mobileandroid.R
import com.example.mobileandroid.core.TAG
import com.example.mobileandroid.data.MessageData
import com.example.mobileandroid.data.remote.GameApi
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_game_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameListFragment : Fragment() {
    private lateinit var gameListAdapter: GameListAdapter
    private lateinit var gamesModel: GameListViewModel
    private var isActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch { collectEvents() }
        isActive = true
        Log.v(TAG, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
        setupGameList()
        saveButton.setOnClickListener {
            Log.v(TAG, "add new game")
            findNavController().navigate(R.id.GameEditFragment)
        }
    }

    private fun setupGameList() {
        gameListAdapter = GameListAdapter(this)
        game_list.adapter = gameListAdapter
        gamesModel = ViewModelProvider(this).get(GameListViewModel::class.java)

        gamesModel.games.observe(viewLifecycleOwner, { games ->
            Log.v(TAG, "update games")
            gameListAdapter.games = games
        })
        gamesModel.loading.observe(viewLifecycleOwner, { loading ->
            Log.i(TAG, "update loading")
            progress.visibility = if (loading) View.VISIBLE else View.GONE
        })
        gamesModel.loadingError.observe(viewLifecycleOwner, { exception ->
            if (exception != null) {
                Log.i(TAG, "update loading error")
                val message = "Loading exception ${exception.message}"
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        })
        gamesModel.refresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        isActive = false
        Log.v(TAG, "onDestroy")
    }

    private suspend fun collectEvents() {
        while (isActive) {
            val messageData =
                Gson().fromJson(GameApi.eventChannel.receive(), MessageData::class.java)
            Log.d("GLF: collectEvents", "received $messageData")
            handleMessage(messageData)
        }
    }

    private suspend fun handleMessage(messageData: MessageData) {
        val game = messageData.payload.game

        when (messageData.event) {
            "created" -> gamesModel.gameRepository.save(game, false)
            "updated" -> gamesModel.gameRepository.update(game, false)
            "deleted" -> gamesModel.gameRepository.delete(game, false)
            else -> {
                Log.d("GLF: handleMessage", "received $messageData")
            }
        }
        gamesModel.refresh()
    }
}