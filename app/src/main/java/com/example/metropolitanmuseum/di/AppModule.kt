package com.example.metropolitanmuseum.di

import androidx.room.Room
import com.example.metropolitanmuseum.data.local.AppDatabase
import com.example.metropolitanmuseum.data.remote.RetrofitClient
import com.example.metropolitanmuseum.data.repository.MuseumRepository
import com.example.metropolitanmuseum.ui.viewmodels.DetailViewModel
import com.example.metropolitanmuseum.ui.viewmodels.FavoritesViewModel
import com.example.metropolitanmuseum.ui.viewmodels.MainScreenViewModel
import com.example.metropolitanmuseum.ui.viewmodels.SearchViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val appModule = module {
    // API
    single { RetrofitClient.createApiService() }

    // Database
    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "museum-database"
        ).build()
    }

    single { get<AppDatabase>().favoritesDao() }

    // Repository
    single { MuseumRepository(get(), get()) }

    // ViewModels
    viewModelOf(::MainScreenViewModel)
    viewModel { SearchViewModel(get()) }
    viewModel { params -> DetailViewModel(get(), params.get()) }
    viewModel { FavoritesViewModel(get()) }
}