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
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.GeoPoint

class AddTourBasicInfo: Fragment() {

        private lateinit var sharedViewModel: SharedViewModel
        private lateinit var tourName: TextInputEditText
        private lateinit var tourDescrip: TextInputEditText
        private lateinit var buttonAddPicture: Button
        private lateinit var buttonNext: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_add_tour_1, container, false)

        // UI elements
        tourName = root.findViewById<View>(R.id.tour_name_) as TextInputEditText
        tourDescrip = root.findViewById<View>(R.id.tour_descrip) as TextInputEditText
        buttonAddPicture = root.findViewById<View>(R.id.add_tour_picture_button) as Button
        buttonNext = root.findViewById<View>(R.id.next_button) as Button


        buttonNext.setOnClickListener() {
            saveAndNext()
        }

//        TODO make sure this is adding just pictures
        buttonAddPicture.setOnClickListener() {
            navigateToAddMedia()
        }

        return root
    }

    //        TODO make sure this is adding just pictures

    private fun navigateToAddMedia(){
        //findNavController().navigate(R.id.action_navigation_add_tour_to_navigation_add_media)
    }

    fun saveAndNext() {
        // get strings from textboxes
        val tourNameStr = tourName.text.toString().trim { it <= ' ' }
        val tourDescripStr = tourDescrip.text.toString()

        // add to viewModel
        sharedViewModel.addName(tourNameStr)
        sharedViewModel.addDescription(tourDescripStr)
//        TODO add the picture to viewmodel

    }

//    TODO navigate to the add checkpoints page
    private fun navigateToAddCheckpoints(){
        //findNavController().navigate(R.id.action_navigation_add_tour_to_navigation_add_media)
    }

}