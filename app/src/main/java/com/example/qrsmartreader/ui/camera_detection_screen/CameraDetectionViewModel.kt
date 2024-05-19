package com.example.qrsmartreader.ui.camera_detection_screen

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import com.example.qrsmartreader.App
import com.example.qrsmartreader.SingleLiveEvent
import com.example.qrsmartreader.domain.entities.CameraRecognitionType
import com.example.qrsmartreader.domain.entities.ProcessorRecognitionType
import com.example.qrsmartreader.domain.entities.QrResultEntity
import com.example.qrsmartreader.ui.interactors.QrDecoderInteractor
import com.example.qrsmartreader.ui.interactors.SettingsInteractor
import javax.inject.Inject

class CameraDetectionViewModel(val myApplication: Application) : AndroidViewModel(myApplication) {

    @Inject
    lateinit var interactor: SettingsInteractor
    @Inject
    lateinit var qrDecoderInteractor: QrDecoderInteractor

    val recognisedQrSLE:SingleLiveEvent<String>

    init {
        App().component.inject(this)

        recognisedQrSLE = qrDecoderInteractor.recognisedQrSLE
    }

    val isFpsCounting: Boolean by lazy { interactor.getFpsCountIsVisible() }
    val processorType: ProcessorRecognitionType by lazy { interactor.getProcessorType() }
    val modelType: CameraRecognitionType by lazy { interactor.getModelType() }
    var currentImage: IntArray = intArrayOf()

    fun decodeQr(bitmap: Bitmap) {
//        todo убрать App!!!
        qrDecoderInteractor.decodeQRCodeAsync(myApplication,bitmap)
    }

}