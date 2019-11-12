package com.example.android.cmsc436final

import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.*


import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.fragment.app.Fragment
import com.example.locationbasedtourguide.ui.home.HomeFragment


class MainActivity : AppCompatActivity() {

    companion object{
        private const val UPDATE_INTERVAL = (10 * 1000).toLong()  /* 10 secs */
        private const val FASTEST_INTERVAL: Long = 2000 /* 2 sec */
        private const val REQUEST_CODE = 1

        private var latitude = 0.0
        private var longitude = 0.0
        private lateinit var mGoogleMap: GoogleMap
        private var mLocationRequest: LocationRequest? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)


        //Loading home fragment as default
        loadFragment(HomeFragment())

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_search_tour,
                R.id.navigation_add_tour,
                R.id.navigation_user_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

//    override fun onStart() {
//        super.onStart()
//        startLocationUpdates()
//    }


    private fun loadFragment(fragment: Fragment?): Boolean {
        //switching fragment
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment!!)
                .commit()
            return true
        }
        return false
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
//    override fun onMapReady(googleMap: GoogleMap) {
//        mGoogleMap = googleMap
//
//        // Add a marker in Sydney and move the camera
////        val sydney = LatLng(-34.0, 151.0)
////        mGoogleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
////        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//        mGoogleMap = googleMap
//        mGoogleMap.addMarker(MarkerOptions().position(LatLng(latitude, longitude)).title("Current Location"))
//    }
//
//    private fun startLocationUpdates() {
//        mLocationRequest = LocationRequest.create()
//        mLocationRequest!!.run {
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//            interval = UPDATE_INTERVAL
//            setFastestInterval(FASTEST_INTERVAL)
//        }
//
//        // initialize location setting request builder object
//        val builder = LocationSettingsRequest.Builder()
//        builder.addLocationRequest(mLocationRequest!!)
//        val locationSettingsRequest = builder.build()
//
//        // initialize location service object
//        val settingsClient = LocationServices.getSettingsClient(this)
//        settingsClient!!.checkLocationSettings(locationSettingsRequest)
//
//        // call register location listener
//        registerLocationListener()
//    }
//
//    private fun registerLocationListener() {
//        // initialize location callback object
//        val locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult?) {
//                onLocationChanged(locationResult!!.lastLocation)
//            }
//        }
//
//        if (Build.VERSION.SDK_INT >= 26 && checkPermission()) {
//            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper())
//        }
//    }
//
//    //
//    private fun onLocationChanged(location: Location) {
//        val loc = LatLng(location.latitude, location.longitude)
//        mGoogleMap.clear()
//        mGoogleMap.addMarker(MarkerOptions().position(loc).title("Current Location"))
//        mGoogleMap.animateCamera(
//            CameraUpdateFactory.newLatLngZoom(
//                LatLng(
//                    loc.latitude,
//                    loc.longitude
//                ), 13.0f
//            )
//        )
//    }
//
//    private fun checkPermission(): Boolean {
//        if (ContextCompat.checkSelfPermission(this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            return true
//        } else {
//            requestPermissions()
//            return false
//        }
//    }
//
//    private fun requestPermissions() {
//        ActivityCompat.requestPermissions(this, arrayOf("Manifest.permission.ACCESS_FINE_LOCATION"), REQUEST_CODE)
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray) {
//
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE) {
//            if (permissions[0] == android.Manifest.permission.ACCESS_FINE_LOCATION) {
//                registerLocationListener()
//            }
//        }
//    }
}
