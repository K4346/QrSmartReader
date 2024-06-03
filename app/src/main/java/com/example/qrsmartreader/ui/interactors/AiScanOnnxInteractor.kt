package com.example.qrsmartreader.ui.interactors

import android.graphics.Bitmap

interface AiScanOnnxInteractor {
    fun findQrPoints(image: Bitmap): FloatArray?
    fun performPerspectiveTransformation(source: Bitmap, srcPoints: FloatArray): Bitmap?
}