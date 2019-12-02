package com.example.android.cmsc436final.ui.addTour

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.android.cmsc436final.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.GeoPoint

class AddTourBasicInfo: Fragment() {

        private lateinit var addTourViewModel: AddTourViewModel
        private lateinit var tourName: TextInputEditText
        private lateinit var tourDescrip: TextInputEditText
        private lateinit var buttonAddPicture: Button
        private lateinit var buttonCancel: Button


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addTourViewModel =
            ViewModelProviders.of(this).get(AddTourViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_add_tour_1, container, false)
        return root
    }
}