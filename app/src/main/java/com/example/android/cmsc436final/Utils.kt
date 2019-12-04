package com.example.android.cmsc436final

import android.content.Context
import android.graphics.Color
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.android.cmsc436final.model.Checkpoint
import com.example.android.cmsc436final.model.Tour
import com.example.android.cmsc436final.ui.tourOverview.MapRouteDrawer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import org.json.JSONObject

class Utils {

    fun drawOnMap(currTour: Tour, context : Context, mGoogleMap: GoogleMap){
        val mapRouteDrawer = MapRouteDrawer(currTour, context)
        val TAG = "drawOnMap"

        for(marker: Checkpoint in currTour.checkpoints!!){
            mGoogleMap.addMarker(MarkerOptions().position(
                LatLng(marker.location.latitude, marker.location.longitude)
            ).title(marker.name)
            )
        }

        val requestQueue = Volley.newRequestQueue(context)
        val url = mapRouteDrawer.getURL()

        val path: MutableList<List<LatLng>> = ArrayList()
        val directionsRequest = object : StringRequest(Method.GET, url, Response.Listener<String> {
                response ->
            val jsonResponse = JSONObject(response)
            Log.i(TAG, "Response: %s".format(jsonResponse.toString()))
            // Get routes
            val routes = jsonResponse.getJSONArray("routes")
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            val steps = legs.getJSONObject(0).getJSONArray("steps")
            for (i in 0 until steps.length()) {
                val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                path.add(PolyUtil.decode(points))
            }
            for (i in 0 until path.size) {
                mGoogleMap.addPolyline(PolylineOptions().addAll(path[i]).color(Color.BLUE))
            }

        }, Response.ErrorListener {
            Log.e(TAG, "Error :(")
        }){}

        requestQueue.add(directionsRequest)
    }


    fun moveCameraToTourBounds(currTour: Tour, context : Context, mGoogleMap: GoogleMap){
        val builder = LatLngBounds.Builder()
        for (marker: Checkpoint in currTour.checkpoints!!) {
            builder.include(LatLng(marker.location.latitude, marker.location.longitude))
        }
        val bounds = builder.build()
        val width = context.resources.displayMetrics.widthPixels
        val height = context.resources.displayMetrics.heightPixels
        val padding = (width * 0.40).toInt() // offset from edges of the map

        val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)

        mGoogleMap.animateCamera(cu)
    }



    fun moveCameraFocusCheckpoints(currTour: Tour, context : Context, currentcheckpoint: Int, mGoogleMap: GoogleMap){
        val builder = LatLngBounds.Builder()
        if(currentcheckpoint == 0){
            moveCameraFocusUserLocFirstCheckpoint(currTour, context, currentcheckpoint, mGoogleMap)
        }
        for(i in currentcheckpoint until currentcheckpoint+2){
            builder.include(LatLng(currTour.checkpoints!![i].location.latitude, currTour.checkpoints!![i].location.longitude))
        }

        val bounds = builder.build()
        val width = context.resources.displayMetrics.widthPixels
        val height = context.resources.displayMetrics.heightPixels
        val padding = (width * 0.30).toInt() // offset from edges of the map

        val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)

        mGoogleMap.animateCamera(cu)

    }

    private fun moveCameraFocusUserLocFirstCheckpoint(currTour: Tour, context : Context, currentcheckpoint: Int, mGoogleMap: GoogleMap){

    }
}
