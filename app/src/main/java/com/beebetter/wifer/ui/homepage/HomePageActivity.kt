package com.beebetter.wifer.ui.homepage

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import com.beebetter.base.view.BaseActivity
import com.beebetter.wifer.R
import com.beebetter.wifer.databinding.ActivityHomepageBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tbruyelle.rxpermissions2.RxPermissions


class HomePageActivity : BaseActivity<ActivityHomepageBinding,HomePageVM>() {
    override val layoutId = R.layout.activity_homepage
    override val viewModelClass = HomePageVM::class

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val rxPermissions = RxPermissions(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        rxPermissions
            .request(Manifest.permission.ACCESS_FINE_LOCATION)
            .subscribe { granted ->
                if (granted) {
                    obtainLocalization()
                } else {
                    // Oups permission denied
                }
            }
    }

    @SuppressLint("MissingPermission")
    private fun obtainLocalization(){
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                viewModel.userLocation = location
                viewModel.getToken()
               val latitude =  location?.latitude
                val longitude = location?.longitude
                Log.d("location",latitude.toString()
                        +" and longitude"+ longitude)
            }
    }
}
