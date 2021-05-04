package ru.gorban.mobileinvestapp

import android.app.Application
import ru.gorban.mobileinvestapp.di.DaggerApplicationComponent

class App: Application() {


    override fun onCreate() {
        super.onCreate()
        initDI()
    }

    private fun initDI() {
       DI.applicationComponent = DaggerApplicationComponent.builder()
           .appContext(this)
           .build()
    }
}