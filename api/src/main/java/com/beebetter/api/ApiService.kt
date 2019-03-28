package com.beebetter.api

import android.app.Application
import androidx.annotation.NonNull
import com.beebetter.api.ApiConfig.Companion.BASE_URL
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

class ApiService {
    companion object {
        var stsService:StsService? = null

        val apiModule = Kodein.Module {
            bind<Retrofit>() with singleton {
                Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .client(initOkHttp())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            }
        }
        fun initStsService(){
            stsService = initRxRetrofit(BASE_URL, initOkHttp()).create(StsService::class.java)
        }

        @NonNull
        fun initRxRetrofit(endpoint: String, httpClient: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .baseUrl(endpoint)
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .client(httpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }

        @NonNull
        fun getGson(): Gson {
            return GsonBuilder()
                .setLenient()
                .create()
        }

        @NonNull
        fun initOkHttp(): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val builder = OkHttpClient.Builder()
            builder.addInterceptor(loggingInterceptor)
                //.addInterceptor(ChuckInterceptor(context))

            return builder.build()

        }
    }
}