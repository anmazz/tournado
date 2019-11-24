package com.example.android.cmsc436final.ui.addTour

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddTourViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the add tour Fragment"
    }
    val text: LiveData<String> = _text

}