package ru.gorban.mobileinvestapp.di

import dagger.Module
import dagger.Provides
import ru.gorban.mobileinvestapp.DI
import ru.gorban.mobileinvestapp.data.db.StocksDatabase
import ru.gorban.mobileinvestapp.data.entity.RespCompany
import ru.gorban.mobileinvestapp.data.network.WebServicesProvider
import ru.gorban.mobileinvestapp.data.repository.LocalCompanyRepository
import ru.gorban.mobileinvestapp.data.repository.RemoteCompanyRepository
import ru.gorban.mobileinvestapp.data.repository.SocketRepository
import ru.gorban.mobileinvestapp.data.repository.UserSearchRepository

@Module
class ProvideModule {

    @Provides
    fun provideRemoteCompanyRepository() = RemoteCompanyRepository()
    @Provides
    fun provideUserSearchRepository(database: StocksDatabase) = UserSearchRepository(database.userSearchDao())
    @Provides
    fun provideDatabase(): StocksDatabase = StocksDatabase.getInstance(DI.applicationComponent.context())
    @Provides
    fun provideLocalCompanyRepository(database: StocksDatabase) = LocalCompanyRepository(database.companyProfileDao())
    @Provides
    fun provideRespCompany(local: LocalCompanyRepository, remote: RemoteCompanyRepository)= RespCompany(local,remote)
    @Provides
    fun provideSocketRepository() = SocketRepository(WebServicesProvider())
}
