package com.beebetter.api.model

data class ServerResponse(val city:String,
                          val country:String,
                          val countryCode:String,
                          val latitude:Double,
                          val longitude:Double,
                          val provider:String,
                          val providerUrl:String,
                          val speedMbps:Double,
                          val url:String
                          ) {
}