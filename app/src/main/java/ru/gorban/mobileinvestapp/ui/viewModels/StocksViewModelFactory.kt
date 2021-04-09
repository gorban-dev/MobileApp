package ru.gorban.mobileinvestapp.ui.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

/**
 * Own implementation of ViewModelProvider.Factory to create ViewModelMain instance.
 */
class StocksViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelMain::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ViewModelMain(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}