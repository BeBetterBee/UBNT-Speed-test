package com.beebetter.api.model.ping

import androidx.lifecycle.MutableLiveData

class PingBdo {
    var pong: Boolean = false
    val timeResponse = MutableLiveData<Long>()

    companion object {
        fun convert(pingResponse: PingResponse): PingBdo {
            return PingBdo().apply {
                pong = pingResponse.pong
            }
        }
    }
}