package com.beebetter.wifer.util

import android.content.Context
import android.location.Location
import com.beebetter.api.ApiService
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
                    it.apiService = ApiService.initRxRetrofit(it.url!!, ApiService.initOkHttp())
                        .create(StsService::class.java)
                    Observable.just(it)
                }
                .toList()
        }


        fun getClosestServerObservable(
            userLocation: Location,
            serversResponse: List<ServerBdo>
        ): Single<MutableList<ServerBdo>>? {
            val allDistances = ArrayList<Float>()
            return Observable.just(serversResponse)
                .map { unsortedServerList ->
                    unsortedServerList.sortedBy {
                        allDistances.add(
                            MapUtil.getDistanceFrom(
                                userLocation,
                                it.latitude!!,
                                it.longitude!!
                            )
                        )
                    }
                }
                .flatMapIterable { server -> server }
                .take(AppConfig.CLOSEST_SERVER_COUNT)
                .toList()
        }
    }
}