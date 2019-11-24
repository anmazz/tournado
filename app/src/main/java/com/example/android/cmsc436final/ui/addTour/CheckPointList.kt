package com.example.android.cmsc436final.ui.addTour

// Class to allow Checkpoints to be displayed in a list when creating tours
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.model.Checkpoint


class CheckPointList(private val context: Activity, internal var checkpoints: List<Checkpoint>) : ArrayAdapter<Checkpoint>(context, R.layout.checkpoint_list, checkpoints) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.checkpoint_list, null, true)

        val checkPointNameView = listViewItem.findViewById<View>(R.id.CheckPointName) as TextView

        val cp = checkpoints[position]
        checkPointNameView.text = cp.name

        return listViewItem
    }
}