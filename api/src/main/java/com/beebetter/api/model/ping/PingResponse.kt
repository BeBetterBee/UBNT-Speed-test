package com.beebetter.api.model.ping

import retrofit2.Response


data class PingResponse(val pong:Boolean,val version:String) {
}