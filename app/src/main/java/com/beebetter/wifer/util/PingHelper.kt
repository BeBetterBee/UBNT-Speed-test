package com.beebetter.wifer.util

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.beebetter.api.model.ping.PingBdo
import com.beebetter.api.model.ping.PingResponse
import com.beebetter.api.model.server.ServerBdo
import com.beebetter.base.util.RxUtil
import io.reactivex.Observable


class PingHelper {
    companion object {
        fun getPingObservable(serverBdo: ServerBdo, token: String): Observable<PingBdo>? {
            return RxUtil.applySchedulers(serverBdo.apiService?.ping(token)!!)
                ?.flatMap { pingResponse ->
                    val ping = PingBdo.convert(pingResponse?.body() as PingResponse)
                    serverBdo.pingBdo = ping
                    ping.timeResponse.value =
                        pingResponse.raw().receivedResponseAtMillis() - pingResponse.raw().sentRequestAtMillis()
                    Observable.just(ping)
                }
        }

        fun getSmallestPingObservable(closest5Servers: List<ServerBdo>): Observable<ServerBdo>? {
            return Observable.just(closest5Servers)
                .map { unsorted ->
                    val sorted =
                        unsorted.sortedWith(
                            compareBy { it.pingBdo?.timeResponse!!.value!! })
                    sorted.forEach { Log.d("url", "url : " + it.url + " ping : " + it.pingBdo?.timeResponse?.value) }
                    sorted
                }
                .take(1)
                .map { it[0] }
        }

        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }
}