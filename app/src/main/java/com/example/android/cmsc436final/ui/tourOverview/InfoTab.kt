package com.example.android.cmsc436final.ui.tourOverview

import android.content.Context
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
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
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


class InfoTab: Fragment(), CheckpointAdapter.ItemClickListener {
    private lateinit var mModel: SharedViewModel
    private var mCheckpointsRecycler: RecyclerView? = null
    private var mAdapter: CheckpointAdapter? = null
    private lateinit var checkpoints : List<Checkpoint>
    private lateinit var descriptionView : TextView
    private lateinit var peopleCompletedView : TextView
    private lateinit var chipGroup: ChipGroup


    companion object {
        private const val TAG = "InfoTab"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.tour_overview_info_tab, container, false)
        mCheckpointsRecycler = root.findViewById(R.id.recycler_checkpoints)
        mCheckpointsRecycler!!.layoutManager = LinearLayoutManager(context)
        descriptionView =root.findViewById(R.id.info_tour_description)
        peopleCompletedView = root.findViewById(R.id.info_ppl_completed)
        chipGroup = root.findViewById(R.id.chipGroup)

        mModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
        if(mModel.getCurrentTour() == null){
            mModel.getTour().observe(this, Observer { tour ->
                run {
                    loadScreen(tour)
                }
            })
        } else {
            loadScreen(mModel.getCurrentTour()!!)
        }

        return root
    }

    private fun loadScreen(tour: Tour){
        setupView(tour)
        initRecycler(tour)
    }

    private fun setupView(tour: Tour){
        descriptionView.text = tour.description
        val ppl = "People completed ${tour.pplCompleted}"
        peopleCompletedView.text = ppl

        // populate tags
        tour.tags?.forEach {
            var newChip = Chip(context)
            newChip.text = it
            chipGroup.addView(newChip)
        }
    }

    private fun initRecycler(tour: Tour){
        checkpoints = tour.checkpoints!!
        mAdapter = CheckpointAdapter(context, checkpoints)
        mCheckpointsRecycler!!.adapter = mAdapter
    }


//    TODO add navigation
    override fun onItemClick(view: View?, position: Int) {
        mModel.setCurrentCheckpointNum(position)
        findNavController().navigate(R.id.action_tour_overview_to_checkpoint_overview)
    }

}