package com.example.qrsmartreader.di

import android.app.Application
import com.example.qrsmartreader.domain.interactors.AiScanOnnxInteractorImpl
import com.example.qrsmartreader.domain.interactors.QrDecoderInteractorImpl
import com.example.qrsmartreader.domain.interactors.QrResultsInteractorImpl
import com.example.qrsmartreader.domain.interactors.SettingsInteractorImpl
import com.example.qrsmartreader.ui.interactors.AiScanOnnxInteractor
import com.example.qrsmartreader.ui.interactors.QrDecoderInteractor
import com.example.qrsmartreader.ui.interactors.QrResultsInteractor
import com.example.qrsmartreader.ui.interactors.SettingsInteractor
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class InteractorsModule {
    @Provides
    @Singleton
    fun provideQrResultsInteractor(): QrResultsInteractor {
        return QrResultsInteractorImpl()
    }

    @Provides
    @Singleton
    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl()
    }

    @Provides
    @Singleton
    fun provideQrDecoderInteractor(): QrDecoderInteractor {
        return QrDecoderInteractorImpl()
    }

    @Provides
    @Singleton
    fun provideAiScanOnnxInteractor(context: Application): AiScanOnnxInteractor {
        return AiScanOnnxInteractorImpl(context)
    }

}