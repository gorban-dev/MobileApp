package ru.gorban.mobileinvestapp.data.network

import org.json.JSONObject

class ResponseSocket(json: String) : JSONObject(json) {
    val data = this.optJSONArray("data")
        ?.let { 0.until(it.length()).map { i -> it.optJSONObject(i) } } // returns an array of JSONObject
        ?.map { Trade(it.toString()) } // transforms each JSONObject of the array into Foo
}

class Trade(json: String) : JSONObject(json) {
    val lastPrice: Double? = this.optDouble("p")
    val ticker: String? = this.optString("s")
    val timestamp: Long? = this.optLong("t")
    val volume: Double? = this.optDouble("v")
}