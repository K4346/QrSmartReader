package com.example.qrsmartreader.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "results")
data class QrResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val text: String,
    val date: String
)
