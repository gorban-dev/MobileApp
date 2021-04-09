package ru.gorban.mobileinvestapp.data.repository


import ru.gorban.mobileinvestapp.data.network.WebServicesProvider

class SocketRepository constructor(private val webServicesProvider: WebServicesProvider) {

    fun startSocket() = webServicesProvider.startSocket()

    fun closeSocket() = webServicesProvider.stopSocket()

}