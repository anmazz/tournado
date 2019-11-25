package com.example.android.cmsc436final.ui.userProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class UserProfileViewModel : ViewModel() {
    val user = FirebaseAuth.getInstance().currentUser

    private val name = MutableLiveData<String>().apply {
        //value = user.
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is user profile Fragment"
    }
    val text: LiveData<String> = _text
}