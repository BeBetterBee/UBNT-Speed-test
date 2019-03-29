package com.beebetter.wifer.ui.homepage

import android.location.Location
import android.util.Log
import com.beebetter.api.ApiService.Companion.initOkHttp
import com.beebetter.api.ApiService.Companion.initRxRetrofit
import com.beebetter.api.ServersService
import com.beebetter.api.StsService
import com.beebetter.api.model.ServerResponse
import com.beebetter.api.model.ping.PingBdo
import com.beebetter.api.model.ping.PingBdo.Companion.convert
import com.beebetter.api.model.ping.PingResponse
import com.beebetter.base.viewmodel.BaseViewModel
import com.beebetter.wifer.AppConfig.Companion.CLOSEST_SERVER_COUNT
import com.beebetter.wifer.Wifer
import com.beebetter.wifer.util.MapUtil.Companion.getDistanceFrom
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleSource

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import retrofit2.Retrofit


class HomePageVM : BaseViewModel(), KodeinAware {
    override val kodein by lazy { Wifer.kodein }
    private val apiService: ServersService by kodein.instance()
    private val retrofit: Retrofit by kodein.instance()
    var userLocation: Location? = null
    var token: String = ""
    lateinit var serversResponse: List<ServerResponse>
    lateinit var closest5Servers: List<ServerResponse>

    fun getToken() {
        apiService.getToken()?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnError { Log.d("testApi", "No response") }
            ?.subscribe { response ->
                token = response.token
                run {
                    Log.d("testApi", "token" + response.token)
                    getServers(response.token)
                }
            }
    }

    private fun getServers(token: String): Disposable? {
        return apiService.getServers(userLocation?.latitude!!, userLocation?.longitude!!, token)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnError { Log.d("testApi", "No response server") }
            ?.subscribe { response ->
                serversResponse = response
                getClosestServers()
                Log.d("iu", response.get(0).city)
            }
    }

    private fun getClosestServers() {
        val allDistances = ArrayList<Float>()
        Observable.just(serversResponse)
            .map { unsortedServerList ->
                unsortedServerList.sortedBy {
                    allDistances.add(
                        getDistanceFrom(
                            userLocation,
                            it.latitude,
                            it.longitude
                        )
                    )
                }
            }
            .flatMapIterable { server -> server }
            .take(CLOSEST_SERVER_COUNT)
            .toList()
            .subscribe { closestServers ->
                closest5Servers = closestServers
                getPingResponses(closest5Servers)
                Log.d("distances", "all distances sorted" + closestServers)
                Log.d(
                    "server",
                    "1: " + getDistanceFrom(
                        userLocation,
                        closestServers.get(0).latitude,
                        closestServers.get(0).longitude
                    )
                            + "2: " + getDistanceFrom(
                        userLocation,
                        closestServers.get(1).latitude,
                        closestServers.get(1).longitude
                    )
                            + "3: " + getDistanceFrom(
                        userLocation,
                        closestServers.get(2).latitude,
                        closestServers.get(2).longitude
                    )
                )
            }
    }

    private fun getPingResponses(closest5Servers: List<ServerResponse>) {
        val apiService: StsService = initRxRetrofit(closest5Servers[0].url, initOkHttp())
            .create(StsService::class.java)
        apiService.ping(token).subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap { pingResponse ->
                val ping = convert(pingResponse?.body() as PingResponse)
                ping.timeResponse = pingResponse.raw().receivedResponseAtMillis()
                Single.just(ping)
            }
            ?.subscribe { pingResponse -> Log.d("pong", pingResponse.timeResponse.toString()) }
    }
}