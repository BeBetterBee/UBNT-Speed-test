package com.beebetter.wifer.util

import android.location.Location

class MapUtil {
    companion object {
        fun getDistanceFrom(originalLocation: Location?, nextLatitude:Double,nextLongitude:Double ):Float{
            val serverLocation = Location("")
            serverLocation.latitude = nextLatitude
            serverLocation.longitude = nextLongitude

           return originalLocation?.distanceTo(serverLocation) ?: 10000000000000000f
        }
    }
}