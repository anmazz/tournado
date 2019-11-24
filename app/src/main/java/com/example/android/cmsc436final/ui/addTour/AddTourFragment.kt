package com.example.android.cmsc436final.ui.addTour

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.model.Checkpoint

class AddTourFragment : Fragment() {

    companion object{
        const val TAG = "AddTourFragment"
    }

    private lateinit var addTourViewModel: AddTourViewModel
    private lateinit var parentLinearLayout: LinearLayout
    private lateinit var addCheckpointButton: Button
    var numOfCheckpoints = 1



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



        return root
    }



    private fun onAddField(v : View, inflater: LayoutInflater){
        var view = inflater.inflate(R.layout.checkpoint_field, null)
        parentLinearLayout.addView(view, parentLinearLayout.childCount - 1)
    }
}