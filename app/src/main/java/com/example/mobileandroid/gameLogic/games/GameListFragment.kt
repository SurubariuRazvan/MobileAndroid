package com.example.mobileandroid.gameLogic.games

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
import com.example.mobileandroid.auth.data.AuthRepository
import com.example.mobileandroid.core.Constants
import com.example.mobileandroid.core.TAG
import kotlinx.android.synthetic.main.fragment_game_list.*

class GameListFragment : Fragment() {
    private lateinit var gameListAdapter: GameListAdapter
    private lateinit var gamesModel: GameListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_game_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
        if (Constants.instance()?.fetchValueString("token") == null) {
//if (!AuthRepository.isLoggedIn) {
            findNavController().navigate(R.id.fragment_login)
            return;
        }
        setupGameList();
        saveButton.setOnClickListener {
            Log.v(TAG, "add new game")
            findNavController().navigate(R.id.GameEditFragment)
        }
        logout.setOnClickListener {
            Log.v(TAG, "LOGOUT")
            AuthRepository.logout()
            findNavController().navigate(R.id.fragment_login)
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
        Log.v(TAG, "onDestroy")
    }
}