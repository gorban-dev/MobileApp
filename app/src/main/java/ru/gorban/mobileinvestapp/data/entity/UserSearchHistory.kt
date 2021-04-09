package ru.gorban.mobileinvestapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_search_history")
data class UserSearchHistory(
    @PrimaryKey
    @ColumnInfo(name = "user_request")
    val request: String
)
