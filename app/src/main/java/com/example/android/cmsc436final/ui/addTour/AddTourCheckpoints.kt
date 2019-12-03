package com.example.android.cmsc436final.ui.addTour

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.SharedViewModel
import com.example.android.cmsc436final.model.Checkpoint
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.GeoPoint
import java.util.*
import kotlin.collections.ArrayList

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


    // ArrayList of Checkpoints
    private lateinit var checkpoints: MutableList<Checkpoint>
    private val AUTOCOMPLETE_REQUEST_CODE = 1


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

        buttonAddCheckpoint = root.findViewById<View>(R.id.add_cp_button) as Button

        // list of checkpoints we keep adding to
        checkpoints = ArrayList()

        //---------MAP STUFF---------
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


        // The rest of the button listeners //

        buttonNext.setOnClickListener() {
            saveAndNext()
        }

        buttonAddPicture.setOnClickListener() {
            navigateToAddMedia()
        }

        buttonAddVideo.setOnClickListener() {
            navigateToAddVideo()
        }

        buttonAddAudio.setOnClickListener() {
            navigateToAddAudio()
        }

        buttonAddCheckpoint.setOnClickListener() {
            addCheckpoint()
        }

        return root
    }

    // TODO make sure this is adding just pictures
    private fun navigateToAddMedia(){
        findNavController().navigate(R.id.action_navigation_add_tour_to_navigation_add_media)
    }

    // TODO make sure this is adding just audio
    private fun navigateToAddAudio(){
        findNavController().navigate(R.id.action_navigation_add_tour_to_navigation_add_media)
    }

    // TODO make sure this is adding just video
    private fun navigateToAddVideo(){
        findNavController().navigate(R.id.action_navigation_add_tour_to_navigation_add_media)
    }


    fun saveAndNext() {
        // get strings from textboxes
        val tourNameStr = checkptName.text.toString().trim { it <= ' ' }
        val tourDescripStr = checkptDesc.text.toString()

        // add to viewModel
        sharedViewModel.addCheckpoints(checkpoints)
    }

    //    TODO navigate to the add tags page
    private fun navigateToAddTags(){
        findNavController().navigate(R.id.action_navigation_add_tour_to_navigation_add_media)
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
        //creating adapter using CheckPointList
        val lvCheckpoint = CheckPointList(activity!!, checkpoints)
        //attaching adapter to the listview
        listviewCP.adapter = lvCheckpoint

        checkptName.setText("")
        checkptDesc.setText("")
    }

}