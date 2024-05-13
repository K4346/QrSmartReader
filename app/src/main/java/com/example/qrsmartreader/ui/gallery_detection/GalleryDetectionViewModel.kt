package com.example.qrsmartreader.ui.gallery_detection

import android.app.Application
import android.graphics.Bitmap
import android.graphics.PointF
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.qrsmartreader.App
import com.example.qrsmartreader.SingleLiveEvent
import com.example.qrsmartreader.domain.AiResult
import com.example.qrsmartreader.domain.AiScanInteractor
import com.example.qrsmartreader.ui.interactors.QrDecoderInteractor
import javax.inject.Inject

class GalleryDetectionViewModel(val myApplication: Application) : AndroidViewModel(myApplication) {

    val recognisedQrSLE: SingleLiveEvent<String>
    val aiResult: AiResult

    //    todo
    private val aiScanInteractor: AiScanInteractor by lazy { AiScanInteractor(myApplication) }

    @Inject
    lateinit var qrDecoderInteractor: QrDecoderInteractor

    init {
        App().component.inject(this)
        recognisedQrSLE = qrDecoderInteractor.recognisedQrSLE
        aiResult = qrDecoderInteractor.aiResult!!
    }

    fun getNewBitmap(newRationPoints: List<PointF>): Bitmap? {
        if (aiResult == null || aiResult!!.image == null) return null
        val width = aiResult!!.image!!.width
        val height = aiResult!!.image!!.height

        val newPoints = FloatArray(8).mapIndexed { i, fl ->
            if (i % 2 == 0) {
                newRationPoints[i / 2].x * width
            } else {
                newRationPoints[i / 2].y * height
            }
        }.toFloatArray()
        Log.i("kpop attack", newPoints.joinToString { "$it " })
        return aiScanInteractor.performPerspectiveTransformation(aiResult!!.image!!, newPoints)
    }

    fun decodeQR(qr: Bitmap) {
        qrDecoderInteractor.decodeQRCodeAsync(myApplication, qr)
    }
}