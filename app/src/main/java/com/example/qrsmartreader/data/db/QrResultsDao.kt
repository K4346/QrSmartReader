package com.example.qrsmartreader.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.qrsmartreader.data.entities.QrResultEntity

@Dao
interface QrResultsDao {
    @Query("SELECT * FROM results")
    fun getAllAsync(): LiveData<List<QrResultEntity>>

    @Query("SELECT * FROM results")
    fun getAll(): List<QrResultEntity>

    @Insert
    fun insert(result: QrResultEntity)

    @Update
    fun update(result: QrResultEntity)

    @Delete
    fun delete(result: QrResultEntity)
}