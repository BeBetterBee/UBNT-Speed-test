package com.beebetter.api

import com.beebetter.api.model.ping.PingResponse
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.ResponseBody


import retrofit2.Response

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Streaming

interface StsService {
    @GET("/ping")
    fun ping(@Header("x-test-token") token:String): Observable<Response<PingResponse>>

    @Streaming
    @GET("/download")
    fun testDownload(@Header("x-test-token") token:String,
                     @Query("size") downloadSize:Long):Observable<Response<ResponseBody>>
}