package ru.gorban.mobileinvestapp.data.network

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import ru.gorban.mobileinvestapp.data.repository.DefaultListSymbols

//TODO Рассмотреть переход на Scarlet (A Retrofit inspired WebSocket client for Kotlin, Java, and Android)
class SocketListener: WebSocketListener() {

    var socketFlow = MutableSharedFlow<List<Trade>>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.i("MySocket", "Socket response: $response")
        subscribe(webSocket)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
            updatePriceInDatabase(text)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        Log.i("MySocket", "onClosing with code: $code")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Log.i("MySocket", "onClosed with code: $code")
    }


    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.i("MySocket", "Throwable: $t")
        Log.i("MySocket", "Throwable: $response")
    }

    private fun subscribe(webSocket: WebSocket) {
        val listTickers = DefaultListSymbols.ListSymbols
        if (listTickers.isNotEmpty()) {
            listTickers.forEach { ticker ->
                webSocket.send("{\"type\":\"subscribe\",\"symbol\":\"$ticker\"}")
            }
        }
    }

  private fun updatePriceInDatabase(string: String) {
        val socketResponse = ResponseSocket(string)
        socketResponse.data?.let {list ->
            val sortedList =  list.distinctBy {it.ticker to it.lastPrice}
            GlobalScope.launch {
                socketFlow.emit(sortedList)
            }
        }
    }
}

data class Selector(val property1: String?, val property2: Double?)

