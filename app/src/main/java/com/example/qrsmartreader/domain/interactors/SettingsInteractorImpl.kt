package com.example.qrsmartreader.domain.interactors

import com.example.qrsmartreader.App
import com.example.qrsmartreader.data.entities.CameraRecognitionType
import com.example.qrsmartreader.data.entities.ProcessorRecognitionType
import com.example.qrsmartreader.domain.repositories.SettingsRepository
import com.example.qrsmartreader.ui.interactors.SettingsInteractor
import javax.inject.Inject


class SettingsInteractorImpl : SettingsInteractor {
    @Inject
    lateinit var repository: SettingsRepository

    init {
        App().component.inject(this)
    }


    override fun getModelType() = repository.getRecognitionType()

    override fun changeModelType(type: CameraRecognitionType) {
        repository.changeRecognitionType(type)
    }

    override fun getProcessorType() = repository.getProcessorType()

    override fun changeProcessorType(type: ProcessorRecognitionType) {
        repository.changeProcessorType(type)
    }

    override fun getModelVersionName() = repository.getModelVersionName()

    override fun changeModelVersionName(name: String) {
        repository.changeModelVersionName(name)
    }

    override fun getFpsCountIsVisible() = repository.getFpsCountIsVisible()

    override fun changeFpsCountVisibility(flag: Boolean) {
        repository.changeFpsCountVisibility(flag)
    }

    override fun getHistoryLimit() = repository.getHistoryLimit()

    override fun changeMaxCountResults(maxResults: Int) {
        repository.changeHistoryLimit(maxResults)
    }
}