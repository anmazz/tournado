package com.example.android.cmsc436final.ui.tourOverview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
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
    private lateinit var startButton: FloatingActionButton
    private lateinit var mModel: SharedViewModel
    private lateinit var mapButton : Button
    private lateinit var infoButton : Button

    companion object{
        private const val TAG = "TourOverviewFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_tour_overview, container, false)

        // saved the id that the fragment passed to this activity
        var tourid = arguments?.getString("tourid")
        mModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        //UI
        startButton = root.findViewById(R.id.start_tour_button)
        mapButton = root.findViewById(R.id.show_map_button)
        infoButton = root.findViewById(R.id.show_info_button)


        //Selects tour a
        lifecycleScope.launch {
            mModel.selectTour(tourid!!)
        }
        Log.i("test", mModel.toString())
        mModel.getTour().observe(this, Observer { tour ->
            run {
                Log.i(TAG, tour.name)
                loadDataIntoFragment(tour)
                mModel.setCurrentTour(tour)
            }
        })

        //On click listeners
        startButton.setOnClickListener{
            findNavController().navigate(R.id.action_tour_overview_to_start_tour)
        }

        infoButton.setOnClickListener{
            frameLoader(InfoTab())
        }

        mapButton.setOnClickListener{
            frameLoader(MapTab())
        }

        //Load map into frame when tour overview is opened
        frameLoader(MapTab())

        return root
    }



    private fun frameLoader(fragment : Fragment){
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.tab_frame, fragment)
        transaction.commit()
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
//        viewPager.adapter
        Log.i(TAG, "OnDestroy")
    }
}