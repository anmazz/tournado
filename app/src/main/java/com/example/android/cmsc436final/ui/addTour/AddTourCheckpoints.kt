package com.example.android.cmsc436final.ui.addTour

import android.app.AlertDialog
import android.os.Bundle
import android.provider.MediaStore
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.VideoView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.SharedViewModel
import com.example.android.cmsc436final.adapter.CheckpointAdapter
import com.example.android.cmsc436final.model.Checkpoint
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.GeoPoint
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.add_image_dialogue.view.*

class AddTourCheckpoints: Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var checkptName: TextInputEditText
    private lateinit var checkptDesc: TextInputEditText
//    Buttons
    private lateinit var buttonAddLocation: Button
    private lateinit var buttonAddPicture: Button
    private lateinit var buttonAddVideo: Button
    private lateinit var buttonAddAudio: Button
    private lateinit var buttonAddCheckpoint: Button
    private lateinit var buttonNext: Button

    private lateinit var buttonCancel: Button


    private lateinit var selectedPic: ImageView
    private lateinit var selectedVideo: VideoView
    private lateinit var selectedAudio: MediaStore.Audio
    private val TAG = "In addTourCheckpoints"

    private lateinit var location: GeoPoint
    private var mCheckpointsRecycler: RecyclerView? = null



    // ArrayList of Checkpoints
    private lateinit var checkpoints: MutableList<Checkpoint>
    private val AUTOCOMPLETE_REQUEST_CODE = 1

    companion object{
        private val TAG = "AddTourCheckpoints"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedViewModel =
            ViewModelProviders.of(this).get(sharedViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_add_tour_2checkpoints, container, false)


        // UI elements
        checkptName = root.findViewById<View>(R.id.cp_name) as TextInputEditText
        checkptDesc = root.findViewById<View>(R.id.cp_descrip) as TextInputEditText

        // buttons
        buttonAddLocation = root.findViewById<View>(R.id.add_cp_location_button) as Button
        buttonAddPicture = root.findViewById<View>(R.id.add_cp_picture_button) as Button
        buttonAddVideo = root.findViewById<View>(R.id.add_cp_video_button) as Button
        buttonAddAudio = root.findViewById<View>(R.id.add_cp_audio_button) as Button


        buttonNext = root.findViewById<View>(R.id.next_button) as Button
        buttonCancel = root.findViewById<View>(R.id.cancel_button) as Button

        buttonAddCheckpoint = root.findViewById<View>(R.id.add_cp_button) as Button

        // list of checkpoints we keep adding to
        checkpoints = ArrayList()


        mCheckpointsRecycler = root.findViewById(R.id.recycler_checkpoints)


        //---------MAP STUFF---------//
        //For add location intent
        var fields = Arrays.asList(Place.Field.ID, Place.Field.NAME)

        // Start the autocomplete intent.
        var intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields).build(activity!!)

        buttonAddLocation.setOnClickListener{
            if (!Places.isInitialized()) {
                context?.let { it1 -> Places.initialize(it1, getString(R.string.api_key), Locale.US) }
            }
            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        buttonNext.setOnClickListener() {
            saveAndNext()
        }

        buttonAddPicture.setOnClickListener() {

            addPicture()
        }
        buttonAddVideo.setOnClickListener() {
            addAudio()
        }
        buttonAddAudio.setOnClickListener() {
            addVideo()
        }

        buttonAddVideo.setOnClickListener() {
            //navigateToAddVideo()
        }

        buttonAddAudio.setOnClickListener() {
            //navigateToAddAudio()
        }

        buttonAddCheckpoint.setOnClickListener() {
            addCheckpoint()
        }

//        TODO navigate to home
        buttonCancel.setOnClickListener() {
            checkptName.setText("")
            checkptDesc.setText("")
            navigateToHome()
        }

        return root
    }

    // TODO make sure this is adding just pictures
//    private fun navigateToAddMedia(){
//        findNavController().navigate(R.id.action_navigation_add_tour_to_navigation_add_media)
//    }
//
//    // TODO make sure this is adding just audio
//    private fun navigateToAddAudio(){
//        findNavController().navigate(R.id.action_navigation_add_tour_to_navigation_add_media)
//    }
//
//    // TODO make sure this is adding just video
//    private fun navigateToAddVideo(){
//        findNavController().navigate(R.id.action_navigation_add_tour_to_navigation_add_media)
//    }

    private fun addPicture(){
            Log.i(TAG, "Im about to reset password")
            val addImageView = LayoutInflater.from(context).inflate(R.layout.add_image_dialogue, null)
            val builder = AlertDialog.Builder(context)
                .setView(addImageView)
                .setTitle("Enter email for reset instructions")
            val mAlertDialog = builder.show()
        addImageView.selectImageButton.setOnClickListener{
                //mAlertDialog.dismiss() do this last but before it make sure to change cancel button to done
                //actually reset here
//                val email = addImageView.resetPasswordEmailEditText.text.toString()
//                auth.sendPasswordResetEmail(email).addOnCompleteListener{ task ->
//                    if(task.isSuccessful){
//                        Toast.makeText(applicationContext, "Reset instructions have been sent to email", Toast.LENGTH_LONG).show()
//                    }
//                }
            }

            addImageView.cancelPicSelectButton.setOnClickListener {
                mAlertDialog.dismiss()
            }

    }

    // TODO make sure this is adding just audio
    private fun addAudio(){

    }

    // TODO make sure this is adding just video
    private fun addVideo(){

    }


    fun saveAndNext() {
        // add to viewModel
        sharedViewModel.addCheckpoints(checkpoints)
        navigateToAddTags()
    }

    private fun navigateToAddTags(){
        findNavController().navigate(R.id.action_add_tour2_to_add_tour3)
    }

//    TODO fix this
    private fun navigateToHome(){
        findNavController().navigate(R.id.action_add_tour3_to_navigation_home)
    }


    private fun addCheckpoint() {
        val name = checkptName.text.toString()

        val description = checkptDesc.text.toString()
//        TODO figure out how to save media
//        images = arr
//        audio:  MutableList<String>
//        video: MutableList<String>
        val newCP = Checkpoint(name, location, description, "", "", "")

        // need to add this checkpoint to tour arrayList
        checkpoints.add(newCP)

        // TO display added checkpoints
        //creating adapter using CheckpointAdapter
        val adapter = CheckpointAdapter(activity!!, checkpoints)
        //attaching adapter to the listview
        mCheckpointsRecycler!!.adapter = adapter
        mCheckpointsRecycler!!.layoutManager = LinearLayoutManager(context)

        checkptName.setText("")
        checkptDesc.setText("")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val place = data?.let { Autocomplete.getPlaceFromIntent(it) }
                if (place != null) {
                    Log.i(TAG, "Place: " + place.name + ", " + place.id)
                    location = GeoPoint(place.latLng!!.latitude, place.latLng!!.longitude)
                    Toast.makeText(activity, "Place: " + place.name + ", " + place.id, Toast.LENGTH_LONG).show()
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Toast.makeText(activity, "Error with autocomplete. Please try again.", Toast.LENGTH_LONG)
                var status = Autocomplete.getStatusFromIntent(data!!)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    }

}
