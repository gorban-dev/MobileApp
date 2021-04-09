package ru.gorban.mobileinvestapp.ui.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.cachedIn
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import ru.gorban.mobileinvestapp.data.db.StocksDatabase
import ru.gorban.mobileinvestapp.data.entity.CompanyProfile
import ru.gorban.mobileinvestapp.data.entity.News
import ru.gorban.mobileinvestapp.data.entity.RespCompany
import ru.gorban.mobileinvestapp.data.entity.UserSearchHistory
import ru.gorban.mobileinvestapp.data.network.WebServicesProvider
import ru.gorban.mobileinvestapp.data.repository.RemoteCompanyRepository
import ru.gorban.mobileinvestapp.data.repository.LocalCompanyRepository
import ru.gorban.mobileinvestapp.data.repository.SocketRepository
import ru.gorban.mobileinvestapp.data.repository.UserSearchRepository
import kotlin.Exception

class ViewModelMain(application: Application) : AndroidViewModel(application) {


    private val dataBase: StocksDatabase = StocksDatabase.getInstance(application)
    private val remoteRepo: RemoteCompanyRepository = RemoteCompanyRepository()
    private var _userSearchRepository = UserSearchRepository(dataBase.userSearchDao())
    private val userSearchRepository get() = _userSearchRepository
    private var _localCompanyRepository: LocalCompanyRepository = LocalCompanyRepository(dataBase.companyProfileDao())
    private val localCompanyRepository get() = _localCompanyRepository
    private val repository = RespCompany(localCompanyRepository, remoteRepo)
    private var _companyNews: MutableLiveData<List<News>> = MutableLiveData()
    val companyNews get() = _companyNews
    var socketRepository: SocketRepository = SocketRepository(WebServicesProvider())

   //TODO Проработать логику записи в базу обновленной цены (при активных торгах слишком много обновлений приходит)
    fun subscribeToSocketEvents() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                delay(10000)
                socketRepository.startSocket().collectLatest {
                    delay(900)
                    it.forEach { trade ->
                        val updateTicker = trade.ticker
                        val updatePrice = trade.lastPrice
                        if (updateTicker != null && updatePrice != null) {
                            localCompanyRepository.updateCurrentPriceByTicker(updatePrice, updateTicker)
                        }
                    }
                }
            }
        } catch (ex: java.lang.Exception) {
            Log.e("MySocket", "Socket was failure with $ex")
        }
    }

    @ExperimentalPagingApi
    fun getCompanyList() = repository.getResult().cachedIn(viewModelScope)

    fun getFavoriteCompany() = repository.getFavoriteCompany().cachedIn(viewModelScope)

    fun getUserSearchRequest() = userSearchRepository.getAllRequest()

    fun addUserRequest(userRequest: UserSearchHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            userSearchRepository.insert(userRequest)
        }
    }

    fun deleteOldUserRequest(userRequest: UserSearchHistory){
        viewModelScope.launch(Dispatchers.IO) {
            userSearchRepository.delete(userRequest)
        }
    }

    fun deleteSearchHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            userSearchRepository.deleteAllRequest()
        }
    }

    fun getNews(ticker: String, dateFrom: String, dateTo: String) {
        viewModelScope.launch {
            try {
                val responseNews = remoteRepo.getNews(ticker, dateFrom, dateTo)
                if (responseNews.isSuccessful) {
                   _companyNews.value = responseNews.body()
                }
            } catch (ex: Exception) {
                Log.e("ViewModelMain", "getNews fun with error $ex")
                _companyNews.value = emptyList()
            }
        }
    }


    /**
     * Первичня инициализация профиля компании.
     * При отсутствии торгов происходит проверка актуальности цены закрытия и ее обновление.
     */
    fun initCompanyProfile(ticker: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val localCompanyProfile = localCompanyRepository.getCompanyProfileByTicker(ticker)
                if (localCompanyProfile.time == 0L) {
                    val responseQuote = remoteRepo.getPreviousClosePrice(ticker)
                    if (responseQuote.isSuccessful) {
                        val responseCompanyProfile = remoteRepo.getCompanyProfile(ticker)
                        if (responseCompanyProfile.isSuccessful) {
                            val updateCompanyProfile = responseCompanyProfile.body()!!
                            val updatePrice = responseQuote.body()!!
                            localCompanyProfile.apply {
                                currency = updateCompanyProfile.currency
                                finnhubIndustry = updateCompanyProfile.finnhubIndustry
                                weburl = updateCompanyProfile.weburl
                                ipo = updateCompanyProfile.ipo
                                logo = updateCompanyProfile.logo
                                phone = updateCompanyProfile.phone.substringBeforeLast('.')
                                close_price = updatePrice.pc
                                stocks_price = updatePrice.c
                                time = updatePrice.t
                            }
                            localCompanyRepository.updateCompanyProfile(localCompanyProfile)
                        }
                    }
                } else {
                    val responseQuote = remoteRepo.getPreviousClosePrice(ticker)
                    if (responseQuote.isSuccessful) {
                        val updatePrice = responseQuote.body()!!
                        if (localCompanyProfile.time != updatePrice.t) {
                            localCompanyRepository.updateTimeByTicker(updatePrice.t, ticker)
                            localCompanyRepository.updateCurrentPriceByTicker(updatePrice.c, ticker)
                            localCompanyRepository.updatePreviousPriceByTicker(
                                updatePrice.pc,
                                ticker
                            )
                        }
                    }
                }
            } catch (ex: Exception) {
                Log.e("ViewModelMain", "initCompanyProfile failed with error: $ex")
            }
        }
    }

    fun updateFavoriteStatus(companyProfile: CompanyProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            localCompanyRepository.updateFavoriteStatus(
                companyProfile.isFavorite,
                companyProfile.ticker
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("MySocket", "Socket connection was closed from fun onCleared")
        socketRepository.closeSocket()
    }
}