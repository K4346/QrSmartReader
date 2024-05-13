package com.example.qrsmartreader.domain

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

//todo вообще переделать
data class AiResult(
    val image: Bitmap?,
    val points: FloatArray?,
    val result: List<String>?
    )