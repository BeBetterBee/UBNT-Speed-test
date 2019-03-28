package com.beebetter.api

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val apiModule = Kodein.Module {
    bind<Retrofit>() with singleton {
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(ApiService.getGson()))
           // .client(ApiService.initOkHttp())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    bind<StsService>() with singleton {
        val retrofit: Retrofit = instance()
        retrofit.create(StsService::class.java)
    }
}