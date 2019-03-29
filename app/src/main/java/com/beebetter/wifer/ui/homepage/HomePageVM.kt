package com.beebetter.wifer.ui.homepage

import android.location.Location
import android.util.Log
import com.beebetter.api.ApiService.Companion.initOkHttp
import com.beebetter.api.ApiService.Companion.initRxRetrofit
import com.beebetter.api.ServersService
import com.beebetter.api.StsService
import com.beebetter.api.model.ping.PingBdo
import com.beebetter.api.model.ping.PingBdo.Companion.convert
import com.beebetter.api.model.ping.PingResponse
import com.beebetter.api.model.server.ServerBdo
import com.beebetter.base.viewmodel.BaseViewModel
import com.beebetter.wifer.AppConfig.Companion.CLOSEST_SERVER_COUNT
import com.beebetter.wifer.Wifer
import com.beebetter.wifer.util.Converter
import com.beebetter.wifer.util.MapUtil.Companion.getDistanceFrom
import com.beebetter.wifer.util.PingHelper.Companion.getPingObservable
import com.beebetter.wifer.util.PingHelper.Companion.getSmallestPingObservable
import com.beebetter.wifer.util.StsHelper
import io.reactivex.Observable
import io.reactivex.Single

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit


class HomePageVM : BaseViewModel(), KodeinAware {
    override val kodein by lazy { Wifer.kodein }
    private val apiService: ServersService by kodein.instance()
    var userLocation: Location? = null
    var token: String = ""
    lateinit var serversResponse: List<ServerBdo>
    lateinit var closest5Servers: List<ServerBdo>

    fun getToken() {
        apiService.getToken().subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnError { Log.d("testApi", "No response") }
            ?.subscribe { response ->
                token = response.token
                Log.d("testApi", "token" + response.token)
                getServers(response.token)
            }
    }

    private fun getServers(token: String): Disposable? {
        return apiService.getServers(userLocation?.latitude!!, userLocation?.longitude!!, token)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnError { Log.d("testApi", "No response server") }
            ?.subscribe { response ->
                val resObjects = ArrayList<ServerBdo>()
                for (server in response) {
                    resObjects.add(Converter.convert(server))
                }
                serversResponse = resObjects
                getClosestServers()
                Log.d("iu", response.get(0).city)
            }
    }

    private fun getClosestServers() {
        StsHelper.getClosestServerObservable(this!!.userLocation!!, serversResponse)
            ?.subscribe { closestServers ->
                closest5Servers = closestServers
                setApiForStsServers(closest5Servers)
                Log.d("distances", "all distances sorted" + closestServers)
            }
    }


    private fun setApiForStsServers(closest5Servers: List<ServerBdo>) {
        StsHelper.getApiForStsObservable(closest5Servers)
            ?.subscribe { it ->
                getPingResponses(it)
            }
    }

    private fun getPingResponses(closest5Servers: List<ServerBdo>) {
        setPing(closest5Servers[0])
        setPing(closest5Servers[1])
        setPing(closest5Servers[2])
        setPing(closest5Servers[3])
        setPing(closest5Servers[4])

        Observable.timer(10, TimeUnit.SECONDS).subscribe {
            pickSmallestPing()
        }
    }

    private fun pickSmallestPing() {
        getSmallestPingObservable(closest5Servers)
            ?.subscribe { it ->
                Log.d(
                    "pinger", "smallest ping " + it.pingBdo!!.timeResponse
                            + " for server " + it.url
                )
            }
    }

    private fun setPing(serverBdo: ServerBdo) {
        getPingObservable(serverBdo, token)
            ?.subscribe { pingResponse -> Log.d("pong", pingResponse.timeResponse.toString()) }
    }
}