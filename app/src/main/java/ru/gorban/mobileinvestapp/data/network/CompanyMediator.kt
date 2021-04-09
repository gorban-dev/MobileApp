package ru.gorban.mobileinvestapp.data.network

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import retrofit2.HttpException
import ru.gorban.mobileinvestapp.data.entity.CompanyProfile
import ru.gorban.mobileinvestapp.data.repository.DefaultListSymbols
import ru.gorban.mobileinvestapp.data.repository.LocalCompanyRepository
import ru.gorban.mobileinvestapp.data.repository.RemoteCompanyRepository
import java.io.IOException


@ExperimentalPagingApi
class CompanyMediator(
    private val localCompanyRepository: LocalCompanyRepository,
    private val remoteCompanyRepository: RemoteCompanyRepository
) : RemoteMediator<Int, CompanyProfile>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CompanyProfile>
    ): MediatorResult {

        return try {
            val localListSymbols = localCompanyRepository.getAllStocksName()
            if (localListSymbols.isEmpty()){
                val list = DefaultListSymbols.ListSymbols
                val response = remoteCompanyRepository.getStocksSymbols()
                if (response.isSuccessful) {
                    val filterList = response.body()?.filter { list.contains(it.displaySymbol) }
                    val listCompany = arrayListOf<CompanyProfile>()
                    filterList?.forEach {
                        listCompany.add(CompanyProfile(name = it.description, ticker = it.displaySymbol))
                    }
                    if (!listCompany.isNullOrEmpty()) {
                        localCompanyRepository.insertAllCompanyList(listCompany)
                    }
                }
            }
            MediatorResult.Success (endOfPaginationReached = true)
        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        }
    }
}