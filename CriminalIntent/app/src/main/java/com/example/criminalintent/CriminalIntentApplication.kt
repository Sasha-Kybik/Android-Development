package com.example.criminalintent

import android.app.Application

class CriminalIntentApplication : Application() {

    // Включить в раздел application файла AndroidManifest.xml -> android:name=".CriminalIntentApplication"
    // с целью создания экземпляра CriminalIntentApplication при запуске приложения
    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this) // Обязательная инициализация CrimeRepository при запуске приложения
    }
}