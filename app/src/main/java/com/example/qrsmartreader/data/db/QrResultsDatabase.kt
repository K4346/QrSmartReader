package com.example.qrsmartreader.data.db
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.qrsmartreader.data.entities.QrResultEntity

@Database(entities = [QrResultEntity::class], version = 1, exportSchema = false)
abstract class QrResultsDatabase : RoomDatabase() {
    abstract fun qrResultsDao(): QrResultsDao

    companion object {
        private var db: QrResultsDatabase? = null
        private const val DB_NAME = "results.db"
        private val Lock = Any()
        fun getInstance(context: Context): QrResultsDatabase {
            synchronized(Lock) {
                db?.let { return it }
                val instance =
                    Room.databaseBuilder(context, QrResultsDatabase::class.java, DB_NAME).build()
                db = instance
                return instance
            }
        }
    }
}