package com.example.locationbasedtourguide.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.android.cmsc436final.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions




class HomeFragment : Fragment(), OnMapReadyCallback {

    companion object{
        private lateinit var homeViewModel: HomeViewModel
        private const val UPDATE_INTERVAL = (5000).toLong()
        private const val FASTEST_INTERVAL: Long = 2000 /* 2 sec */
        private const val REQUEST_CODE = 1

        private var latitude = -35.0
        private var longitude = 149.0

        private lateinit var mapView: MapView
        private var mLocationRequest: LocationRequest? = null
        private const val TAG = "HomeFragment"
    }

    private lateinit var mGoogleMap: GoogleMap

    override fun onStart() {
        super.onStart()
        startLocationUpdates()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapView = view.findViewById(R.id.map) as MapView
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)


        super.onViewCreated(view, savedInstanceState)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        Log.i(TAG, "IN ON MAP READY")
        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mGoogleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mGoogleMap = googleMap
        mGoogleMap.addMarker(MarkerOptions().position(LatLng(latitude, longitude)).title("Current Location"))
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitude, longitude)))
        Log.i(TAG, "Latitude: " + latitude)
        Log.i(TAG, "Longitude: " + longitude)
    }

    private fun startLocationUpdates() {
        Log.i(TAG, "In start location updates")
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = UPDATE_INTERVAL
            setFastestInterval(FASTEST_INTERVAL)
        }

        // initialize location setting request builder object
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        // initialize location service object
        val settingsClient = LocationServices.getSettingsClient(activity!!)
        settingsClient!!.checkLocationSettings(locationSettingsRequest)

        // call register location listener
        registerLocationListener()
    }

    private fun registerLocationListener() {
        Log.i(TAG, "In register location listener")
        // initialize location callback object
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                onLocationChanged(locationResult!!.lastLocation)
            }
        }

        if (Build.VERSION.SDK_INT >= 26 && checkPermission()) {
            LocationServices.getFusedLocationProviderClient(activity!!).requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper())
        }
    }

    fun onLocationChanged(location: Location) {
        Log.i(TAG, "In on location changed. Loc lat: ${location.latitude}, loc long: ${location.longitude}")
        val loc = LatLng(location.latitude, location.longitude)
//        latitude = location.latitude
//        longitude = location.longitude

        mGoogleMap.clear()
        mGoogleMap.addMarker(MarkerOptions().position(loc).title("Current Location"))
        mGoogleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    loc.latitude,
                    loc.longitude
                ), 13.0f
            )
        )


    }

    fun clearMap(){
        mGoogleMap.clear()
    }

    private fun checkPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(context!!,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            requestPermissions()
            return false
        }
    }

    private fun requestPermissions() {
       ActivityCompat.requestPermissions(activity!!,
           arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION) {
                registerLocationListener()
            }
        }
    }

}
