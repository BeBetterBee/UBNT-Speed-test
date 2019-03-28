package com.beebetter.wifer.ui.homepage

import android.util.Log
import com.beebetter.api.StsService
import com.beebetter.base.viewmodel.BaseViewModel
import com.beebetter.wifer.Wifer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class HomePageVM: BaseViewModel(), KodeinAware {
    override val kodein by lazy { Wifer.kodein}
    private val apiService: StsService by kodein.instance()
    
    init {
        apiService.getToken()?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnError{ Log.d("testApi","No response")}
            ?.subscribe{response -> Log.d("testApi","token"+response.token)}
    }

}