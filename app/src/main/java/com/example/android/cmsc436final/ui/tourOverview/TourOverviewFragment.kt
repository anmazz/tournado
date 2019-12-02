package com.example.android.cmsc436final.ui.tourOverview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.SharedViewModel
import com.example.android.cmsc436final.adapter.TabsAdapter
import com.example.android.cmsc436final.model.Tour
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

/**
 *
 * MAIN FRAGMENT FOR TOUR OVERVIEW
 * has info at the top and tabs
 *
 * */

class TourOverviewFragment : Fragment() {
    private lateinit var mapRouteDrawer: MapRouteDrawer
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var db: FirebaseFirestore
    private lateinit var tourImage: ImageView
    private lateinit var startButton: FloatingActionButton
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var mModel: SharedViewModel

    companion object{
        private const val TAG = "TourOverviewFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        // saved the id that the fragment passed to this activity
        var tourid = arguments?.getString("tourid")
        mModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        lifecycleScope.launch {
            mModel.selectTour(tourid!!)
        }
        Log.i("test", mModel.toString())
        mModel.getTour().observe(this, Observer { tour ->
            run {
                Log.i(TAG, tour.name)
                loadDataIntoFragment(tour)
                mModel.setCurrentTour(tour)
                Log.i(TAG + "Hello", mModel.getCurrentTour()!!.name)
            }
        })

        val root = inflater.inflate(R.layout.fragment_tour_overview, container, false)

        // UI ELEMENTS
//        tourImage = root.findViewById(R.id.tour_image)
        // set picture of collapsingAppBar using Glide
//        Glide.with(appBarImg.context).load(currTour!!.pic).centerCrop().into(appBarImg)
//        Picasso.get().load(currTour!!.pic).into(tourImage)

        startButton = root.findViewById(R.id.start_tour_button)
        startButton.setOnClickListener{
            findNavController().navigate(R.id.action_tour_overview_to_start_tour)
        }

        tabLayout = root.findViewById(R.id.tab_layout)
        viewPager = root.findViewById(R.id.viewpager)

        val fragM = getFragmentManager()

        val adapter = TabsAdapter(childFragmentManager)

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })


        return root
    }


    private fun loadDataIntoFragment(currTour: Tour){
        Log.i(TAG, "in load data into fragment")
        val imageView = activity!!.findViewById<ImageView>(R.id.tour_image)
        Glide.with(imageView.context).load(currTour.pic).centerCrop().into(imageView)
        val nameText = activity!!.findViewById<TextView>(R.id.tour_name)
        nameText.text = currTour.name
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "OnPause")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "OnResume")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "OnStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        viewPager.adapter
        Log.i(TAG, "OnDestroy")
    }
}