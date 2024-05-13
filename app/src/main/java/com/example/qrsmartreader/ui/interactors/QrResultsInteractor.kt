package com.example.qrsmartreader.ui.interactors

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.qrsmartreader.data.entities.QrResultEntity

interface QrResultsInteractor {
    fun initHistory(app: Application): LiveData<List<QrResultEntity>>
    fun processingHistory(app: Application, history: List<QrResultEntity>)
    fun addResultToHistory(app: Application, res: String)
    fun clearQrResults(app: Application)

    fun removeExcessQrResultsFromDb()
}
