package com.example.qrsmartreader.ui.interactors

import android.app.Application
import android.graphics.Bitmap
import com.example.qrsmartreader.SingleLiveEvent
import com.example.qrsmartreader.domain.AiResult

interface QrDecoderInteractor {
    fun decodeQRCodeAsync(app: Application, bitmap: Bitmap)

    var aiResult: AiResult?
    val recognisedQrSLE: SingleLiveEvent<String>
}