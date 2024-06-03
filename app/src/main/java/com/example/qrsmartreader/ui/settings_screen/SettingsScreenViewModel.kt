package com.example.qrsmartreader.ui.settings_screen

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.qrsmartreader.App
import com.example.qrsmartreader.domain.entities.CameraRecognitionType
import com.example.qrsmartreader.domain.entities.ProcessorRecognitionType
import com.example.qrsmartreader.ui.interactors.QrResultsInteractor
import com.example.qrsmartreader.ui.interactors.SettingsInteractor
import java.io.IOException
import javax.inject.Inject

class SettingsScreenViewModel(val app: Application) : AndroidViewModel(app) {

    @Inject
    lateinit var qrResultsInteractor: QrResultsInteractor

    @Inject
    lateinit var settingsInteractor: SettingsInteractor

    init {
        App().component.inject(this)
    }

    fun getFilesFromAssetsFolder(): List<String> {
        val list = try {
            app.assets.list("")?.toList() ?: emptyList()
        } catch (e: IOException) {
            emptyList()
        }
       return getModelsName(list)
    }

    private fun getModelsName(list: List<String>): List<String> {
        val modelFiles = list.filter {
            it.contains(
                when (getRecognitionType()) {
                    CameraRecognitionType.Segment -> "segment"
                    CameraRecognitionType.Pose -> "pose"
                }
            )
        }
        val ncnnFiles =
            modelFiles.filter { it.endsWith(".bin") && modelFiles.contains(it.substring(0..it.lastIndex - 3) + "param") }
        return ncnnFiles.map { it.substring(0..it.lastIndex - 4) }
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
    fun changeModelName(name: String) {
        settingsInteractor.changeModelVersionName(name)
    }

}