package com.beebetter.api

import com.beebetter.api.model.TokenResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST


interface StsService {
    @POST("api/v1/tokens")
    fun getToken(): Single<TokenResponse>
}