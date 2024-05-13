package com.example.qrsmartreader.data.db

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.qrsmartreader.data.entities.CameraRecognitionType
import com.example.qrsmartreader.data.entities.ProcessorRecognitionType
import javax.inject.Singleton

@Singleton
class SettingsManager {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var masterKey: MasterKey
    fun initialize(app: Application) {
        masterKey = MasterKey.Builder(app, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        sharedPreferences = EncryptedSharedPreferences.create(
            app,
            SETTINGS_PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    var modelType: String
        get() = sharedPreferences.getString(MODEL_TYPE, defaultModelType)!!
        set(value) {
            sharedPreferences.edit().putString(MODEL_TYPE, value).apply()
        }
    var modelVersionName: String
        get() = sharedPreferences.getString(MODEL_VERSION, defaultModelVersionName)!!
        set(value) {
            sharedPreferences.edit().putString(MODEL_VERSION, value).apply()
        }

    var processorValue: String
        get() = sharedPreferences.getString(PROCESSOR_TYPE, defaultProcessorType)!!
        set(value) {
            sharedPreferences.edit().putString(PROCESSOR_TYPE, value).apply()
        }
    var historyCountLimitation: Int
        get() = sharedPreferences.getInt(HISTORY_COUNT_LIMITATION, defaultHistoryCountLimitation)
        set(value) {
            sharedPreferences.edit().putInt(HISTORY_COUNT_LIMITATION, value)
                .apply()
        }
    var fpsCountVisible: Boolean
        get() = sharedPreferences.getBoolean(FPS_COUNT_VISIBLE, defaultFPSCountVisible)
        set(value) {
            sharedPreferences.edit().putBoolean(FPS_COUNT_VISIBLE, value)
                .apply()
        }


    companion object {
        private val defaultModelType = CameraRecognitionType.Pose.name
        private const val defaultModelVersionName = ""
        private val defaultProcessorType= ProcessorRecognitionType.Cpu.name
        private const val defaultHistoryCountLimitation = 5
        private const val defaultFPSCountVisible = true

        private const val MODEL_TYPE = "MODEL_TYPE"
        private const val MODEL_VERSION = "MODEL_VERSION"
        private const val PROCESSOR_TYPE = "PROCESSOR_TYPE"
        private const val HISTORY_COUNT_LIMITATION = "HISTORY_COUNT_LIMITATION"
        private const val FPS_COUNT_VISIBLE = "FPS_COUNT_VISIBLE"

        private const val SETTINGS_PREF_NAME = "qr_smart_reader_settings"
    }
}