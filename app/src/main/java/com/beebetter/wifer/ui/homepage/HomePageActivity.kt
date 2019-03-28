package com.beebetter.wifer.ui.homepage

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import com.beebetter.base.view.BaseActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tbruyelle.rxpermissions2.RxPermissions


class HomePageActivity : BaseActivity<com.beebetter.wifer.databinding.ActivityHomepageBinding,HomePageVM>() {
    override val layoutId = com.beebetter.wifer.R.layout.activity_homepage
    override val viewModelClass = HomePageVM::class
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val rxPermissions = RxPermissions(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.beebetter.wifer.R.layout.activity_homepage)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        rxPermissions
            .request(Manifest.permission.ACCESS_COARSE_LOCATION)
            .subscribe { granted ->
                if (granted) {
                    obtainLocalizacion()
                } else {
                    // Oups permission denied
                }
            }
    }

    @SuppressLint("MissingPermission")
    private fun obtainLocalizacion(){
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                viewModel.latitude = location?.latitude
                viewModel.longitude = location?.longitude
                viewModel.userLocation = location
               val latitude =  location?.latitude
                val longitude = location?.longitude
                Log.d("location",latitude.toString()
                        +" and longitude"+ longitude)
            }
    }
}
