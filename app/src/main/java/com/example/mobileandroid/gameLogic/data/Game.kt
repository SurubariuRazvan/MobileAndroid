package com.example.mobileandroid.gameLogic.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey @ColumnInfo(name = "_id") val _id: Long,
    @ColumnInfo(name = "userId") val userId: Long,
    @ColumnInfo(name = "appid") var appid: Long,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "developer") var developer: String,
    @ColumnInfo(name = "positive") var positive: Long,
    @ColumnInfo(name = "negative") var negative: Long,
    @ColumnInfo(name = "owners") var owners: String,
    @ColumnInfo(name = "price") var price: Float,
) {
    override fun toString(): String = "$_id $userId $appid $name $developer $positive $negative $owners $price"
}
