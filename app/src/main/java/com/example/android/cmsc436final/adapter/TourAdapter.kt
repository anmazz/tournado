package com.example.android.cmsc436final.adapter

import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.model.Tour
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query

/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * RecyclerView adapter for a list of Restaurants.
 */
open class TourAdapter(query: Query?, private val mListener: OnTourSelectedListener) :
    FirestoreAdapter<TourAdapter.ViewHolder>(query) {


    interface OnTourSelectedListener {
        fun onTourSelected(tour: DocumentSnapshot?)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return ViewHolder(
            inflater.inflate(
                R.layout.item_tour,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getSnapshot(position), mListener)
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.tour_item_image)
        var nameView: TextView = itemView.findViewById(R.id.tour_item_name)
        var locationView: TextView = itemView.findViewById(R.id.tour_item_location)
        fun bind(
            snapshot: DocumentSnapshot,
            listener: OnTourSelectedListener?
        ) {
            val tour = snapshot.toObject(Tour::class.java)
            // Load image
            Glide.with(imageView.context).load(tour!!.pic).centerCrop().into(imageView)
            //Set nameView text
            nameView.text = tour.name

            //Retrieve starting checkpoint location's city
            val geocoder = Geocoder(itemView.context)
           // Log.i("Adapter", "Location is: " + tour.checkpoints!![0].location.toString())
//            val lat = tour.checkpoints!![0].location.latitude
//            val long = tour.checkpoints!![0].location.longitude
//            locationView.text = geocoder.getFromLocation(lat, long, 1)[0].locality

            // Click listener
            itemView.setOnClickListener { listener?.onTourSelected(snapshot) }
        }
    }

}