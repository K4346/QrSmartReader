package com.example.qrsmartreader.ui.interactors

import com.example.qrsmartreader.data.entities.CameraRecognitionType
import com.example.qrsmartreader.data.entities.ProcessorRecognitionType

interface SettingsInteractor {


    fun getFpsCountIsVisible(): Boolean
    fun changeFpsCountVisibility(flag: Boolean)

    fun getHistoryLimit(): Int
    fun changeMaxCountResults(maxResults: Int)

    fun getModelType(): CameraRecognitionType
    fun changeModelType(type: CameraRecognitionType)

    fun getProcessorType():ProcessorRecognitionType
    fun changeProcessorType(type: ProcessorRecognitionType)

    fun getModelVersionName(): String
    fun changeModelVersionName(name: String)
}