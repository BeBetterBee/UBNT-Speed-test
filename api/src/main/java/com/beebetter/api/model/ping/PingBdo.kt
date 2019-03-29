package com.beebetter.api.model.ping

class PingBdo {
    var pong:Boolean = false
    var timeResponse:Long = 0

    companion object {
        fun convert(pingResponse: PingResponse):PingBdo{
            return PingBdo().apply { pong = pingResponse.pong }
        }
    }
}