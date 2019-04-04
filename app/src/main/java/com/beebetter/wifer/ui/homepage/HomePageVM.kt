package com.beebetter.wifer.ui.homepage

import android.location.Location
import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.beebetter.api.ServersService
import com.beebetter.api.model.server.ServerBdo
import com.beebetter.base.util.RxUtil
import com.beebetter.base.viewmodel.BaseViewModel
import com.beebetter.wifer.AppConfig.Companion.TEST_COUNTDOWN
import com.beebetter.wifer.AppConfig.Companion.TEST_DOWNLOAD_SIZE
import com.beebetter.wifer.Wifer.Companion.kodein
import com.beebetter.wifer.util.Converter
import com.beebetter.wifer.util.DownloadHelper.measureDownloadSpeed
import com.beebetter.wifer.util.PingHelper.Companion.getPingObservable
import com.beebetter.wifer.util.PingHelper.Companion.getSmallestPingObservable
import com.beebetter.wifer.util.StsHelper
import com.beebetter.wifer.util.TimerHelper.Companion.getCountDownObservable
import io.reactivex.disposables.CompositeDisposable
import okhttp3.ResponseBody
import org.kodein.di.generic.instance
import org.reactivestreams.Subscriber
import java.util.*


class HomePageVM : BaseViewModel(), HomePage.VM {
    private val apiService: ServersService by kodein.instance()
    var userLocation: Location? = null
    var token: String = ""
    private lateinit var serversResponse: List<ServerBdo>
    var serversResponseDistance = ArrayList<ServerBdo>()
    lateinit var closest5Servers: List<ServerBdo>
    var stsServer = MutableLiveData<ServerBdo>()
    var stsServerUrl = MutableLiveData<String>()

    val downloadSpeed = MutableLiveData<String>()
    val downloadAvailable = MutableLiveData<Boolean>().apply { value = false }
    var downloadTestDisposable = CompositeDisposable()
    private val allDownloadSpeeds = ObservableArrayList<Double>()
    val testProgress = MutableLiveData<Int>()

    lateinit var downloadSubscriber: Subscriber<ResponseBody>

    init {
        testProgress.value = 5
    }

    override fun onTestBtnClick() {
        if (downloadAvailable.value == true) {
            finishTest()
        } else {
            RxUtil.applySchedulers(apiService.getToken())
                ?.doOnError { Log.d("testApi", "No response") }
                ?.subscribe { response ->
                    token = response.token
                    startDownloadTest()
                }
        }
    }

    override fun startDownloadTest() {
        downloadAvailable.value = true
        startTestCountdown()
        this.downloadTestDisposable.add(RxUtil.applySchedulers(
            stsServer.value?.apiService
                ?.testDownload(token, TEST_DOWNLOAD_SIZE)!!
                .map { it ->
                    measureDownloadSpeed(it.body(), downloadSpeed, System.currentTimeMillis(), allDownloadSpeeds)
                }
        )
            .repeat().subscribe { downloadSubscriber })
    }

    override fun startTestCountdown() {
        downloadTestDisposable.add(
            getCountDownObservable(TEST_COUNTDOWN)
                .doOnComplete {
                    finishTest()
                }
                .subscribe { it ->
                    testProgress.postValue(testProgress.value?.plus(7))
                    Log.d("timer", testProgress.value.toString())
                })
    }

    private fun finishTest() {
        downloadTestDisposable.clear()
        downloadAvailable.postValue(false)
        testProgress.postValue(5)
        setAverageSpeed()
    }

    private fun setAverageSpeed() {
        downloadSpeed.postValue(String.format("%.2f", (allDownloadSpeeds.sum() / allDownloadSpeeds.size)))
        allDownloadSpeeds.clear()
    }

    override fun getToken() {
        RxUtil.applySchedulers(apiService.getToken())
            ?.doOnError { Log.d("testApi", "No response") }
            ?.subscribe { response ->
                token = response.token
                Log.d("testApi", "token" + response.token)
                getServers(response.token)
            }
    }

    override fun getServers(token: String) {
        RxUtil.applySchedulers(
            apiService.getServers(userLocation?.latitude!!, userLocation?.longitude!!, token)
        )
            .doOnError { Log.d("testApi", "No response server") }
            .subscribe { response ->
                val resObjects = ArrayList<ServerBdo>()
                for (server in response) {
                    resObjects.add(Converter.convert(server))
                }
                serversResponse = resObjects
                getClosestServers()
                Log.d("iu", response[0].city)
            }
    }

    override fun getClosestServers() {
        StsHelper.getDistanceForServersObservable(this!!.userLocation!!, serversResponse)
            ?.doOnComplete {
                StsHelper.getClosestServerObservable(serversResponseDistance)
                    ?.subscribe { closestServers ->
                        closest5Servers = closestServers
                        setApiForStsServers(closest5Servers)
                        closestServers.forEach { it ->
                            Log.d(
                                "distances",
                                "distance" + it.distanceFromUser + "url " + it.url
                            )
                        }
                    }
            }
            ?.subscribe { it ->
                serversResponseDistance.add(it)
            }
    }

    override fun setApiForStsServers(closest5Servers: List<ServerBdo>) {
        StsHelper.getApiForStsObservable(closest5Servers)
            ?.subscribe { it ->
                getPingForClosestServers()
            }
    }

    override fun pickSmallestPing() {
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

    override fun getPingForClosestServers() {
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