package com.example.android.cmsc436final.ui.startTour

import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.cmsc436final.*
import com.example.android.cmsc436final.model.Tour
import com.example.android.cmsc436final.ui.tourOverview.TourOverviewFragment
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class StartTourFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mModel: SharedViewModel
    private lateinit var mapView: MapView
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var currTour: Tour
    private var currentCheckpoint = 0
    private lateinit var dialogShown : Array<Boolean>


    companion object{
        const val TAG = "StartTour"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_start_tour, container, false)
        mModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
        mModel.setCurrentCheckpointNum(0)
        mapView = root.findViewById(R.id.map_start_tour)

        //Get loaded current tour saved into viewModel
        currTour = mModel.getCurrentTour()!!

        dialogShown = Array(currTour.checkpoints!!.size) {false}

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)
        super.onViewCreated(view, savedInstanceState)

        //Checks if user is nearby checkpoint
        mModel.getLocationData().observe(this, Observer {
                location -> run{
            val current = Location("current").apply {
                this.latitude = location.latitude
                this.longitude = location.longitude
            }
            val checkpoint = Location("checkpoint").apply {
                this.latitude = currTour.checkpoints!![currentCheckpoint].location.latitude
                this.longitude = currTour.checkpoints!![currentCheckpoint].location.longitude
            }

            if(current.distanceTo(checkpoint) < 50 && !dialogShown[currentCheckpoint]){
                Log.i(TAG, "success listener")
                Toast.makeText(context, "SUCCESS!", Toast.LENGTH_LONG)
                dialogShown[currentCheckpoint] = true
                val onCheckpoint = currTour.checkpoints!![currentCheckpoint]
                val title = "Checkpoint ${currentCheckpoint+1} reached!"
                val body = onCheckpoint.name
                showDialog(title, body)
            }
        }})
    }

    //TODO: calculate when user has completed all checkpoints
    private fun showDialog(title: String, body: String) {
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_checkpoint)

        val titleView = dialog.findViewById(R.id.tvTitle) as TextView
        val bodyView = dialog.findViewById(R.id.tvBody) as TextView
        titleView.text = title
        bodyView.text = body
        val yesBtn = dialog .findViewById(R.id.btn_yes) as Button
        yesBtn.setOnClickListener {
            currentCheckpoint++
            mModel.setCurrentCheckpointNum(currentCheckpoint)
            dialog.dismiss()
        }
        dialog.show()

    }
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        //Draws polylines on the map to show location route
        Utils().drawOnMap(currTour, activity!!, mGoogleMap)
        Utils().moveCameraToTourBounds(currTour, activity!!, mGoogleMap)
        mGoogleMap.isMyLocationEnabled = true
    }

}