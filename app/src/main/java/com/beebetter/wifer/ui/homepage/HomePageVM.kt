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
    private val retrofit: Retrofit by kodein.instance()
    var userLocation: Location? = null
    var token: String = ""
    lateinit var serversResponse: List<ServerBdo>
    lateinit var closest5Servers: List<ServerBdo>

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
//            ?.map{ servers ->
//                val list: List<com.beebetter.api.model.server.ServerBdo>
//                servers.forEach { list.adConverter.convert(it) }}
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
        val allDistances = ArrayList<Float>()
        Observable.just(serversResponse)
            .map { unsortedServerList ->
                unsortedServerList.sortedBy {
                    allDistances.add(
                        getDistanceFrom(
                            userLocation,
                            it.latitude!!,
                            it.longitude!!
                        )
                    )
                }
            }
            .flatMapIterable { server -> server }
            .take(CLOSEST_SERVER_COUNT)
            .toList()
            .subscribe { closestServers ->
                closest5Servers = closestServers
                setApiForStsServers(closest5Servers)
                Log.d("distances", "all distances sorted" + closestServers)
                Log.d(
                    "server",
                    "1: " + getDistanceFrom(
                        userLocation,
                        closestServers.get(0).latitude!!,
                        closestServers.get(0).longitude!!
                    )
                            + "2: " + getDistanceFrom(
                        userLocation,
                        closestServers.get(1).latitude!!,
                        closestServers.get(1).longitude!!
                    )
                            + "3: " + getDistanceFrom(
                        userLocation,
                        closestServers.get(2).latitude!!,
                        closestServers.get(2).longitude!!
                    )
                )
            }
    }

    private fun setApiForStsServers(closest5Servers: List<ServerBdo>){
        Observable.fromIterable(closest5Servers)
            .flatMap{it -> it.apiService = initRxRetrofit(it.url!!, initOkHttp())
                .create(StsService::class.java)
            Observable.just(it)}
            .toList()
            .subscribe{
                it ->
                getPingResponses(it)
            }
    }
    private fun getPingResponses(closest5Servers: List<ServerBdo>) {
        setPing(closest5Servers[0])
        setPing(closest5Servers[1])
        setPing(closest5Servers[2])
        setPing(closest5Servers[3])
        setPing(closest5Servers[4])

        Observable.timer(20,TimeUnit.SECONDS).subscribe{
            pickSmallestPing()
        }
    }

    private fun pickSmallestPing() {
        val allPings = ArrayList<Float>()
        Observable.just(closest5Servers)
            .map{unsorted -> unsorted.sortedBy {allPings.add(it.pingBdo?.timeResponse!!.toFloat())}}
            .take(1)
            .subscribe{
                it -> Log.d("pinger","smallest ping "+ it.get(0).pingBdo!!.timeResponse
            + " for server "+it.get(0).url)
            }
    }

    private fun setPing(serverBdo: ServerBdo){
        serverBdo.apiService?.ping(token)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap { pingResponse ->
                val ping = convert(pingResponse?.body() as PingResponse)
                serverBdo.pingBdo = ping
                ping.timeResponse = pingResponse.raw().receivedResponseAtMillis()
                Single.just(ping)
            }
            ?.subscribe { pingResponse -> Log.d("pong", pingResponse.timeResponse.toString()) }
    }
}