package ru.gorban.mobileinvestapp.data.repository

import retrofit2.Response
import ru.gorban.mobileinvestapp.BuildConfig
import ru.gorban.mobileinvestapp.data.entity.CompanyProfile
import ru.gorban.mobileinvestapp.data.entity.News
import ru.gorban.mobileinvestapp.data.entity.QuoteResponse
import ru.gorban.mobileinvestapp.data.entity.Symbol
import ru.gorban.mobileinvestapp.data.network.StocksService
import javax.inject.Inject

class RemoteCompanyRepository @Inject constructor() {

    /**
     * Выполнение сетевых запросов
     */
    suspend fun getCompanyProfile(symbol: String): Response<CompanyProfile> {
        return StocksService.apiRest.getCompanyProfile(symbol)
    }

    suspend fun getStocksSymbols(): Response<List<Symbol>> {
        return StocksService.apiRest.getStocksSymbols(BuildConfig.EXCHANGE)
    }

    suspend fun getPreviousClosePrice(symbol: String): Response<QuoteResponse> {
        return StocksService.apiRest.getQuote(symbol)
    }

    suspend fun getNews(symbol: String, from: String, to: String): Response<List<News>>{
        return StocksService.apiRest.getNews(symbol, from, to)
    }

}