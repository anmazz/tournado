package com.example.android.cmsc436final.ui.addTour

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.model.Checkpoint
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AddTourFragment : Fragment() {

    private lateinit var addTourViewModel: AddTourViewModel
    internal lateinit var tourName: EditText
    internal lateinit var tourDescrip: EditText
    internal lateinit var buttonAddCheckpoint: Button
    internal lateinit var buttonAddTour: Button

    internal lateinit var db: FirebaseFirestore
    private var dbTours: Query? = null
    private var dbUsers: Query? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addTourViewModel =
            ViewModelProviders.of(this).get(AddTourViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_add_tour, container, false)

//        val textView: TextView = root.findViewById(R.id.text_add_tour)
//        addTourViewModel.text.observe(this, Observer {
//            textView.text = it
//        })

        // Get DB refs
        db = FirebaseFirestore.getInstance()
        dbTours = db.collection("tours")
        dbUsers = db.collection("users")

        // Get UI elements
        tourName = findViewById<View>(R.id.editText) as EditText
        tourDescrip = findViewById<View>(R.id.editText2) as EditText
//        buttonAddCheckpoint = findViewById<View>(R.id.listViewAuthors) as Button
//        buttonAddTour = findViewById<View>(R.id.buttonAddAuthor) as Button
        
        return root
    }
}