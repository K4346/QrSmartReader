package com.example.qrsmartreader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.qrsmartreader.databinding.ActivityMainBinding
import com.example.qrsmartreader.di.DaggerAppComponent
import com.example.qrsmartreader.ui.interactors.SettingsInteractor
import javax.inject.Inject


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }
}