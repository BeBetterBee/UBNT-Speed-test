package com.beebetter.wifer.ui.homepage

import android.util.Log
import com.beebetter.api.ApiConfig
import com.beebetter.api.ApiService
import com.beebetter.api.StsService
import com.beebetter.base.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HomePageVM: BaseViewModel() {
    init {
        ApiService.initRxRetrofit(ApiConfig.BASE_URL, ApiService.initOkHttp()).create(StsService::class.java).getToken()?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnError{ Log.d("testApi","No response")}
            ?.subscribe{response -> Log.d("testApi","token"+response.token)}
    }

}