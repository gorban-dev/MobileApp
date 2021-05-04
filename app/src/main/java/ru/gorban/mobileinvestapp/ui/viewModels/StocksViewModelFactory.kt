package ru.gorban.mobileinvestapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException
import javax.inject.Inject
import javax.inject.Provider

/**
 * Own implementation of ViewModelProvider.Factory to create ViewModelMain instance.
 */
class StocksViewModelFactory@Inject constructor(
    private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModelProvider = viewModels[modelClass]
            ?: throw IllegalArgumentException("ViewModel $modelClass not found")
        return viewModelProvider.get() as T
    }
}
