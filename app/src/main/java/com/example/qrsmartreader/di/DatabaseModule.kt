package com.example.qrsmartreader.di

import android.app.Application
import com.example.qrsmartreader.data.db.QrResultsDao
import com.example.qrsmartreader.data.db.QrResultsDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDao(context: Application): QrResultsDao {
        return QrResultsDatabase.getInstance(context).qrResultsDao()
    }
}