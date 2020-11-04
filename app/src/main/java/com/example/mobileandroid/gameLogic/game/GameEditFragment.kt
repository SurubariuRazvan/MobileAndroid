package com.example.mobileandroid.gameLogic.game

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
import com.example.mobileandroid.gameLogic.data.Game
import kotlinx.android.synthetic.main.fragment_game_edit.*

class GameEditFragment : Fragment() {
    companion object {
        const val GAME_ID = "GAME_ID"
    }

    private lateinit var viewModel: GameEditViewModel
    private var gameId: Long? = null
    private var game: Game? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate")
        arguments?.let { if (it.containsKey(GAME_ID)) gameId = it.getLong(GAME_ID) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.v(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_game_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
        setupViewModel()
        saveButton.setOnClickListener {
            Log.v(TAG, "save game")
            val i = game
            if (i != null) {
                i.appid = game_appid.text.toString().toLong()
                i.name = game_name.text.toString()
                i.developer = game_developer.text.toString()
                i.positive = game_positive.text.toString().toLong()
                i.negative = game_negative.text.toString().toLong()
                i.owners = game_owners.text.toString()
                i.price = game_price.text.toString().toFloat()
                viewModel.saveOrUpdateGame(i)
            }
        }
        deleteButton.setOnClickListener {
            Log.v(TAG, "delete game")
            val i = game
            if (i != null) viewModel.deleteGame(i)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(GameEditViewModel::class.java)
        viewModel.fetching.observe(viewLifecycleOwner, { fetching ->
            Log.v(TAG, "update fetching")
            progress.visibility = if (fetching) View.VISIBLE else View.GONE
        })
        viewModel.fetchingError.observe(viewLifecycleOwner, { exception ->
            if (exception != null) {
                Log.v(TAG, "update fetching error")
                val message = "Fetching exception ${exception.message}"
                val parentActivity = activity?.parent
                if (parentActivity != null) {
                    Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.completed.observe(viewLifecycleOwner, { completed ->
            if (completed) {
                Log.v(TAG, "completed, navigate back")
                findNavController().navigateUp()
            }
        })
        val id = gameId
        if (id == null) {
            game = Game(0, 0, "", "", 0, 0, "", 0.0f)
        } else {
            viewModel.getItemById(id).observe(viewLifecycleOwner, {
                Log.v(TAG, "update items")
                if (it != null) {
                    game = it
                    game_appid.setText(it.appid.toString())
                    game_name.setText(it.name)
                    game_developer.setText(it.developer)
                    game_positive.setText(it.positive.toString())
                    game_negative.setText(it.negative.toString())
                    game_owners.setText(it.owners)
                    game_price.setText(it.price.toString())
                }
            })
        }
    }
}
