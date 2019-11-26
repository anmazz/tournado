package com.example.android.cmsc436final.ui.searchTour

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
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.SharedViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions



class SearchTourFragment : Fragment(), OnMapReadyCallback {


    companion object{
        private lateinit var mapView: MapView
        private const val TAG = "SearchTourFragment"
    }

    private lateinit var searchTourViewModel: SearchTourViewModel
    private lateinit var mSharedViewModel: SharedViewModel
    private lateinit var mGoogleMap: GoogleMap
    private var lat : Double = 0.0
    private var long : Double = 0.0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        searchTourViewModel =
//            ViewModelProviders.of(this).get(SearchTourViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_search_tour, container, false)
//        searchTourViewModel.text.observe(this, Observer {
//
//        })
        Log.i(TAG, "In onCreateView")


        if (savedInstanceState != null) {
            // Restore last state for checked position.
            lat = savedInstanceState.getDouble("lat")
            long = savedInstanceState.getDouble("long")
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapView = view.findViewById(R.id.map) as MapView
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)
        Log.i(TAG, "In onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i(TAG, "In onActivityCreated")
        mSharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        startLocationUpdates()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        Log.i(TAG, "IN ON MAP READY")

        mGoogleMap = googleMap
    }


    private fun startLocationUpdates() {
        mSharedViewModel.getLocationData().observe(this, Observer {
            lat = it.latitude
            long = it.longitude
            Log.i(TAG, "in startLocationUpdates")
            moveCamera(lat, long)
        })
    }


    private fun moveCamera(latitude : Double, longitude: Double){
        mGoogleMap.clear()
        mGoogleMap.addMarker(MarkerOptions().position(LatLng(latitude, longitude)).title("Current Location"))
        mGoogleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    latitude,
                    longitude
                ), 13.0f
            )
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble("lat", lat)
        outState.putDouble("long", long)
    }
}