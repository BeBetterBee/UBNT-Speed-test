package com.beebetter.wifer

import android.app.Application
import com.beebetter.api.ApiService
import com.beebetter.api.StsService

class Wifer:Application() {
    companion object {
        lateinit var instance: Wifer
        lateinit var apiService : StsService
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        ApiService.initStsService()
    }
}