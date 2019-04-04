package com.beebetter.api

import com.beebetter.api.model.TokenResponse
import com.beebetter.api.model.server.ServerResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


interface ServersService {
    @POST("api/v1/tokens")
    fun getToken(): Single<TokenResponse>

    @GET("/api/v2/servers")
    fun getServers(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Header("x-test-token") token: String
    ): Single<List<ServerResponse>>


}