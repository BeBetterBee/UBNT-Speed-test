package com.beebetter.api

import androidx.annotation.NonNull
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ApiService {
    companion object {
        @NonNull
        fun initRxRetrofit(endpoint: String, httpClient: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .baseUrl(endpoint)
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .client(httpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //.addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create(Schedulers.io()))
                .build()
        }

        @NonNull
        fun getGson(): Gson {
            return GsonBuilder()
                //.setLenient()
                .create()
        }

        @NonNull
        fun initOkHttp(): OkHttpClient {
          //  val loggingInterceptor = HttpLoggingInterceptor()
            //loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val builder = OkHttpClient.Builder()
            //builder.addInterceptor(loggingInterceptor)
            // .addInterceptor(ChuckInterceptor())

            return builder.build()

        }


    }}