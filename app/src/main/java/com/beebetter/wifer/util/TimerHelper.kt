package com.beebetter.wifer.util

import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class TimerHelper {
    companion object {
        fun getTimerObservable(countDown:Long): Observable<Long> {
            return Observable.interval(0,1,TimeUnit.SECONDS)
                .take(countDown+1)
                .map{remainingTime -> remainingTime-1}
                .filter { remain -> remain>0 }
        }
    }
}