package com.example.android.cmsc436final.ui.tourOverview

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.SharedViewModel
import com.example.android.cmsc436final.model.Checkpoint
import com.example.android.cmsc436final.model.Tour
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.PolyUtil
import org.json.JSONObject


/**
 *
 * CLASS FOR THE MAP TAB OF TOUROVERVIEW
 *
 */


class MapTab : Fragment(), OnMapReadyCallback, LifecycleObserver {
    private lateinit var mapRouteDrawer: MapRouteDrawer
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var db: FirebaseFirestore
    private lateinit var mModel: SharedViewModel


    companion object{
        private const val TAG = "MapTag"
        fun newInstance(): MapTab = MapTab()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
        Log.i("test", mModel.toString())
        Log.i("test", mModel.getTour().toString())
        mModel.getTour().observe(this, Observer { tour ->
            run {
                drawOnMap(tour)
            }
        })


        val root = inflater.inflate(R.layout.tour_overview_map_tab, container, false)
        Log.i("test", "test2")
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapView = view.findViewById(R.id.map) as MapView
        Log.i("test", mapView.toString())
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)
        super.onViewCreated(view, savedInstanceState)
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
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
    }

    private fun drawOnMap(currTour: Tour){
        mapRouteDrawer = MapRouteDrawer(currTour, context!!)

        val requestQueue = Volley.newRequestQueue(context!!)
        val url = mapRouteDrawer.getURL()

        val path: MutableList<List<LatLng>> = ArrayList()
        val directionsRequest = object : StringRequest(Method.GET, url, Response.Listener<String> {
                response ->
            val jsonResponse = JSONObject(response)
            Log.i(TAG, "Response: %s".format(jsonResponse.toString()))
            // Get routes
            val routes = jsonResponse.getJSONArray("routes")
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            val steps = legs.getJSONObject(0).getJSONArray("steps")
            for (i in 0 until steps.length()) {
                val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                path.add(PolyUtil.decode(points))
            }
            for (i in 0 until path.size) {
                this.mGoogleMap.addPolyline(PolylineOptions().addAll(path[i]).color(Color.BLUE))
            }
            moveCameraToTourBounds(currTour)

        }, Response.ErrorListener {
            Log.e(TAG, "Error :(")
        }){}

        requestQueue.add(directionsRequest)
    }


    private fun moveCameraToTourBounds(currTour: Tour){
        val builder = LatLngBounds.Builder()
        for (marker: Checkpoint in currTour.checkpoints!!) {
            builder.include(LatLng(marker.location.latitude, marker.location.longitude))
        }
        val bounds = builder.build()
        val width = context!!.resources.displayMetrics.widthPixels
        val height = context!!.resources.displayMetrics.heightPixels
        val padding = (width * 0.20).toInt() // offset from edges of the map

        val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)

        mGoogleMap.animateCamera(cu)
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "OnPause")
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "OnResume")
        mapView.onResume()
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "OnStop")
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "OnDestroy")
        mapView.onDestroy()
    }

}