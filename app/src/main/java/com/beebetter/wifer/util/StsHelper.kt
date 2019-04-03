package com.beebetter.wifer.util

import android.location.Location
import android.util.Log
import com.beebetter.api.ApiHelper
import com.beebetter.api.StsService
import com.beebetter.api.model.server.ServerBdo
import com.beebetter.wifer.AppConfig
import io.reactivex.Observable
import io.reactivex.Single

class StsHelper {
    companion object {
        fun getApiForStsObservable(closest5Servers: List<ServerBdo>): Single<MutableList<ServerBdo>>? {
            return Observable.fromIterable(closest5Servers)
                .flatMap { it ->
                    it.apiService = ApiHelper.initRxRetrofit(it.url!!, ApiHelper.initOkHttp())
                        .create(StsService::class.java)
                    Observable.just(it)
                }
                .toList()
        }

        fun getDistanceForServersObservable(
            userLocation: Location,
            serversResponse: List<ServerBdo>
        ): Observable<ServerBdo>? {
            return Observable.fromIterable(serversResponse)
                .map { server ->
                    server.distanceFromUser = MapUtil.getDistanceFrom(
                        userLocation,
                        server.latitude!!,
                        server.longitude!!
                    )
                    server
                }
        }


        fun getClosestServerObservable(
            serversResponse: List<ServerBdo>
        ): Single<MutableList<ServerBdo>>? {
            return Observable.just(serversResponse)
                .map { unsortedDistanceServerList ->
                   val sortedDistanceList = unsortedDistanceServerList.sortedWith(
                        compareBy {
                           it.distanceFromUser
                        })
                    sortedDistanceList.forEach { Log.d("sortedDist","url: " + it.url + "distance:" +it.distanceFromUser) }
                    unsortedDistanceServerList.forEach { Log.d("sortedDist","url: " + it.url + "distance:" +it.distanceFromUser) }
                    sortedDistanceList
                }
                .flatMapIterable { server -> server }
                .take(AppConfig.CLOSEST_SERVER_COUNT)
                .toList()
        }
    }

}