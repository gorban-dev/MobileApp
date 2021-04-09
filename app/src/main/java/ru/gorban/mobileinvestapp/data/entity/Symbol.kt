package ru.gorban.mobileinvestapp.data.entity

import androidx.room.ColumnInfo

data class Symbol(
    /**Display symbol name.*/
    @ColumnInfo(name = "stocks_ticker")
    val displaySymbol: String,
    /**Symbol description*/
    @ColumnInfo(name = "stocks_name")
    val description: String
    )