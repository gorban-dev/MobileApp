package ru.gorban.mobileinvestapp.data.repository


import androidx.paging.PagingSource
import ru.gorban.mobileinvestapp.data.db.CompanyProfileDao
import ru.gorban.mobileinvestapp.data.entity.CompanyProfile
import ru.gorban.mobileinvestapp.data.entity.Symbol

class LocalCompanyRepository(private val dao: CompanyProfileDao) {

    /**
     * Выполнение запросов к локальной базе данных.
     */
    suspend fun insert(companyProfile: CompanyProfile) {
        return dao.insertCompanyProfile(companyProfile)
    }

    suspend fun insertAllCompanyList(companyList: List<CompanyProfile>) {
        dao.insertAllCompanyList(companyList)
    }

    suspend fun updateCompanyProfile(companyProfile: CompanyProfile) {
        return dao.updateCompanyProfile(companyProfile)
    }

    fun updateFavoriteStatus(isFavorite: Boolean, ticker: String) {
        dao.updateFavoriteStatus(isFavorite, ticker)
    }

    suspend fun updateCurrentPriceByTicker(price: Double, ticker: String) {
        dao.updateCurrentPriceByTicker(price, ticker)
    }

    fun updatePreviousPriceByTicker(price: Double, ticker: String) {
        dao.updatePreviousPriceByTicker(price, ticker)
    }

    fun updateTimeByTicker(time: Long, ticker: String) {
        dao.updateTimeByTicker(time, ticker)
    }

    suspend fun delete(companyProfile: CompanyProfile) {
        return dao.deleteCompanyProfile(companyProfile)
    }

    suspend fun deleteAll() {
        return dao.deleteAll()
    }

    suspend fun getCompanyProfileByTicker(ticker: String): CompanyProfile {
        return dao.getCompanyProfileByTicker(ticker)
    }

    suspend fun getAllStocksName(): List<Symbol> {
        return dao.getAllStocksName()
    }

    fun getCompanyPaging(): PagingSource<Int, CompanyProfile> {
        return dao.getCompanyPaging()
    }

    fun getFavoriteCompanyPaging(): PagingSource<Int, CompanyProfile> {
        return dao.getFavoriteCompanyPaging()
    }
}