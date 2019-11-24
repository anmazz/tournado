
package com.example.android.cmsc436final.ui.addTour


import android.content.Context
import android.content.Intent

import android.os.Bundle
import android.text.TextUtils
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_add_tour.*

import com.google.firebase.firestore.*


class AddTourFragment : Fragment() {

    private lateinit var addTourViewModel: AddTourViewModel
    internal lateinit var tourName: EditText
    internal lateinit var tourDescrip: EditText
    internal lateinit var checkptName: EditText
    internal lateinit var checkptDesc: EditText
    internal lateinit var location: GeoPoint

    internal lateinit var buttonAddCheckpoint: Button
    internal lateinit var buttonAddTour: Button
    internal lateinit var buttonAddLocation: Button
    private lateinit var buttonAddMedia: Button
    private lateinit var buttonCancel: Button


    internal lateinit var listviewCP: ListView


    internal lateinit var db: FirebaseFirestore
    // reference to tours collection
    private var dbTours: CollectionReference? = null
    // reference to specific users' document
    private var dbUser: DocumentReference? = null
    internal lateinit var checkpoints: MutableList<Checkpoint>

    internal lateinit var dummyTags: MutableList<String>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addTourViewModel =
            ViewModelProviders.of(this).get(AddTourViewModel::class.java)

        val root = inflater.inflate(com.example.android.cmsc436final.R.layout.fragment_add_tour, container, false)


        //Parent linear layout to dynamically add checkpoint views
//        parentLinearLayout = root.findViewById(com.example.android.cmsc436final.R.id.parent_linear_layout)

//        buttonAddCheckpoint.setOnClickListener{
//            onAddField(root, inflater)
//        }


//        val textView: TextView = root.findViewById(R.id.text_add_tour)
//        addTourViewModel.text.observe(this, Observer {
//            textView.text = it
//        })

        // Get DB refs
        db = FirebaseFirestore.getInstance()
        dbTours = db.collection("tours")

        // need uid of currentUser to get specific document
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        dbUser = db.collection("users").document(uid)

        // Get UI elements
        tourName = root.findViewById<View>(com.example.android.cmsc436final.R.id.tour_name_et) as EditText
        tourDescrip = root.findViewById<View>(com.example.android.cmsc436final.R.id.tour_description_et) as EditText
        checkptName = root.findViewById<View>(com.example.android.cmsc436final.R.id.checkpoint_name_et) as EditText
        checkptDesc = root.findViewById<View>(com.example.android.cmsc436final.R.id.checkpoint_description_et) as EditText

        buttonAddCheckpoint = root.findViewById<View>(com.example.android.cmsc436final.R.id.add_checkpoint_button) as Button
        buttonAddCheckpoint = root.findViewById(com.example.android.cmsc436final.R.id.add_checkpoint_button)
        buttonAddLocation = root.findViewById<View>(com.example.android.cmsc436final.R.id.add_location_button) as Button
        buttonAddTour = root.findViewById<View>(com.example.android.cmsc436final.R.id.add_tour_button) as Button
        buttonAddMedia = root.findViewById(R.id.add_media_button)
        buttonCancel =  root.findViewById(com.example.android.cmsc436final.R.id.cancel_tour_button)
        listviewCP = root.findViewById<View>(com.example.android.cmsc436final.R.id.listViewCheckpoints) as ListView

        // OnClickListener for addTour Button
        buttonAddTour.setOnClickListener {
            addTour()
        }


        buttonAddMedia.setOnClickListener{
            navigateToAddMedia()
        }
//        buttonAddTour = root.findViewById<View>(R.id.add_tour_button) as Button

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
        val newCP = Checkpoint(name, location, description, null, null, null)

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

        //checking if the value is provided
        if (!TextUtils.isEmpty(tourNameStr) && !TextUtils.isEmpty(tourDescripStr) &&
            checkpoints.isNotEmpty()) {

            // Create an ID key for our new Tour Document
            val id = db.collection("tours").document().id

            //creating a Tour Object
            val newTour = Tour(id, tourNameStr, 0, tourDescripStr, checkpoints, dummyTags)

            // Add newTour obj to the database in the tours collection
            dbTours!!.document(id).set(newTour)
            // Add to the list of createdTours for specific user
            dbUser!!.update("toursCreated", FieldValue.arrayUnion(newTour))


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
        findNavController().navigate(com.example.android.cmsc436final.R.id.action_navigation_add_tour_to_navigation_add_media)
    }

}