package com.example.qrsmartreader.di

import android.app.Application
import com.example.qrsmartreader.MainActivity
import com.example.qrsmartreader.data.repositories.QrResultsRepositoryImpl
import com.example.qrsmartreader.data.repositories.SettingsRepositoryImpl
import com.example.qrsmartreader.domain.interactors.QrDecoderInteractorImpl
import com.example.qrsmartreader.domain.interactors.QrResultsInteractorImpl
import com.example.qrsmartreader.domain.interactors.SettingsInteractorImpl
import com.example.qrsmartreader.ui.camera_detection_screen.CameraDetectionViewModel
import com.example.qrsmartreader.ui.gallery_detection.GalleryDetectionViewModel
import com.example.qrsmartreader.ui.result_screen.ResultScreenViewModel
import com.example.qrsmartreader.ui.settings_screen.SettingsScreenViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [SharedPreferencesModule::class, DatabaseModule::class, RepositoriesModule::class, InteractorsModule::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun application(application: Application): Builder
    }

    fun inject(activity: MainActivity)
    fun inject(interactor: QrResultsInteractorImpl)
    fun inject(interactor: SettingsInteractorImpl)
    fun inject(interactor: QrDecoderInteractorImpl)
    fun inject(viewModel: ResultScreenViewModel)
    fun inject(viewModel: SettingsScreenViewModel)
    fun inject(viewModel: GalleryDetectionViewModel)
    fun inject(viewModel: CameraDetectionViewModel)
    fun inject(repository: SettingsRepositoryImpl)
    fun inject(repository: QrResultsRepositoryImpl)
}