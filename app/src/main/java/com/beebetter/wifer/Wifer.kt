package com.beebetter.wifer

import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.beebetter.api.ApiConfig.Companion.BASE_URL
import com.beebetter.api.ApiService
import com.beebetter.api.ApiService.Companion.initRxRetrofit
import com.beebetter.api.StsService
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import retrofit2.Retrofit
import java.security.SecureRandom

class Wifer : Application() {
    companion object {
        lateinit var instance: Wifer
        lateinit var apiService: StsService

        val kodein = Kodein {
            bind<Retrofit>() with singleton { initRxRetrofit(BASE_URL, ApiService.initOkHttp()) }
            bind<StsService>() with singleton {
                val retrofit: Retrofit = instance()
                retrofit.create(StsService::class.java)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        ApiService.initStsService()
    }
}