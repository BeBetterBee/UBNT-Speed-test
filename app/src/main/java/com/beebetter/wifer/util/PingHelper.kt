package com.beebetter.wifer.util

import android.util.Log
import com.beebetter.api.model.ping.PingBdo
import com.beebetter.api.model.ping.PingResponse
import com.beebetter.api.model.server.ServerBdo
import com.beebetter.base.util.RxUtil
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PingHelper {
    companion object {
        fun getPingObservable(serverBdo: ServerBdo, token: String): Observable<PingBdo>? {
            return RxUtil.applySchedulers(serverBdo.apiService?.ping(token)!!)
                ?.flatMap { pingResponse ->
                    val ping = PingBdo.convert(pingResponse?.body() as PingResponse)
                    serverBdo.pingBdo = ping
                    ping.timeResponse.value =
                    pingResponse.raw().receivedResponseAtMillis() - pingResponse.raw().sentRequestAtMillis()
                    Log.d("PingResponse",pingResponse.raw().sentRequestAtMillis().toString())
                    Log.d("PingResponseReceived",pingResponse.raw().receivedResponseAtMillis().toString())
                    Observable.just(ping)
                }
        }

        fun getSmallestPingObservable(closest5Servers: List<ServerBdo>): Observable<ServerBdo>? {
            val allPings = ArrayList<Float>()
            return Observable.just(closest5Servers)
                .map { unsorted -> unsorted.sortedBy { allPings.add(it.pingBdo?.timeResponse!!.value!!.toFloat()) } }
                .take(1)
                .map { it[0] }
        }
    }
}