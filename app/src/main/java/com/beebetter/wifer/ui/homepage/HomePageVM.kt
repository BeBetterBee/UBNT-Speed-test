package com.beebetter.wifer.ui.homepage

import android.location.Location
import android.util.Log
import com.beebetter.api.StsService
import com.beebetter.api.model.ServerResponse
import com.beebetter.base.viewmodel.BaseViewModel
import com.beebetter.wifer.Wifer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import retrofit2.Retrofit
import java.util.*


class HomePageVM : BaseViewModel(), KodeinAware {
    override val kodein by lazy { Wifer.kodein }
    private val apiService: StsService by kodein.instance()
    private val retrofit: Retrofit by kodein.instance()
    var latitude: Double? = null
    var longitude: Double? = null
    var userLocation: Location? = null
    lateinit var serversResponse: Array<ServerResponse>

    init {
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
        // todo add real position
        return apiService.getServers(49.711711711711715, 14.879550948237824, token)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnError { Log.d("testApi", "No response server") }
            ?.subscribe { response ->
                serversResponse = response
                getClosestServer()
                Log.d("iu", response.get(0).city)
            }
    }

    private fun getClosestServer() {
        val allDistances = ArrayList<Float>()
        for (server in serversResponse) {
            val serverLocation = Location("")
            serverLocation.latitude = server.latitude
            serverLocation.longitude = server.longitude

            userLocation?.distanceTo(serverLocation)?.let { allDistances.add(it)
                Log.d("distances","distance"+it)}
        }
        Log.d("distances","all distances"+allDistances)
 allDistances.sortedWith(compareByDescending { it })
        Log.d("distances","all distances"+allDistances)
    }
}