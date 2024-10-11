package com.example.permissionandroid2

import android.app.Application

class CustomApplication : Application() {

    companion object {
        lateinit var instance: CustomApplication
            private set
    }

    override fun onCreate() {
        instance = this
        super.onCreate()
    }
}