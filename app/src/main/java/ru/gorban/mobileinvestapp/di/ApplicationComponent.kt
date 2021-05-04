package ru.gorban.mobileinvestapp.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.gorban.mobileinvestapp.ui.fragment.StocksFragment
import ru.gorban.mobileinvestapp.ui.viewModels.StocksViewModelFactory
import javax.inject.Singleton

@Singleton
@Component(modules = [ProvideModule::class, ViewModelModule::class])
interface ApplicationComponent {

    fun viewModuleFactory(): StocksViewModelFactory
    fun context(): Context

    @Component.Builder
        interface Builder {
            @BindsInstance
            fun appContext(context: Context): Builder

            fun build(): ApplicationComponent
        }


    fun inject(fragment: StocksFragment)
}