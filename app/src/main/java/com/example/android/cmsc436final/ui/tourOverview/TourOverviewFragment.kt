package com.example.android.cmsc436final.ui.tourOverview

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.model.Checkpoint
import com.example.android.cmsc436final.model.Tour
import com.example.android.cmsc436final.ui.searchTour.SearchTourFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.PolyUtil
import org.json.JSONObject

class TourOverviewFragment(tour: Tour) : Fragment(), OnMapReadyCallback {
    private lateinit var mapRouteDrawer: MapRouteDrawer
    private var currTour = tour
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mapView: MapView

    companion object{
        private const val TAG = "TourOverviewFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_tour_overview, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapView = view.findViewById(R.id.map) as MapView
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
        mapRouteDrawer = MapRouteDrawer(currTour, context!!)

        Log.i(TAG, mapRouteDrawer.getURL())

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
            moveCameraToTourBounds()

        }, Response.ErrorListener {
            Log.e(TAG, "Error :(")
        }){}

        requestQueue.add(directionsRequest)

    }


    private fun moveCameraToTourBounds(){
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

    private fun createExample(): Tour {
        val checkPointList = mutableListOf<Checkpoint>()
        val checkpoint1 = Checkpoint("Stamp Student Union", GeoPoint(38.9882, -76.9447), "", null, null, null)
        checkPointList.add(checkpoint1)
        val checkpoint2 = Checkpoint("Eppley Recreation Center", GeoPoint(38.9936, -76.9452), "", null, null, null)
        checkPointList.add(checkpoint2)
        val checkpoint3 = Checkpoint("Prince Frederick", GeoPoint(38.9832, -76.9458), "", null, null, null)
        checkPointList.add(checkpoint3)
        val exampleTour = Tour("1", "UMD Tour", 0, "", checkPointList, null)
        return exampleTour
    }

}