package ru.gorban.mobileinvestapp.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import ru.gorban.mobileinvestapp.data.entity.CompanyProfile
import ru.gorban.mobileinvestapp.data.entity.News
import ru.gorban.mobileinvestapp.data.entity.QuoteResponse
import ru.gorban.mobileinvestapp.data.entity.Symbol

interface StocksApiRest {

    @GET("stock/profile2")
    suspend fun getCompanyProfile(
        @Query("symbol") symbol: String
    ): Response<CompanyProfile>

    @GET("stock/symbol")
    suspend fun getStocksSymbols(
        @Query("exchange") exchange: String
    ) : Response<List<Symbol>>

    @GET("quote")
    suspend fun getQuote(
        @Query("symbol") symbol: String
    ) : Response<QuoteResponse>

    @GET("company-news")
    suspend fun getNews(
            @Query("symbol") symbol: String,
            @Query("from") from: String,
            @Query("to") to: String
    ): Response<List<News>>
}