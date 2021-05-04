package ru.gorban.mobileinvestapp.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.gorban.mobileinvestapp.ui.viewModels.ViewModelKey
import ru.gorban.mobileinvestapp.ui.viewModels.ViewModelMain

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ViewModelMain::class)
    abstract fun provideStocksViewModel(viewModelMain: ViewModelMain): ViewModel
}