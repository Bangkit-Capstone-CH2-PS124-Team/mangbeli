package com.capstone.mangbeli

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    companion object {
        lateinit var instance: MyApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseApp.initializeApp(this)
    }
}