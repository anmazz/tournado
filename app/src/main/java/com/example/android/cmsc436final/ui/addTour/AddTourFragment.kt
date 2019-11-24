package com.example.android.cmsc436final.ui.addTour

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
<<<<<<< HEAD
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
=======
import android.widget.*
>>>>>>> master
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.model.Checkpoint
<<<<<<< HEAD
=======
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
>>>>>>> master

class AddTourFragment : Fragment() {

    companion object{
        const val TAG = "AddTourFragment"
    }

    private lateinit var addTourViewModel: AddTourViewModel
<<<<<<< HEAD
    private lateinit var parentLinearLayout: LinearLayout
    private lateinit var addCheckpointButton: Button
    var numOfCheckpoints = 1


=======
    internal lateinit var tourName: EditText
    internal lateinit var tourDescrip: EditText
    internal lateinit var checkptName: EditText
    internal lateinit var checkptDesc: EditText
    internal lateinit var buttonAddCheckpoint: Button
    internal lateinit var buttonAddTour: Button
    internal lateinit var buttonAddLocation: Button

    internal lateinit var db: FirebaseFirestore
    private var dbTours: Query? = null
    private var dbUsers: Query? = null
>>>>>>> master

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View?
    {
        addTourViewModel =
            ViewModelProviders.of(this).get(AddTourViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_add_tour, container, false)
        Log.i(TAG, "onCreateView1")

        addCheckpointButton = root.findViewById(R.id.add_checkpoint_button)

        //Parent linear layout to dynamically add checkpoint views
        parentLinearLayout = root.findViewById(R.id.parent_linear_layout)
        Log.i(TAG, "onCreateView2")

        addCheckpointButton.setOnClickListener{
            onAddField(root, inflater)
        }



//        val textView: TextView = root.findViewById(R.id.text_add_tour)
//        addTourViewModel.text.observe(this, Observer {
//            textView.text = it
//        })

        // Get DB refs
        db = FirebaseFirestore.getInstance()
        dbTours = db.collection("tours")
        dbUsers = db.collection("users")

        // Get UI elements
        tourName = root.findViewById<View>(R.id.tour_name_et) as EditText
        tourDescrip = root.findViewById<View>(R.id.tour_description_et) as EditText
        checkptName = root.findViewById<View>(R.id.checkpoint_name_et) as EditText
        checkptDesc = root.findViewById<View>(R.id.checkpoint_desc) as EditText

        buttonAddCheckpoint = root.findViewById<View>(R.id.addCheckpoint) as Button
        buttonAddLocation = root.findViewById<View>(R.id.addLocation) as Button
        buttonAddTour = root.findViewById<View>(R.id.addTourButton) as Button

        return root
    }



    private fun onAddField(v : View, inflater: LayoutInflater){
        var view = inflater.inflate(R.layout.checkpoint_field, null)
        parentLinearLayout.addView(view, parentLinearLayout.childCount - 1)
    }
}