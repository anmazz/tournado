package com.example.locationbasedtourguide.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.SharedViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class HomeFragment : Fragment(), OnMapReadyCallback {

    companion object{
        private const val LOCATION_REQUEST = 1

        private lateinit var mapView: MapView
        private const val TAG = "HomeFragment"
    }

    private lateinit var mSharedViewModel: SharedViewModel
    private lateinit var mGoogleMap: GoogleMap
    private var lat : Double = 0.0
    private var long : Double = 0.0

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mSharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        invokeLocationAction()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val button = root.findViewById<View>(R.id.testingButton)
        button.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_home_to_tour_overview)
        }
        return root
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
    }

    private fun invokeLocationAction(){
        if(hasPermission()){
            startLocationUpdates()
        } else {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_REQUEST
            )
        }

    }

    private fun startLocationUpdates() {
        mSharedViewModel.getLocationData().observe(this, Observer {
            lat = it.latitude
            long = it.longitude
            Log.i(TAG, "in startLocationUpdates")
            mGoogleMap.clear()
            mGoogleMap.addMarker(MarkerOptions().position(LatLng(lat, long)).title("Current Location"))
            mGoogleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        lat,
                        long
                    ), 13.0f
                )
            )

        })
    }


    private fun hasPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(context!!,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION) {
                invokeLocationAction()
            }
        }
    }


}
