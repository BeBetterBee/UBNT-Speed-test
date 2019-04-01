package com.beebetter.base.util

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by stepan on 28.10.2017.
 */

class RxUtil {

    companion object {
        fun <T> applySchedulers(observable: Observable<T>): Observable<T> {
            return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        }

        fun <T> applySchedulers(maybe: Maybe<T>): Maybe<T> {
            return maybe.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        }

        fun <T> applySchedulers(single: Single<T>): Single<T> {
            return single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        }
    }
}