package com.example.qrsmartreader.ui.settings_screen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.qrsmartreader.App
import com.example.qrsmartreader.data.entities.CameraRecognitionType
import com.example.qrsmartreader.data.entities.ProcessorRecognitionType
import com.example.qrsmartreader.ui.interactors.QrResultsInteractor
import com.example.qrsmartreader.ui.interactors.SettingsInteractor
import javax.inject.Inject

class SettingsScreenViewModel(val app: Application) : AndroidViewModel(app) {

    @Inject
    lateinit var qrResultsInteractor: QrResultsInteractor
    @Inject
    lateinit var settingsInteractor: SettingsInteractor

    init {
        App().component.inject(this)
    }

    fun clearHistory() {
        qrResultsInteractor.clearQrResults(app)
    }

    fun changeFpsCounterVisible(flag: Boolean) {
        settingsInteractor.changeFpsCountVisibility(flag)
    }
  fun getFpsCounterVisible() = settingsInteractor.getFpsCountIsVisible()

    fun changeMaxResults(maxCount: Int) {
        settingsInteractor.changeMaxCountResults(maxCount)
        qrResultsInteractor.removeExcessQrResultsFromDb()
    }

    fun getMaxResultsCount() = settingsInteractor.getHistoryLimit()

    fun changeProcessorType(processor: ProcessorRecognitionType) {
            settingsInteractor.changeProcessorType(processor)
    }

    fun getProcessor() = settingsInteractor.getProcessorType()

    fun changeRecognitionType(type: CameraRecognitionType) {
            settingsInteractor.changeModelType(type)
    }

    fun getRecognitionType() = settingsInteractor.getModelType()

}