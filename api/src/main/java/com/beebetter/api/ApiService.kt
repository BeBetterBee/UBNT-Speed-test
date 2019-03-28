package com.beebetter.api

import android.app.Application
import android.content.Context
import androidx.annotation.NonNull
import com.beebetter.api.ApiConfig.Companion.BASE_URL
import com.beebetter.api.model.interceptor.AuthenticationInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.readystatesoftware.chuck.ChuckInterceptor
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import java.io.IOException

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
                .setLenient()
                .create()
        }

        @NonNull
        fun initOkHttp(): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val builder = OkHttpClient.Builder()
            builder.addInterceptor(loggingInterceptor)
           //     .addInterceptor(AuthenticationInterceptor("test"))
               // .addInterceptor(ChuckInterceptor())

            return builder.build()

        }
    }
}