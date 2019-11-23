package com.example.locationbasedtourguide.ui.home

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleObserver


class HomeViewModel : ViewModel(), LifecycleObserver {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Home Fragment"
    }

    val text: LiveData<String> = _text
    private val currentLat = MutableLiveData<Int>()
    private val currentLong = MutableLiveData<Int>()
    private lateinit var mHandler: Handler

    internal val lat: LiveData<Int>
        get() = currentLat

    init {
        //
        currentLat.value = 38
        currentLong.value = 38
    }

    internal fun bindToFragmentLifecycle(homeFragment: HomeFragment) {
        homeFragment.lifecycle.addObserver(this)
    }



}
