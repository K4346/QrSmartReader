package com.example.qrsmartreader

import android.app.Application

import com.example.qrsmartreader.di.AppComponent
import com.example.qrsmartreader.di.DaggerAppComponent

class App : Application() {

    val component: AppComponent by lazy { DaggerAppComponent.builder().application(appContext!!).build() }

    override fun onCreate() {
        super.onCreate()
        appContext = this

    }
    companion object{
        var appContext:Application? = null
    }
}