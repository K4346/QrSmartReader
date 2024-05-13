package com.example.qrsmartreader.di

import android.app.Application
import android.content.Context
import com.example.qrsmartreader.App
import com.example.qrsmartreader.data.db.SettingsManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class SharedPreferencesModule {
    @Provides
    @Singleton
    fun provideSettingsManager(context: Application): SettingsManager {
        val settingsManager = SettingsManager()
        settingsManager.initialize(context)
        return settingsManager
    }
}