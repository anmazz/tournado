package com.example.android.cmsc436final.ui.searchTour

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.algolia.instantsearch.helper.android.highlighting.toSpannedString
import com.example.android.cmsc436final.model.Tour
import kotlinx.android.synthetic.main.list_item_small.view.*
import kotlin.coroutines.coroutineContext


class TourViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(tour: TourDataOrganization) {
        view.itemName.text = tour.highlightedName?.toSpannedString() ?: tour.name
        view.setOnClickListener{
            //TODO:
            displayToast(tour.name)
        }


    }
    fun displayToast(name:String) {
        Log.i("got one!!!","grabbed"+name)
    }

//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val name: TextView
//        private val image: ImageView
//        fun bind(item: ContentItem, listener: OnItemClickListener) {
//            name.setText(item.name)
//            Picasso.with(itemView.context).load(item.imageUrl).into(image)
//            itemView.setOnClickListener(object : OnClickListener() {
//                fun onClick(v: View?) {
//                    listener.onItemClick(item)
//                }
//            })
//        }
//
//        init {
//            name = itemView.findViewById(R.id.name)
//            image = itemView.findViewById(R.id.image) as ImageView
//        }
//    }
}