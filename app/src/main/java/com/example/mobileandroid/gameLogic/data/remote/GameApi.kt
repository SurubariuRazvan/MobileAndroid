package com.example.mobileandroid.gameLogic.data.remote

import android.util.Log
import com.example.mobileandroid.gameLogic.data.Game
import com.google.gson.GsonBuilder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import retrofit2.http.Headers

object GameApi {
    private const val URL = "http://192.168.0.101:3000/"
    private const val WSURL = "ws://192.168.0.101:3000/"

    interface Service {
        @GET("/games")
        suspend fun find(): List<Game>

        @GET("/games/{id}")
        suspend fun read(@Path("id") gameId: Long): Game

        @Headers("Content-Type: application/json")
        @POST("/games")
        suspend fun create(@Body game: Game): Game

        @Headers("Content-Type: application/json")
        @PUT("/games/{id}")
        suspend fun update(@Path("id") gameId: Long, @Body game: Game): Game

        @DELETE("/games/{id}")
        suspend fun delete(@Path("id") gameId: Long): Void
    }

    private val client: OkHttpClient = OkHttpClient.Builder().build()

    private var gson = GsonBuilder().setLenient().create()

    private val retrofit = Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create(gson)).client(client).build()

    val service: Service = retrofit.create(Service::class.java)
    val eventChannel = Channel<String>()

    init {
        val request = Request.Builder().url(WSURL).build()
        OkHttpClient().newWebSocket(request, MyWebSocketListener())
    }

    private class MyWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d("WebSocket", "onOpen")
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