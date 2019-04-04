package com.beebetter.api


import com.beebetter.api.model.TestUploadResponse
import com.beebetter.api.model.ping.PingResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface StsService {
    @GET("/ping")
    fun ping(@Header("x-test-token") token: String): Observable<Response<PingResponse>>

    @Streaming
    @GET("/download")
    fun testDownload(
        @Header("x-test-token") token: String,
        @Query("size") downloadSize: Long
    ): Observable<Response<ResponseBody>>

    @Multipart
    @POST("/upload")
    fun testUpload(@Part part: MultipartBody.Part): Observable<TestUploadResponse>

}