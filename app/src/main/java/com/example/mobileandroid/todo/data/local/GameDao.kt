package com.example.mobileandroid.todo.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mobileandroid.todo.data.Game

@Dao
interface GameDao {
    @Query("SELECT * from games ORDER BY text ASC")
    fun getAll(): LiveData<List<Game>>

    @Query("SELECT * FROM games WHERE id=:_id ")
    fun getById(_id: String): LiveData<Game>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: Game)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(game: Game)

    @Query("DELETE FROM games")
    suspend fun deleteAll()
}