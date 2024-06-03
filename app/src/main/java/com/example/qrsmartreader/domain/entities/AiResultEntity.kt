package com.example.qrsmartreader.domain.entities

import android.graphics.Bitmap

//todo вообще переделать
data class AiResultEntity(
    val image: Bitmap?,
    val points: FloatArray?,
    val result: List<String>?
    )