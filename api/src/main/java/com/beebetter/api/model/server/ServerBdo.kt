package com.beebetter.api.model.server

import com.beebetter.api.StsService
import com.beebetter.api.model.ping.PingBdo

class ServerBdo {
    var city:String? = null
    var country:String? = null
    var countryCode:String? = null
    var latitude:Double? = null
    var longitude:Double? = null
    var provider:String? = null
    var providerUrl:String? = null
    var speedMbps:Double? = null
    var url:String? = null

    var distanceFromUser:Float? = null
    var apiService: StsService? = null
    var pingBdo:PingBdo? = null
}