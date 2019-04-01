package com.beebetter.wifer.util

import com.beebetter.api.model.ping.PingBdo
import com.beebetter.api.model.ping.PingResponse
import com.beebetter.api.model.server.ServerBdo
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PingHelper {
    companion object {
        fun getPingObservable(serverBdo: ServerBdo, token: String): Observable<PingBdo>? {
            return serverBdo.apiService?.ping(token)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.flatMap { pingResponse ->
                    val ping = PingBdo.convert(pingResponse?.body() as PingResponse)
                    serverBdo.pingBdo = ping
                    ping.timeResponse =  pingResponse.raw().sentRequestAtMillis()
                    -pingResponse.raw().receivedResponseAtMillis()
                    Observable.just(ping)
                }
        }

        fun getSmallestPingObservable(closest5Servers: List<ServerBdo>): Observable<ServerBdo>? {
            val allPings = ArrayList<Float>()
            return Observable.just(closest5Servers)
                .map { unsorted -> unsorted.sortedBy { allPings.add(it.pingBdo?.timeResponse!!.toFloat()) } }
                .take(1)
                .map { it[0] }
        }
    }
}