package com.example.android.cmsc436final.ui.userProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.android.cmsc436final.R
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileFragment : Fragment() {
    //if sign out button pressed - sign out
    //if change profile pic button pressed - change profile pic

    private lateinit var userProfileViewModel: UserProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userProfileViewModel =
            ViewModelProviders.of(this).get(UserProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_user_profile, container, false)

        //val textView: TextView = root.findViewById(R.id.ratingText)
        userProfileViewModel.text.observe(this, Observer {
            //textView.text = it
        })
        return root
    }



}