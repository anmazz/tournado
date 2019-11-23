package com.example.android.cmsc436final.ui.addTour

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.android.cmsc436final.R

class AddTourFragment : Fragment() {

    private lateinit var addTourViewModel: AddTourViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addTourViewModel =
            ViewModelProviders.of(this).get(AddTourViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_add_tour, container, false)

        return root
    }
}