package ru.gorban.mobileinvestapp.data.db

import androidx.paging.PagingSource
import androidx.room.*

import ru.gorban.mobileinvestapp.data.entity.CompanyProfile
import ru.gorban.mobileinvestapp.data.entity.Symbol

@Dao
interface CompanyProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyProfile(companyProfile: CompanyProfile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCompanyList(companyList: List<CompanyProfile>)

    @Update
    suspend fun updateCompanyProfile(companyProfile: CompanyProfile)

    @Query("UPDATE stocks_data_table SET isFavorite=:isFavorite WHERE stocks_ticker = :ticker")
    fun updateFavoriteStatus(isFavorite: Boolean, ticker: String)

    @Query("UPDATE stocks_data_table SET stocks_price=:price WHERE stocks_ticker = :ticker")
    suspend fun updateCurrentPriceByTicker(price: Double, ticker: String)

    @Query("UPDATE stocks_data_table SET close_price=:price WHERE stocks_ticker = :ticker")
    fun updatePreviousPriceByTicker(price: Double, ticker: String)

    @Query("UPDATE stocks_data_table SET time_update=:time WHERE stocks_ticker = :ticker")
    fun updateTimeByTicker(time: Long, ticker: String)

    @Delete
    suspend fun deleteCompanyProfile(companyProfile: CompanyProfile)

    @Query(" DELETE FROM stocks_data_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM stocks_data_table")
    fun getCompanyPaging() : PagingSource<Int, CompanyProfile>

    @Query("SELECT * FROM stocks_data_table WHERE stocks_ticker = :ticker")
    suspend fun getCompanyProfileByTicker(ticker: String) : CompanyProfile

    @Query("SELECT stocks_ticker, stocks_name FROM stocks_data_table")
    suspend fun getAllStocksName() : List<Symbol>

    @Query("SELECT * FROM stocks_data_table WHERE isFavorite = 1")
    fun getFavoriteCompanyPaging() :  PagingSource<Int, CompanyProfile>
}