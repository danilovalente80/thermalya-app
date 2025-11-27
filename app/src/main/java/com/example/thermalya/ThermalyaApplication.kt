// File: ThermalyaApplication.kt
package com.example.thermalya

import android.app.Application
import com.google.firebase.FirebaseApp
import com.jakewharton.threetenabp.AndroidThreeTen

class ThermalyaApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inizializza Firebase
        FirebaseApp.initializeApp(this)

        // Inizializza ThreeTenABP per le date
        AndroidThreeTen.init(this)

        android.util.Log.d("ThermalyaApp", "Firebase inizializzato correttamente")
    }
}