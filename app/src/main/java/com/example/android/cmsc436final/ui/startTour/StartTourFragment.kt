package com.example.android.cmsc436final.ui.startTour

import android.app.Dialog
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.android.cmsc436final.*
import com.example.android.cmsc436final.model.Tour
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback


class StartTourFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mModel: SharedViewModel
    private lateinit var mapView: MapView
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var currTour: Tour
    private var currentCheckpoint = 0
    private lateinit var dialogShown : Array<Boolean>
    private lateinit var nameView: TextView
    private lateinit var imageView :ImageView


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
        val what = parentFragment
        mapView = root.findViewById(R.id.map_start_tour)

        //Set up next checkpoint view
        nameView = root.findViewById(R.id.checkpoint_item_name2)
        imageView = root.findViewById(R.id.checkpoint_item_image2)

        //Get loaded current tour saved into viewModel
        currTour = mModel.getCurrentTour()!!
        currentCheckpoint = mModel.getCurrChkptNumStartTour()


        fillInNextCheckpointView()
        Log.i(TAG, "in oncreateview")
        if(mModel.getDialogShown() == null) {
            dialogShown = Array(currTour.checkpoints!!.size) {false}
        } else {
            dialogShown = mModel.getDialogShown()!!
        }
        mModel.setDialogShown(dialogShown)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "in onviewcreated")

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
            dialogShown = mModel.getDialogShown()!!
            if(current.distanceTo(checkpoint) < 50 && !dialogShown[currentCheckpoint]){
                Log.i(TAG, "success listener")
                Toast.makeText(context, "SUCCESS!", Toast.LENGTH_LONG)
                dialogShown[currentCheckpoint] = true
                mModel.setDialogShown(dialogShown)
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
            mModel.setCurrentCheckpointNum(currentCheckpoint)
            mModel.setCurrChkptNumStartTour(++currentCheckpoint)
            Log.i(TAG, "Num start tour: " + mModel.getCurrChkptNumStartTour())
            Log.i(TAG, "in yes button on click")
            findNavController().navigate(R.id.action_start_tour_to_checkpoint_overview)
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

    private fun fillInNextCheckpointView(){
        val nextCheckpoint = currTour.checkpoints!![mModel.getCurrChkptNumStartTour()]
        nameView.text = nextCheckpoint.name
        Glide.with(imageView.context).load(nextCheckpoint.image).centerCrop().into(imageView)
    }

    override fun onPause(){
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "In onResume")
    }

}