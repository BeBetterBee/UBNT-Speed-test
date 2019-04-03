package com.beebetter.wifer.ui.homepage

interface HomePage {
    interface View{
        fun checkInternetConnection()

    }

    interface VM{
        fun onTestBtnClick()
    }
}