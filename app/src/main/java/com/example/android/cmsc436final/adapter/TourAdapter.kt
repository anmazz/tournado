package com.example.android.cmsc436final.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.model.Tour
import com.google.firebase.firestore.DocumentSnapshot

class TourAdapter(val tours : ArrayList<Tour>, val context: Context) : RecyclerView.Adapter<TourAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView? = null
        var nameView: TextView? = null
        var distanceView: TextView? = null
        var cityView: TextView? = null

        fun ViewHolder(itemView: View) {
//            super.itemView
            imageView = itemView.findViewById(R.id.image_tour)
            nameView = itemView.findViewById(R.id.tour_name_view)
            cityView = itemView.findViewById(R.id.tour_location)
            distanceView = itemView.findViewById(R.id.location_distance)
        }

        fun bind(
            snapshot: DocumentSnapshot
            //listener: com.google.firebase.example.fireeats.adapter.RestaurantAdapter.OnRestaurantSelectedListener?
        ) {

//            val restaurant: Restaurant? = snapshot.toObject<Restaurant>(Restaurant::class.java)
//            val resources = itemView.resources
//            // Load image
//            Glide.with(imageView!!.context)
//                .load(restaurant.getPhoto())
//                .into(imageView)
//            nameView.setText(restaurant.getName())
//            ratingBar.setRating(restaurant.getAvgRating() as Float)
//            cityView.setText(restaurant.getCity())
//            categoryView.setText(restaurant.getCategory())
//            numRatingsView!!.text = resources.getString(
//                R.string.fmt_num_ratings,
//                restaurant.getNumRatings()
//            )
//            priceView.setText(RestaurantUtil.getPriceString(restaurant))
//            // Click listener
//            itemView.setOnClickListener {
//                if (listener != null) {
//                    listener.onRestaurantSelected(snapshot)
//                }
//            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val tourView = LayoutInflater.from(parent.context).inflate(R.layout.item_tour, parent, false)
        return MyViewHolder(tourView)
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}