package ru.gorban.mobileinvestapp.data.network

import android.util.Log
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import ru.gorban.mobileinvestapp.BuildConfig
import java.lang.Exception
import java.util.concurrent.TimeUnit

class WebServicesProvider {

    private var _webSocket: WebSocket? = null

    private val socketClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private var _webSocketListener: SocketListener? = null

    fun startSocket(): Flow<List<Trade>> =
        with(SocketListener()) {
            startSocket(this)
            this@with.socketFlow
        }

    private fun startSocket(webSocketListener: SocketListener){
        _webSocketListener = webSocketListener
        val requestBuilder = Request.Builder()
            .url(BuildConfig.WEBSOCKET_URL)
        val request = requestBuilder.build()
        _webSocket = socketClient.newWebSocket(request, webSocketListener)
        socketClient.dispatcher.executorService.shutdown()
    }

    fun stopSocket() {
        try {
            _webSocket?.close(NORMAL_CLOSURE_STATUS, null)
            _webSocket = null
            _webSocketListener = null
        } catch (ex: Exception) {
            Log.e("MySocket" ,"stopSocket Exception $ex")
        }
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}

