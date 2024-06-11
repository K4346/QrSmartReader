package com.example.qrsmartreader.ui.gallery_detection

import android.app.Application
import android.graphics.Bitmap
import android.graphics.PointF
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.qrsmartreader.App
import com.example.qrsmartreader.SingleLiveEvent
import com.example.qrsmartreader.domain.DataProcess
import com.example.qrsmartreader.domain.entities.AiResultEntity
import com.example.qrsmartreader.ui.interactors.AiScanOnnxInteractor
import com.example.qrsmartreader.ui.interactors.QrDecoderInteractor
import javax.inject.Inject

class GalleryDetectionViewModel(private val myApplication: Application) : AndroidViewModel(myApplication) {

    val recognisedQrSLE: SingleLiveEvent<String>
    val aiResult: AiResultEntity

    @Inject
    lateinit var aiScanOnnxInteractor: AiScanOnnxInteractor

    @Inject
    lateinit var qrDecoderInteractor: QrDecoderInteractor

    init {
        App().component.inject(this)
        recognisedQrSLE = qrDecoderInteractor.recognisedQrSLE
        aiResult = DataProcess.aiResult!!
    }

    fun getNewBitmap(newRationPoints: List<PointF>): Bitmap? {
        if (aiResult.image == null) return null
        val width = aiResult.image!!.width
        val height = aiResult.image!!.height

        val newPoints = FloatArray(8).mapIndexed { i, fl ->
            if (i % 2 == 0) {
                newRationPoints[i / 2].x * width
            } else {
                newRationPoints[i / 2].y * height
            }
        }.toFloatArray()
        return aiScanOnnxInteractor.performPerspectiveTransformation(aiResult.image!!, newPoints)
    }

    fun decodeQR(qr: Bitmap) {
        qrDecoderInteractor.decodeQRCodeAsync(myApplication, qr)
    }
}