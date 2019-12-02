package com.example.android.cmsc436final.ui.tourOverview

import android.graphics.Color
import android.icu.text.IDNA
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.SharedViewModel
import com.example.android.cmsc436final.adapter.CheckpointAdapter
import com.example.android.cmsc436final.adapter.TourAdapter
import com.example.android.cmsc436final.model.Checkpoint
import com.example.android.cmsc436final.model.Tour
import com.example.locationbasedtourguide.ui.home.HomeFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.maps.android.PolyUtil
import org.json.JSONObject

/**
 *
 * CLASS FOR THE INFO TAB OF TOUROVERVIEW
 *
 */

class InfoTab: Fragment() {
    private lateinit var mModel: SharedViewModel

    private var currTour: Tour? = null
    private var mFirestore: FirebaseFirestore? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var tourImage: ImageView
    private lateinit var startButton: Toolbar
    private var mCheckpointsRecycler: RecyclerView? = null
    private var mAdapter: CheckpointAdapter? = null
    private var mQuery: Query? = null


    companion object {
        private const val TAG = "TourOverviewFragment"
        private const val LIMIT = 50
        fun newInstance(): InfoTab = InfoTab()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)
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
        mCheckpointsRecycler = root.findViewById(R.id.recycler_checkpoints)

        // Initialize Firestore and the main RecyclerView
        initFirestore()
        initRecyclerView()


        return root
    }

    override fun onStart() {
        super.onStart()
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

    private fun initFirestore() {
        mQuery = mFirestore!!.collection("tours")
            .limit(InfoTab.LIMIT.toLong())
    }

    private fun initRecyclerView() {
        if (mQuery == null) {
            Log.w(InfoTab.TAG, "No query, not initializing RecyclerView")
        }

        mAdapter = object: CheckpointAdapter(mQuery, this) {
            override fun onDataChanged() { // Show/hide content if the query returns empty.
                if (itemCount == 0) {
                    mCheckpointsRecycler!!.visibility = View.GONE
                } else {
                    mCheckpointsRecycler!!.visibility = View.VISIBLE
                }
            }

            override fun onError(e: FirebaseFirestoreException?) { // Show a snackbar on errors
                Snackbar.make(
                    activity!!.findViewById<View>(android.R.id.content),
                    "Error: check logs for info.", Snackbar.LENGTH_LONG
                ).show()
            }
        }
        mCheckpointsRecycler!!.layoutManager = LinearLayoutManager(context)
        mCheckpointsRecycler!!.adapter = mAdapter
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