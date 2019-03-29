package com.beebetter.wifer

import android.app.Application
import com.beebetter.api.ApiConfig.Companion.BASE_URL
import com.beebetter.api.ApiService
import com.beebetter.api.ApiService.Companion.initRxRetrofit
import com.beebetter.api.ServersService
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import retrofit2.Retrofit

class Wifer : Application() {
    companion object {
        lateinit var instance: Wifer
        var kodein = Kodein {
            bind<Retrofit>() with singleton { initRxRetrofit(BASE_URL, ApiService.initOkHttp()) }
            bind<ServersService>() with singleton {
                val retrofit: Retrofit = instance()
                retrofit.create(ServersService::class.java)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}