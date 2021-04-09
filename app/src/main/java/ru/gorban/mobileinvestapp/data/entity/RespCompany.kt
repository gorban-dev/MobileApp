package ru.gorban.mobileinvestapp.data.entity

import androidx.paging.*
import ru.gorban.mobileinvestapp.data.network.CompanyMediator
import ru.gorban.mobileinvestapp.data.repository.LocalCompanyRepository
import ru.gorban.mobileinvestapp.data.repository.RemoteCompanyRepository

class RespCompany(
    private val localRepository: LocalCompanyRepository,
    private val remoteRepository: RemoteCompanyRepository
){

    @ExperimentalPagingApi
    fun getResult() =
        Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false,
                prefetchDistance = 15

            ),
            remoteMediator = CompanyMediator(localRepository,remoteRepository),
            pagingSourceFactory = { localRepository.getCompanyPaging()}
        ).flow


    fun getFavoriteCompany() =
        Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false,
                prefetchDistance = 15
            ),
            pagingSourceFactory = {localRepository.getFavoriteCompanyPaging()}
        ).flow
}
