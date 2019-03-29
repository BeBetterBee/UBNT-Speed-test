package com.beebetter.api

import com.beebetter.api.model.ping.PingResponse
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response

import retrofit2.http.GET
import retrofit2.http.Header

interface StsService {
    @GET("/ping")
    fun ping(@Header("x-test-token") token:String): Single<Response<PingResponse>>
}