package com.beebetter.api.model.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthenticationInterceptor(token: String) : Interceptor {
    private val credentials: String = token
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request.newBuilder()
            .header("x-test-token", credentials).build()
        Log.d("inte0","Adding interceptor")
        return chain.proceed(authenticatedRequest)
    }
}
