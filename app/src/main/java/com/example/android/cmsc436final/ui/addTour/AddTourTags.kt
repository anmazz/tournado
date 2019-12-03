package com.example.android.cmsc436final.ui.addTour

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.SharedViewModel
import com.example.android.cmsc436final.model.Checkpoint
import com.example.android.cmsc436final.model.Tour
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

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

class AddTourTags: Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var tagInput: TextInputEditText
    //    Buttons

    private lateinit var buttonAddTag: Button
    private lateinit var buttonNext: Button
    private lateinit var buttonCancel: Button

    private lateinit var chipGroup: ChipGroup
    private lateinit var chip: Chip

    private lateinit var tags: MutableList<String>

    //for searching
    val client = ClientSearch(ApplicationID("MLWVY1AHOC"), APIKey("84275e27b9ffaecb0207751b4b2349c6"), LogLevel.ALL)
    val index = client.initIndex(IndexName("tours"))

    // DATABASE STUFF
    internal lateinit var db: FirebaseFirestore
    // reference to tours collection
    private var dbTours: CollectionReference? = null
    // reference to specific users' document
    private var dbUser: DocumentReference? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedViewModel =
            ViewModelProviders.of(this).get(sharedViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_add_tour_3tags, container, false)


        // UI elements
        tagInput = root.findViewById<View>(R.id.tag_input) as TextInputEditText

        // buttons

        buttonAddTag = root.findViewById<View>(R.id.add_tour_button) as Button
        buttonNext = root.findViewById<View>(R.id.next_button) as Button
        buttonCancel =  root.findViewById(R.id.cancel_button)


        chipGroup = root.findViewById<View>(R.id.chip_group) as ChipGroup

        // button listeners
        buttonAddTag.setOnClickListener() {
            addTag()
        }

        buttonNext.setOnClickListener() {
            addToDatabase()
        }

        chip.setOnClickListener() {
            deleteTag(chip)
        }
//        TODO navigate to home button
        buttonCancel.setOnClickListener {
            tagInput.setText("")
        }


        // Get DB refs
        db = FirebaseFirestore.getInstance()
        dbTours = db.collection("tours")

        // need uid of currentUser to get specific document
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        dbUser = db.collection("users").document(uid)

        return root
    }


    private fun addTag() {

        val tagStr = tagInput.text.toString()
        // add to the arraylist if not empty
        if (tagStr.isNotEmpty() && !tags.contains(tagStr)) {
            tags.add(tagStr)
        }

        chip = layoutInflater.inflate(R.layout.single_chip, chipGroup, false) as Chip
        chip.text = tagStr
        chipGroup.addView(chip)

        tagInput.setText("")
    }

    private fun deleteTag(chip: Chip) {
        chipGroup.removeView(chip)
        tags.remove(chip.text)
    }


    private fun addToDatabase() {
        // get strings from textbox

        // add to viewModel
        sharedViewModel.addTags(tags)

        // now get viewModel
        var tourToAdd = sharedViewModel.getCreatedTour()

        //getting the values to save

        //checking if the value is provided
        if (!TextUtils.isEmpty(tourToAdd.name) && !TextUtils.isEmpty(tourToAdd.description) &&
            tourToAdd.checkpoints!!.isNotEmpty()
        ) {

            // Create an ID key for our new Tour Document
            val id = db.collection("tours").document().id

            tourToAdd.id = id

            // Add newTour obj to the database in the tours collection
            dbTours!!.document(id).set(tourToAdd)
            // Add to the list of createdTours for specific user
            dbUser!!.update("toursCreated", FieldValue.arrayUnion(tourToAdd))


                // Add JSON object to algolia for searching
                val newGuy = json {
                    "name" to tourToAdd.name
                    "tags" to tourToAdd.tags.toString()
                }
                //TODO: make sure this is uploading to algolia too
                val uploadToAlgolia = GlobalScope.launch {
                    index.saveObject(newGuy)
                }


            //displaying a success toast
            Toast.makeText(activity, "Tour created!", Toast.LENGTH_LONG).show()

            //if the value is not given displaying a toast
        } else {
            Toast.makeText(activity, "Please complete the fields", Toast.LENGTH_LONG).show()
        }
    }


    //    TODO navigate to the addTour home page
    private fun navigateToAddTags() {
        findNavController().navigate(R.id.action_navigation_add_tour_to_navigation_add_media)
    }
}
