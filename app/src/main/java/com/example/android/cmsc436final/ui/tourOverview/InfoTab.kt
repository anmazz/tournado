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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.PolyUtil
import org.json.JSONObject

/**
 *
 * CLASS FOR THE INFO TAB OF TOUROVERVIEW
 *
 */

class InfoTab: Fragment() {
    private lateinit var mapRouteDrawer: MapRouteDrawer
    private var currTour: Tour? = null
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var db: FirebaseFirestore
    private lateinit var tourImage: ImageView
    private lateinit var startButton: Toolbar

    companion object {
        private const val TAG = "TourOverviewFragment"
        fun newInstance(): InfoTab = InfoTab()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // saved the id that the fragment passed to this activity
//        var tourid = arguments?.getString("tourid")
//        getTourById(tourid!!)
        val root = inflater.inflate(R.layout.tour_overview_info_tab, container, false)


        return root
    }

    private fun getTourById(tourId: String) {
        db = FirebaseFirestore.getInstance()
        val docRef = db.collection("tours").document(tourId)
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                currTour = document.toObject(Tour::class.java)
            } else {
                Log.d(TAG, "No such document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }


}