package com.example.qrsmartreader.di

import com.example.qrsmartreader.data.repositories.QrResultsRepositoryImpl
import com.example.qrsmartreader.data.repositories.SettingsRepositoryImpl
import com.example.qrsmartreader.domain.repositories.QrResultsRepository
import com.example.qrsmartreader.domain.repositories.SettingsRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoriesModule {
    @Provides
    @Singleton
    fun provideExchangeRateInfoRepository(): QrResultsRepository {
        return QrResultsRepositoryImpl()
    }
    @Provides
    @Singleton
    fun provideSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl()
    }

}