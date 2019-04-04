package com.beebetter.api

import androidx.annotation.NonNull
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ApiHelper {
    companion object {
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
                .create()
        }

        @NonNull
        fun initOkHttp(): OkHttpClient {
            val builder = OkHttpClient.Builder()


            return builder.build()

        }


    }
}