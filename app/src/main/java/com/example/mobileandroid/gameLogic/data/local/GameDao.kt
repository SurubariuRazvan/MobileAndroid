package com.example.mobileandroid.gameLogic.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mobileandroid.gameLogic.data.Game

@Dao
interface GameDao {
    @Query("SELECT * from games WHERE userId=:userId ORDER BY _id ASC")
    fun getAll(userId: Long): LiveData<List<Game>>

    @Query("SELECT * FROM games WHERE _id=:id ")
    fun getById(id: Long): LiveData<Game>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: Game)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(game: Game)

    @Query("DELETE FROM games WHERE _id=:id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM games")
    suspend fun deleteAll()
}