
package com.example.android.cmsc436final.ui.addTour


import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.cmsc436final.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
//import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.model.Checkpoint
import com.example.android.cmsc436final.model.Tour
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.Place.*
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_add_tour.*
//for searching with algolia
import com.algolia.search.client.ClientSearch
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName

import com.google.firebase.firestore.*
import io.ktor.client.features.logging.LogLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.json
import java.util.*
import kotlin.collections.ArrayList


class AddTourFragment : Fragment() {

    private lateinit var addTourViewModel: AddTourViewModel
    private lateinit var tourName: TextInputEditText
    private lateinit var tourDescrip: EditText
    private lateinit var checkptName: EditText
    private lateinit var checkptDesc: EditText
    private lateinit var location: GeoPoint

    private lateinit var buttonAddCheckpoint: Button
    private lateinit var buttonAddTour: Button
    private lateinit var buttonAddLocation: Button
    private lateinit var buttonAddMedia: Button
    private lateinit var buttonCancel: Button

    //for searching
    val client = ClientSearch(ApplicationID("MLWVY1AHOC"), APIKey("84275e27b9ffaecb0207751b4b2349c6"), LogLevel.ALL)
    val index = client.initIndex(IndexName("tours"))


    internal lateinit var listviewCP: ListView


    internal lateinit var db: FirebaseFirestore
    // reference to tours collection
    private var dbTours: CollectionReference? = null
    // reference to specific users' document
    private var dbUser: DocumentReference? = null
    private lateinit var checkpoints: MutableList<Checkpoint>
    private lateinit var dummyTags: MutableList<String>
    private val AUTOCOMPLETE_REQUEST_CODE = 1

    companion object{
        private val TAG = "AddTourFragment"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addTourViewModel =
            ViewModelProviders.of(this).get(AddTourViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_add_tour, container, false)

        // Get DB refs
        db = FirebaseFirestore.getInstance()
        dbTours = db.collection("tours")

        // need uid of currentUser to get specific document
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        dbUser = db.collection("users").document(uid)

        // Get UI elements
        tourName = root.findViewById<View>(R.id.tour_name_et_2) as TextInputEditText
        tourDescrip = root.findViewById<View>(R.id.tour_description_et) as EditText
        checkptName = root.findViewById<View>(R.id.checkpoint_name_et) as EditText
        checkptDesc = root.findViewById<View>(R.id.checkpoint_description_et) as EditText

        buttonAddCheckpoint = root.findViewById<View>(R.id.add_checkpoint_button) as Button
        buttonAddCheckpoint = root.findViewById(R.id.add_checkpoint_button)
        buttonAddLocation = root.findViewById<View>(R.id.add_location_button) as Button
        buttonAddTour = root.findViewById<View>(R.id.add_tour_button) as Button
        buttonAddMedia = root.findViewById(R.id.add_media_button)
        buttonCancel =  root.findViewById(R.id.cancel_tour_button)
        listviewCP = root.findViewById<View>(R.id.listViewCheckpoints) as ListView

        //For add location intent
        var fields = Arrays.asList(Place.Field.ID, Place.Field.NAME)

        // Start the autocomplete intent.
        var intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields).build(activity!!)

        //Navigation buttons
        buttonAddLocation.setOnClickListener{
            if (!Places.isInitialized()) {
                context?.let { it1 -> Places.initialize(it1, getString(R.string.api_key), Locale.US) }
            }
            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        buttonAddMedia.setOnClickListener{
            navigateToAddMedia()
        }

        buttonAddTour.setOnClickListener {
            addTour()
        }


        buttonAddCheckpoint.setOnClickListener {
            addCheckpoint()
        }

        buttonCancel.setOnClickListener {
            cancel()
        }

        //list of checkpoints for the listview
        checkpoints = ArrayList()


        // TODO add tags support
//        TODO fill in dummy values
        dummyTags = arrayListOf("school", "nature", "test")
        location = GeoPoint(38.9882, 76.9447)
        return root
    }

    private fun addCheckpoint() {
        val name = checkptName.text.toString()
//        TODO get location
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

    // Method for adding Tour to databases
    private fun addTour() {
        //getting the values to save
        val tourNameStr = tourName.text.toString().trim { it <= ' ' }
        val tourDescripStr = tourDescrip.text.toString()
        val picString = ""

        //checking if the value is provided
        if (!TextUtils.isEmpty(tourNameStr) && !TextUtils.isEmpty(tourDescripStr) &&
            checkpoints.isNotEmpty()) {

            // Create an ID key for our new Tour Document
            val id = db.collection("tours").document().id

            //creating a Tour Object

//            val newTour = Tour(id, tourNameStr, "", 0, tourDescripStr, checkpoints, dummyTags)
            val newTour = Tour(id, tourNameStr, picString, 0, tourDescripStr, checkpoints, dummyTags)

            // Add newTour obj to the database in the tours collection
            dbTours!!.document(id).set(newTour)
            // Add to the list of createdTours for specific user
            dbUser!!.update("toursCreated", FieldValue.arrayUnion(newTour))


            // Add JSON object to algolia for searching
            val newGuy = json {
                "name" to tourNameStr
                "tags" to dummyTags.toString()
            }
            //TODO: make sure this is uploading to algolia too
           val uploadToAlgolia = GlobalScope.launch {
                index.saveObject(newGuy)
            }

            tourName.setText("")
            tourDescrip.setText("")

            //displaying a success toast
            Toast.makeText(activity, "Tour created!", Toast.LENGTH_LONG).show()

            //if the value is not given displaying a toast
        } else  {
            Toast.makeText(activity, "Please complete the fields", Toast.LENGTH_LONG).show()
        }
    }


    private fun cancel() {
        tourName.setText("")
        tourDescrip.setText("")
        checkptName.setText("")
        checkptDesc.setText("")
        checkpoints.clear()

        val lvCheckpoint = CheckPointList(activity!!, checkpoints)
        //attaching adapter to the listview
        listviewCP.adapter = lvCheckpoint

    }

    private fun navigateToAddMedia(){
       // findNavController().navigate(R.id.action_navigation_add_tour_to_navigation_add_media)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val place = data?.let { Autocomplete.getPlaceFromIntent(it) }
                if (place != null) {
                    Log.i(TAG, "Place: " + place.name + ", " + place.id)
                    Toast.makeText(activity, "Place: " + place.name + ", " + place.id, Toast.LENGTH_LONG).show()
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Toast.makeText(activity, "Error with autocomplete. Please try again.", Toast.LENGTH_LONG)
                var status = Autocomplete.getStatusFromIntent(data!!)
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    }

}