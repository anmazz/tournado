package com.example.android.cmsc436final.ui.searchTour

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.adapter.TourAdapter
import com.google.firebase.firestore.DocumentSnapshot
import com.squareup.picasso.Picasso


class SearchTourAdapter/*(parentFragment: SearchTourFragment)*/ :PagedListAdapter<TourDataOrganization, TourViewHolder>(
    SearchTourAdapter
) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_small, parent, false)

        return TourViewHolder(view)
    }

    override fun onBindViewHolder(holder: TourViewHolder, position: Int) {
        val product = getItem(position)

        if (product != null) holder.bind(product)
    }

    companion object : DiffUtil.ItemCallback<TourDataOrganization>() {

        override fun areItemsTheSame(
            oldItem: TourDataOrganization,
            newItem: TourDataOrganization
        ): Boolean {
            return oldItem::class == newItem::class
        }

        override fun areContentsTheSame(
            oldItem: TourDataOrganization,
            newItem: TourDataOrganization
        ): Boolean {
            return oldItem.name == newItem.name
        }
    }
}