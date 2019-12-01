package com.example.locationbasedtourguide.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.SharedViewModel
import com.example.android.cmsc436final.adapter.TourAdapter
import com.example.android.cmsc436final.model.Checkpoint
import com.example.android.cmsc436final.model.Tour
import com.example.android.cmsc436final.ui.tourOverview.TourOverviewFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class HomeFragment : Fragment(), OnMapReadyCallback,
    TourAdapter.OnTourSelectedListener {

    companion object{
        private const val LOCATION_REQUEST = 1

        private lateinit var mapView: MapView
        private const val TAG = "HomeFragment"
        private const val LIMIT = 50
    }

    private lateinit var mModel: SharedViewModel
    private lateinit var mGoogleMap: GoogleMap
    private var lat : Double = 0.0
    private var long : Double = 0.0
    private var mFirestore: FirebaseFirestore? = null
    private var mQuery: Query? = null
    private var mToursRecycler: RecyclerView? = null
    private var mAdapter: TourAdapter? = null
    //private val mEmptyView: ViewGroup? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        invokeLocationAction()
        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter!!.startListening()
        }
    }

    override fun onStop(){
        super.onStop()
        if (mAdapter != null) {
            mAdapter!!.startListening()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        mToursRecycler = root.findViewById(R.id.recycler_tours)

        // Initialize Firestore and the main RecyclerView
        initFirestore()
        initRecyclerView()

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

    private fun initFirestore() {
        mFirestore = FirebaseFirestore.getInstance()
        // Get the 50 highest rated restaurants
        mQuery = mFirestore!!.collection("tours")
            .limit(LIMIT.toLong())
    }

    private fun initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView")
        }

        mAdapter = object: TourAdapter(mQuery, this) {
            override fun onDataChanged() { // Show/hide content if the query returns empty.
                if (itemCount == 0) {
                    mToursRecycler!!.visibility = View.GONE
                } else {
                    mToursRecycler!!.visibility = View.VISIBLE
                }
            }

            override fun onError(e: FirebaseFirestoreException?) { // Show a snackbar on errors
                Snackbar.make(
                    activity!!.findViewById<View>(android.R.id.content),
                    "Error: check logs for info.", Snackbar.LENGTH_LONG
                ).show()
            }
        }
        mToursRecycler!!.layoutManager = LinearLayoutManager(context)
        mToursRecycler!!.adapter = mAdapter
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
        mModel.getLocationData().observe(this, Observer {
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



    override fun onTourSelected(tour: DocumentSnapshot?) {
        lifecycleScope.launch {
            mModel.selectTour(tour!!.id)
        }
        val bundle = bundleOf("tourid" to tour!!.id)
        findNavController().navigate(R.id.action_navigation_home_to_tour_overview, bundle)
    }

}
