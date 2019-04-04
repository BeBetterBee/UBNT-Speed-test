package com.beebetter.wifer.ui.homepage

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.beebetter.base.view.BaseActivity
import com.beebetter.wifer.R
import com.beebetter.wifer.databinding.ActivityHomepageBinding
import com.beebetter.wifer.util.PingHelper.Companion.isNetworkAvailable
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tbruyelle.rxpermissions2.RxPermissions


class HomePageActivity : BaseActivity<ActivityHomepageBinding, HomePageVM>(), HomePage.View {
    override val layoutId = com.beebetter.wifer.R.layout.activity_homepage
    override val viewModelClass = HomePageVM::class

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val rxPermissions = RxPermissions(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkInternetConnection()
    }

    override fun checkInternetConnection() {
        if (!isNetworkAvailable(this)) {
            showDialogForNoInternet()
        } else {
            checkLocationPermissions()
        }
    }

    private fun showDialogForNoInternet() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.dialog_no_internet)
        builder.setPositiveButton(R.string.dialog_no_internet_btn) { dialog, _ ->
            checkInternetConnection()
            dialog.cancel()
        }
        builder.create().show()
    }

    private fun showDialogForNoLocationPermission() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.dialog_no_location)
        builder.setPositiveButton(R.string.dialog_no_location_btn) { dialog, _ ->
            viewModel.getToken()
            dialog.cancel()
        }
        builder.create().show()
    }

    override fun checkLocationPermissions() {
        rxPermissions
            .request(Manifest.permission.ACCESS_FINE_LOCATION)
            .subscribe { granted ->
                if (granted) {
                    obtainLocalization()
                } else {
                    setDummyLocation()
                    showDialogForNoLocationPermission()
                }
            }
    }

    /**
     * Sets dummy Location if user disables access to device location
     * */
    private fun setDummyLocation() {
        viewModel.userLocation = Location("r")
        viewModel.userLocation?.latitude = 47.9999
        viewModel.userLocation?.longitude = 19.56656
    }


    @SuppressLint("MissingPermission")
    private fun obtainLocalization() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                viewModel.userLocation = location
                viewModel.getToken()
            }
    }
}
