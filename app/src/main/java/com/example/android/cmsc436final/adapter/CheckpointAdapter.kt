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
import com.example.android.cmsc436final.model.Checkpoint


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



class CheckpointAdapter(context: Context?, data: List<Checkpoint>) : RecyclerView.Adapter<CheckpointAdapter.ViewHolder?>() {
    private val mData: List<Checkpoint> = data
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mClickListener: ItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.item_checkpoint, parent, false)
        Log.i("CheckpointAdapter", "oncreateviewholder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val checkpoint = mData[position]
        holder.nameView.text = checkpoint.name
        Glide.with(holder.imageView.context).load(checkpoint.image).centerCrop().into(holder.imageView)
        //Retrieve starting checkpoint location's city
        val geocoder = Geocoder(holder.locationView.context)
        val lat = checkpoint.location.latitude
        val long = checkpoint.location.longitude
        holder.locationView.text = geocoder.getFromLocation(lat, long, 1)[0].locality
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var nameView: TextView = itemView.findViewById(R.id.checkpoint_item_name)
        var locationView: TextView = itemView.findViewById(R.id.checkpoint_item_location)
        var imageView: ImageView = itemView.findViewById(R.id.checkpoint_item_image)

        override fun onClick(view: View) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    // convenience method for getting data at click position
    fun getItem(id: Int): Checkpoint{
        return mData[id]
    }

    // allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    fun updateList(newData : List<Checkpoint>) {
        mData = newData
        notifyDataSetChanged()
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }


}