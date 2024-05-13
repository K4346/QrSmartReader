package com.example.qrsmartreader.data.repositories

import androidx.lifecycle.LiveData
import com.example.qrsmartreader.App
import com.example.qrsmartreader.data.db.QrResultsDao
import com.example.qrsmartreader.data.entities.QrResultEntity
import com.example.qrsmartreader.domain.repositories.QrResultsRepository
import javax.inject.Inject

class QrResultsRepositoryImpl : QrResultsRepository {

    @Inject
    lateinit var dao: QrResultsDao

    init {
        App().component.inject(this)
    }

    override fun getAllResultsFromBdAsync(): LiveData<List<QrResultEntity>> {
        return dao.getAllAsync()
    }

    override fun getAllResultsFromBd(): List<QrResultEntity> {
        return dao.getAll()
    }

    override fun insertResultInDb(result: QrResultEntity) {
        return dao.insert(result)
    }

    override fun updateResultInDb(result: QrResultEntity) {
        return dao.update(result)
    }

    override fun deleteResultInDb(result: QrResultEntity) {
        return dao.delete(result)
    }

}