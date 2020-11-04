package com.example.mobileandroid.todo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "appid")
    var appid: Long,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "developer")
    var developer: String,
    @ColumnInfo(name = "positive")
    var positive: Long,
    @ColumnInfo(name = "negative")
    var negative: Long,
    @ColumnInfo(name = "owners")
    var owners: String,
    @ColumnInfo(name = "price")
    var price: Float,
) {
    override fun toString(): String =
        "$id $appid $name $developer $positive $negative $owners $price"
}
