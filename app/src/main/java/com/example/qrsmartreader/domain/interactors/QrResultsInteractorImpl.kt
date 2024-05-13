package com.example.qrsmartreader.domain.interactors

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.qrsmartreader.App
import com.example.qrsmartreader.data.entities.QrResultEntity
import com.example.qrsmartreader.domain.repositories.QrResultsRepository
import com.example.qrsmartreader.domain.repositories.SettingsRepository
import com.example.qrsmartreader.ui.interactors.QrResultsInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


class QrResultsInteractorImpl : QrResultsInteractor {
    @Inject
    lateinit var repositorySettings: SettingsRepository

    @Inject
    lateinit var dbRepository: QrResultsRepository

    private var historyMLD: LiveData<List<QrResultEntity>>? = null

    init {
        App().component.inject(this)
    }

    override fun initHistory(app: Application): LiveData<List<QrResultEntity>> {
        historyMLD = dbRepository.getAllResultsFromBdAsync()
        return historyMLD!!
    }

    override fun addResultToHistory(app: Application, res: String) {
        if (res.isBlank()) return
        GlobalScope.launch(Dispatchers.IO) {
            val results = dbRepository.getAllResultsFromBd()
            if (results.any { it.text == res }) {
                dbRepository.deleteResultInDb(results.first { it.text == res })
            }
//        todo globalscope
            val dateTime = Calendar.getInstance().time
            val formatter = SimpleDateFormat("dd.MMM.yyyy", Locale.getDefault())
            val date = formatter.format(dateTime)
            val resultEntity = QrResultEntity(text = res, date = date)
            dbRepository.insertResultInDb(resultEntity)
        }
    }

    override fun clearQrResults(app: Application) {
//        todo GlobalScope
        GlobalScope.launch(Dispatchers.IO) {
            dbRepository.getAllResultsFromBd().forEach {
                dbRepository.deleteResultInDb(it)
            }
        }

    }

//    todo возможно стоит убрать
    override fun processingHistory(app: Application, history: List<QrResultEntity>) {
        val limitHistory = repositorySettings.getHistoryLimit()

        if (history.size > limitHistory) {
            removeExcessQrResultsFromDb()
        }
    }

    override fun removeExcessQrResultsFromDb() {
        //        todo GlobalScope
        GlobalScope.launch(Dispatchers.IO) {
            val history =   dbRepository.getAllResultsFromBd()
            history.slice(repositorySettings.getHistoryLimit() + 1..history.lastIndex)
                .forEach { entity ->
                    //        todo globalscope
                    dbRepository.deleteResultInDb(entity)
                }
        }
    }
}

