package com.example.qrsmartreader.domain.repositories

import android.app.Application
import com.example.qrsmartreader.data.entities.CameraRecognitionType
import com.example.qrsmartreader.data.entities.ProcessorRecognitionType

interface SettingsRepository {
    fun initialize(application: Application)


    fun getFpsCountIsVisible(): Boolean
    fun changeFpsCountVisibility(flag: Boolean)

    fun getHistoryLimit(): Int
    fun changeHistoryLimit(maxResults: Int)

    fun getRecognitionType(): CameraRecognitionType
    fun changeRecognitionType(type: CameraRecognitionType)

    fun getProcessorType(): ProcessorRecognitionType
    fun changeProcessorType(type: ProcessorRecognitionType)

    fun getModelVersionName(): String
    fun changeModelVersionName(name: String)
}