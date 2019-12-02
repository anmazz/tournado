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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.SharedViewModel
import com.example.android.cmsc436final.adapter.CheckpointAdapter
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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 *
 * CLASS FOR THE INFO TAB OF TOUROVERVIEW
 *
 */

class InfoTab: Fragment(){
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
}