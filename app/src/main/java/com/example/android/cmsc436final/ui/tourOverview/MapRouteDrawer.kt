package com.example.android.cmsc436final.ui.tourOverview

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.model.Tour
import java.lang.StringBuilder

class MapRouteDrawer(tour: Tour, context: Context) {
    private val tour: Tour = tour
    private val checkpoints = tour.checkpoints!!
    private val origin = checkpoints[0]
    private val dest = checkpoints[checkpoints.size-1]
    private val context = context

    companion object{
        private val TAG = "MapRouteDrawer"
    }


    fun getURL() : String {
        val originString = "origin=" + origin.location.latitude + "," + origin.location.longitude
        val destString = "destination=" + dest.location.latitude + "," + dest.location.longitude

        val key = "&key="+ context.getString(R.string.api_key)
        val mode = "mode=walking"
        val waypoints = getWaypoints()
        val params = "$originString&$destString&$mode"
        return "https://maps.googleapis.com/maps/api/directions/json?$params$waypoints$key"
    }

    private fun getWaypoints(): String? {
        val wayptString =  StringBuilder("waypoints=")

        //Invariant that tours must have at least 2 checkpoints
        if(checkpoints.size == 2){
            return ""
        }

        for(i in 1 until checkpoints.size - 1){
            val latLong  = StringBuilder(checkpoints[i].location.latitude.toString() + "," + checkpoints[i].location.longitude.toString())
            if(i != checkpoints.size - 2){
                latLong.append("|")
            }
            val encoded = Uri.encode(latLong.toString())
            wayptString.append(encoded)
        }
        Log.i(TAG, "Waypoints string = $wayptString")
        return "&$wayptString"
    }
}