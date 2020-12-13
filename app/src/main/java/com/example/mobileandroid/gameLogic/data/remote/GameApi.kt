package com.example.mobileandroid.gameLogic.data.remote

import android.util.Log
import com.example.mobileandroid.core.Api
import com.example.mobileandroid.core.Constants
import com.example.mobileandroid.gameLogic.data.Game
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import okhttp3.*
import retrofit2.http.*
import retrofit2.http.Headers

object GameApi {
    interface Service {
        @GET("/api/games")
        suspend fun find(): List<Game>

        @GET("/api/games/{id}")
        suspend fun read(@Path("id") gameId: Long): Game

        @Headers("Content-Type: application/json")
        @POST("/api/games")
        suspend fun create(@Body game: Game): Game

        @Headers("Content-Type: application/json")
        @PUT("/api/games/{id}")
        suspend fun update(@Path("id") gameId: Long, @Body game: Game): Game

        @DELETE("/api/games/{id}")
        suspend fun delete(@Path("id") gameId: Long): Void
    }

    val eventChannel = Channel<String>()

    val service: Service = Api.retrofit.create(Service::class.java)

    init {
        val request = Request.Builder().url("wss://${Api.URL}").build()
        OkHttpClient().newWebSocket(request, MyWebSocketListener())
    }

    private class MyWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d("WebSocket", "onOpen")
            val token = Constants.instance()?.fetchValueString("token")!!
            webSocket.send("{\"type\":\"authorization\",\"payload\":{\"token\":\"${token}\"}}")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("WebSocket", "onMessage$text")
            runBlocking { eventChannel.send(text) }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("WebSocket", "onFailure", t)
            t.printStackTrace()
        }

        private fun output(txt: String) {
            Log.d("WebSocket", txt)
        }
    }
}