package com.example.firstcursorapp.di

import android.content.Context
import com.example.firstcursorapp.data.firebase.AuthService
import com.example.firstcursorapp.data.firebase.NotificationService
import com.example.firstcursorapp.data.local.NewsDatabase
import com.example.firstcursorapp.data.remote.NetworkModule
import com.example.firstcursorapp.data.repository.NewsRepository
import com.example.firstcursorapp.data.repository.FavoritesRepository
import com.example.firstcursorapp.data.repository.AnalyticsRepository
import com.example.firstcursorapp.utils.NetworkMonitor
import com.example.firstcursorapp.feature.analytics.AnalyticsViewModel
import com.example.firstcursorapp.feature.auth.AuthViewModel
import com.example.firstcursorapp.feature.favorites.FavoritesViewModel
import com.example.firstcursorapp.feature.home.HomeViewModel
import com.example.firstcursorapp.feature.login.LoginViewModel
import com.example.firstcursorapp.feature.main.MainViewModel
import com.example.firstcursorapp.feature.settings.SettingsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.work.WorkManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Database
    single { NewsDatabase.getDatabase(androidContext()) }
    single { get<NewsDatabase>().userProfileDao() }
    single { get<NewsDatabase>().analyticsDao() }
    
    // Firebase
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { AuthService(get()) }
    single { NotificationService(androidContext()) }
    single { FavoritesRepository(get(), get()) }
    single { AnalyticsRepository(get()) }
    
    // Network
    single { NetworkModule.provideNewsApiService() }
    single { NewsRepository(get()) }
    
    // Offline sync removed
    
    // Work Manager
    single { WorkManager.getInstance(androidContext()) }
    
    // Network Monitor
    single { NetworkMonitor(androidContext()) }
    
    
    // ViewModels
    viewModel { HomeViewModel(repository = get(), networkMonitor = get(), apiKey = "pub_c81a0cb2abbd433e9d65de06441b8021") }
    viewModel { MainViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
    viewModel { LoginViewModel() }
    viewModel { SettingsViewModel(androidContext() as android.app.Application) }
    viewModel { AnalyticsViewModel(get(), get()) }
    viewModel { AuthViewModel(get(), get()) }
    // Offline feature removed
}


