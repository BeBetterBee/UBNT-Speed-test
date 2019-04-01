package com.beebetter.wifer.ui.homepage

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.beebetter.api.ServersService
import com.beebetter.api.model.server.ServerBdo
import com.beebetter.base.util.RxUtil
import com.beebetter.base.viewmodel.BaseViewModel
import com.beebetter.wifer.Wifer.Companion.kodein
import com.beebetter.wifer.util.Converter
import com.beebetter.wifer.util.PingHelper.Companion.getPingObservable
import com.beebetter.wifer.util.PingHelper.Companion.getSmallestPingObservable
import com.beebetter.wifer.util.StsHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.kodein.di.generic.instance
import java.util.concurrent.TimeUnit


class HomePageVM : BaseViewModel(), HomePage.VM {
    //KodeinAware,
    override fun startTest() {
        Log.d("btn", "onBtnClick")
        RxUtil.applySchedulers(
            stsServer.value?.apiService
                ?.testDownload(token)!!
        ).subscribe{it ->
            val bt = it.raw().body()?.byteStream()
        }

    }

    // override val kodein by lazy { Wifer.kodein }
    private val apiService: ServersService by kodein.instance()
    var userLocation: Location? = null
    var token: String = ""
    lateinit var serversResponse: List<ServerBdo>
    lateinit var closest5Servers: List<ServerBdo>
    var stsServer = MutableLiveData<ServerBdo>()
    var stsServerUrl = MutableLiveData<String>()

    fun getToken() {
        RxUtil.applySchedulers(apiService.getToken())
            ?.doOnError { Log.d("testApi", "No response") }
            ?.subscribe { response ->
                token = response.token
                Log.d("testApi", "token" + response.token)
                getServers(response.token)
            }
    }

    private fun getServers(token: String): Disposable? {
        return RxUtil.applySchedulers(
            apiService.getServers(userLocation?.latitude!!, userLocation?.longitude!!, token)
        )
            ?.doOnError { Log.d("testApi", "No response server") }
            ?.subscribe { response ->
                val resObjects = ArrayList<ServerBdo>()
                for (server in response) {
                    resObjects.add(Converter.convert(server))
                }
                serversResponse = resObjects
                getClosestServers()
                Log.d("iu", response[0].city)
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
                getPingResponses()
            }
    }

    private fun getPingResponses() {
        setPing()
    }

    private fun pickSmallestPing() {
        RxUtil.applySchedulers(getSmallestPingObservable(closest5Servers)!!)
            ?.subscribe { it ->
                stsServerUrl.value = (it.url)
                stsServer.value = it
                Log.d(
                    "pinger", "smallest ping " + it.pingBdo!!.timeResponse
                            + " for server " + it.url
                )
            }
    }

    private fun setPing() {
        getPingObservable(closest5Servers[0], token)
            ?.subscribe { it ->
                getPingObservable(closest5Servers[1], token)
                    ?.subscribe { it ->
                        getPingObservable(closest5Servers[2], token)
                            ?.subscribe { it ->
                                getPingObservable(closest5Servers[3], token)
                                    ?.subscribe { it ->
                                        getPingObservable(closest5Servers[4], token)
                                            ?.subscribe { it ->
                                                pickSmallestPing()
                                            }
                                    }
                            }
                    }
            }
    }
}