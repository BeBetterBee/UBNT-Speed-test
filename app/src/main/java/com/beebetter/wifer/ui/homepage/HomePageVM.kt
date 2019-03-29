package com.beebetter.wifer.ui.homepage

import android.location.Location
import android.util.Log
import com.beebetter.api.StsService
import com.beebetter.api.model.ServerResponse
import com.beebetter.base.viewmodel.BaseViewModel
import com.beebetter.wifer.AppConfig.Companion.CLOSEST_SERVER_COUNT
import com.beebetter.wifer.Wifer
import com.beebetter.wifer.util.MapUtil.Companion.getDistanceFrom
import io.reactivex.Observable

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import retrofit2.Retrofit



class HomePageVM : BaseViewModel(), KodeinAware {
    override val kodein by lazy { Wifer.kodein }
    private val apiService: StsService by kodein.instance()
    private val retrofit: Retrofit by kodein.instance()
    var latitude: Double? = null
    var longitude: Double? = null
    var userLocation: Location? = null
    lateinit var serversResponse: List<ServerResponse>

    fun getToken() {
        apiService.getToken()?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnError { Log.d("testApi", "No response") }
            ?.subscribe { response ->
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
            .map{unsortedServerList ->
                unsortedServerList.sortedBy { allDistances.add(getDistanceFrom(userLocation,it.latitude,it.longitude)) }
            }
            .flatMapIterable { server -> server }
            .take(CLOSEST_SERVER_COUNT)
            .toList()
            .subscribe { closestServers ->
                Log.d("distances","all distances sorted"+closestServers)
            Log.d("server","1: "+getDistanceFrom(userLocation,closestServers.get(0).latitude,closestServers.get(0).longitude)
            +"2: "+getDistanceFrom(userLocation,closestServers.get(1).latitude,closestServers.get(1).longitude)
            +"3: "+getDistanceFrom(userLocation,closestServers.get(2).latitude,closestServers.get(2).longitude))}
    }
}