package com.beebetter.wifer

import android.app.Application
import android.content.Context
import com.beebetter.api.ApiConfig.Companion.BASE_URL
import com.beebetter.api.ApiHelper
import com.beebetter.api.ApiHelper.Companion.initRxRetrofit
import com.beebetter.api.ServersService
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import retrofit2.Retrofit

class Wifer : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: Wifer? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        var kodein = Kodein {
            bind<Retrofit>() with singleton { initRxRetrofit(BASE_URL, ApiHelper.initOkHttp()) }
            bind<ServersService>() with singleton {
                val retrofit: Retrofit = instance()
                retrofit.create(ServersService::class.java)
            }
        }

    }
}