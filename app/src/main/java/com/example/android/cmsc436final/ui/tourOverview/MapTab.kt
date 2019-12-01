package com.example.android.cmsc436final.ui.tourOverview

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.model.Checkpoint
import com.example.android.cmsc436final.model.Tour
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.PolyUtil
import org.json.JSONObject


/**
 *
 * CLASS FOR THE MAP TAB OF TOUROVERVIEW
 *
 */


class MapTab : Fragment(), OnMapReadyCallback {
    private lateinit var mapRouteDrawer: MapRouteDrawer
    private var currTour: Tour? = null
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var db: FirebaseFirestore


    companion object{
        private const val TAG = "TourOverviewFragment"
        fun newInstance(): MapTab = MapTab()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        // saved the id that the fragment passed to this activity
//        var tourid = arguments?.getString("tourid")
//        getTourById(tourid!!)
        val root = inflater.inflate(R.layout.tour_overview_map_tab, container, false)



        return root
    }

    private fun getTourById(tourId: String) {
        db = FirebaseFirestore.getInstance()
        val docRef = db.collection("tours").document(tourId)
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                currTour = document.toObject(Tour::class.java)
                drawOnMap()
            } else {
                Log.d(TAG, "No such document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
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
    }

    private fun drawOnMap(){
        mapRouteDrawer = MapRouteDrawer(currTour!!, context!!)

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
        for (marker: Checkpoint in currTour!!.checkpoints!!) {
            builder.include(LatLng(marker.location.latitude, marker.location.longitude))
        }
        val bounds = builder.build()
        val width = context!!.resources.displayMetrics.widthPixels
        val height = context!!.resources.displayMetrics.heightPixels
        val padding = (width * 0.20).toInt() // offset from edges of the map

        val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)

        mGoogleMap.animateCamera(cu)
    }

//    private fun createExample(): Tour {
//        val checkPointList = mutableListOf<Checkpoint>()
//        val checkpoint1 = Checkpoint("Stamp Student Union", GeoPoint(38.9882, -76.9447), "", "", "", "")
//        checkPointList.add(checkpoint1)
//        val checkpoint2 = Checkpoint("Eppley Recreation Center", GeoPoint(38.9936, -76.9452), "", "", "", "")
//        checkPointList.add(checkpoint2)
//        val checkpoint3 = Checkpoint("Prince Frederick", GeoPoint(38.9832, -76.9458), "", "", "", "")
//        checkPointList.add(checkpoint3)
//        val exampleTour = Tour("1", "UMD Tour", "", 0, "", checkPointList)
//        return exampleTour
//    }

}