package com.example.qrsmartreader.ui.result_screen

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.qrsmartreader.App
import com.example.qrsmartreader.SingleLiveEvent
import com.example.qrsmartreader.domain.DataProcess
import com.example.qrsmartreader.domain.entities.AiResultEntity
import com.example.qrsmartreader.domain.entities.QrResultEntity
import com.example.qrsmartreader.ui.interactors.AiScanOnnxInteractor
import com.example.qrsmartreader.ui.interactors.QrDecoderInteractor
import com.example.qrsmartreader.ui.interactors.QrResultsInteractor
import javax.inject.Inject

class ResultScreenViewModel(val app: Application) : AndroidViewModel(app) {

    val recognisedQrSLE: SingleLiveEvent<String>
    var sourceBitmap: Bitmap? = null

    //    todo стоит от него избавиться и передавать в функциях
    lateinit var resizedBitmap: Bitmap

    @Inject
    lateinit var aiScanOnnxInteractor: AiScanOnnxInteractor

    //    todo dagger
    @Inject
    lateinit var qrResultsInteractor: QrResultsInteractor

    @Inject
    lateinit var qrDecoderInteractor: QrDecoderInteractor

    val aiRecognisedQrSLE = SingleLiveEvent<AiResultEntity>()

    init {
        App().component.inject(this)
        recognisedQrSLE = qrDecoderInteractor.recognisedQrSLE
    }

    fun scanQr() {
        resizedBitmap = DataProcess.imageToBitmap(sourceBitmap!!)
//        todo сделать интерфейс для того чтоб использовать 1метод
        qrDecoderInteractor.decodeQRCodeAsync(app, resizedBitmap)
    }

    fun scanAiQr() {
        resizedBitmap = DataProcess.imageToBitmap(sourceBitmap!!)
        val points = aiScanOnnxInteractor.findQrPoints(resizedBitmap)
        if (points == null) {
            aiRecognisedQrSLE.value = AiResultEntity(sourceBitmap, points, null)
            return
        }
        points.forEach {
            Log.i("kpopdots_ai", it.toString())
        }

        aiRecognisedQrSLE.value = AiResultEntity(sourceBitmap, points, null)
    }

    fun getHistory(): LiveData<List<QrResultEntity>> {
        return qrResultsInteractor.initHistory(app = app)
    }

    fun prepareResultsIfNeeded(qrResults: List<QrResultEntity>) {
        return qrResultsInteractor.processingHistory(app = app, qrResults)
    }

    fun setAiResult(it: AiResultEntity) {
        DataProcess.aiResult = it
    }
}