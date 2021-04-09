package ru.gorban.mobileinvestapp.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.gorban.mobileinvestapp.BuildConfig

object StocksService {

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header(BuildConfig.HEADER_PARAMETR, BuildConfig.FINNHUB_ACCESS_KEY)
        val request = requestBuilder.build()
        chain.proceed(request)
    }
        .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE))
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiRest: StocksApiRest by lazy {
        retrofit.create(StocksApiRest::class.java)
    }
}