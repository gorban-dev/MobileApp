package ru.gorban.mobileinvestapp.data.entity

data class QuoteResponse(
    /**Open price of the day*/
    val o: Double,
    /**High price of the day*/
    val h: Double,
    /**Low price of the day*/
    var i: String,
    /**Current price*/
    var c: Double,
    /**Previous close price*/
    var pc: Double,
    /**UNIX milliseconds timestamp*/
    var t: Long
)
