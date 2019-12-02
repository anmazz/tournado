package com.example.android.cmsc436final.ui.searchTour

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.cmsc436final.R


class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_search_tour)

        showTourFragment()
    }

    fun showFacetFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, FacetFragment())
            .addToBackStack("facet")
            .commit()
    }

    fun showTourFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, SearchTourFragment())
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        supportFragmentManager.popBackStack()
        return super.onSupportNavigateUp()
    }
}