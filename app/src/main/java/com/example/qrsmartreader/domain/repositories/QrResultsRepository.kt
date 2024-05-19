package com.example.qrsmartreader.domain.repositories

import androidx.lifecycle.LiveData
import com.example.qrsmartreader.domain.entities.QrResultEntity

interface QrResultsRepository {

    fun getAllResultsFromBdAsync(): LiveData<List<QrResultEntity>>

    fun getAllResultsFromBd(): List<QrResultEntity>

    fun insertResultInDb(result: QrResultEntity)

    fun updateResultInDb(result: QrResultEntity)

    fun deleteResultInDb(result: QrResultEntity)
}