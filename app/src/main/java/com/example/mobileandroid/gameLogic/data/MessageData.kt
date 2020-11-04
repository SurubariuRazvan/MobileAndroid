package com.example.mobileandroid.gameLogic.data

data class MessageData(var event: String, var payload: GameJson) {
    data class GameJson(var game: Game)
}

