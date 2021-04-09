package ru.gorban.mobileinvestapp.data.entity

data class News(
        var datetime: Long,
        var headline: String,
        var image: String,
        var source: String,
        var summary: String,
        var url: String
)
