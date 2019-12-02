package com.example.android.cmsc436final

import android.app.PendingIntent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.android.cmsc436final.ui.searchTour.SearchTourFragment
import com.example.android.cmsc436final.ui.searchTour.FacetFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    companion object{
        const val TAG= "MainActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_search_tour,
                R.id.navigation_add_tour,
                R.id.navigation_user_profile
            )
        )
        supportActionBar?.hide()

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
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
