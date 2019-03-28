package com.beebetter.base.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.base.base.event.LiveEvent
import com.example.base.base.event.LiveEventMap
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlin.reflect.KClass

/**
 * Handcrafted by Šťěpán Šonský
 * */
open class BaseViewModel: ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val liveEventMap = LiveEventMap()

    fun <T : LiveEvent> subscribe(lifecycleOwner: LifecycleOwner, eventClass: KClass<T>, eventObserver: Observer<T>) {
        liveEventMap.subscribe(lifecycleOwner, eventClass, eventObserver)
    }

    protected fun <T : LiveEvent> publish(event: T) {
        liveEventMap.publish(event)
    }

    protected fun addDisposable(disposable : Disposable){
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }


}