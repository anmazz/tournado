package com.example.android.cmsc436final.ui.searchTour

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.android.cmsc436final.R
import com.google.android.gms.maps.GoogleMap



class SearchTourFragment : Fragment() {

    private lateinit var searchTourViewModel: SearchTourViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchTourViewModel =
            ViewModelProviders.of(this).get(SearchTourViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_search_tour, container, false)
        searchTourViewModel.text.observe(this, Observer {

        })


        return root
    }

}