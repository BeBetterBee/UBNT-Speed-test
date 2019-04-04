package com.beebetter.wifer.util

import com.beebetter.api.model.server.ServerBdo
import com.beebetter.api.model.server.ServerResponse

class Converter {
    companion object {
        fun convert(serverResponse: ServerResponse): ServerBdo {
            return ServerBdo().apply {
                city = serverResponse.city
                country = serverResponse.country
                countryCode = serverResponse.countryCode
                latitude = serverResponse.latitude
                longitude = serverResponse.longitude
                provider = serverResponse.provider
                providerUrl = serverResponse.providerUrl
                url = serverResponse.url
            }
        }
    }
}