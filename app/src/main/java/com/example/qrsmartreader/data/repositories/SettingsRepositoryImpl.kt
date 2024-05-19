package com.example.qrsmartreader.data.repositories

import android.app.Application
import com.example.qrsmartreader.App
import com.example.qrsmartreader.data.db.SettingsManager
import com.example.qrsmartreader.domain.entities.CameraRecognitionType
import com.example.qrsmartreader.domain.entities.ProcessorRecognitionType
import com.example.qrsmartreader.domain.repositories.SettingsRepository
import javax.inject.Inject

class SettingsRepositoryImpl: SettingsRepository {
    @Inject
    lateinit var settingsManager: SettingsManager

    init {
        App().component.inject(this)
    }
    override fun initialize(application: Application) {
        settingsManager.initialize(application)
    }

    override fun getFpsCountIsVisible() =
        settingsManager.fpsCountVisible

    override fun changeFpsCountVisibility(flag: Boolean) {
        settingsManager.fpsCountVisible = flag
    }

    override fun getHistoryLimit(): Int = settingsManager.historyCountLimitation

    override fun changeHistoryLimit(maxResults: Int) {
        settingsManager.historyCountLimitation = maxResults
    }

    override fun getRecognitionType() =
        CameraRecognitionType.valueOf(settingsManager.modelType)


    override fun changeRecognitionType(type: CameraRecognitionType) {
        settingsManager.modelType = type.name
    }

    override fun getProcessorType(): ProcessorRecognitionType {
        return ProcessorRecognitionType.valueOf(settingsManager.processorValue)
    }

    override fun changeProcessorType(type: ProcessorRecognitionType) {
        settingsManager.processorValue = type.name
    }

    override fun getModelVersionName() = settingsManager.modelVersionName

    override fun changeModelVersionName(name: String) {
        settingsManager.modelVersionName = name
    }

}