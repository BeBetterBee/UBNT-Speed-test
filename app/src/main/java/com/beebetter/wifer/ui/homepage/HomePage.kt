package com.beebetter.wifer.ui.homepage

import com.beebetter.api.model.server.ServerBdo

interface HomePage {
    interface View {
        fun checkInternetConnection()
        fun checkLocationPermissions()
    }

    interface VM {
        fun onTestBtnClick()
        fun startDownloadTest()
        fun startTestCountdown()
        fun getToken()
        fun getServers(token: String)
        fun getClosestServers()
        fun setApiForStsServers(closest5Servers: List<ServerBdo>)
        fun pickSmallestPing()
        fun getPingForClosestServers()
    }
}