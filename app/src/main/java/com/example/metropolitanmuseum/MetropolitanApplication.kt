package com.example.metropolitanmuseum

import android.app.Application
import com.example.metropolitanmuseum.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MetropolitanApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MetropolitanApplication)
            modules(appModule)
        }
    }
}