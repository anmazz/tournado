package com.example.android.cmsc436final

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.android.cmsc436final.Data.LocationData.LocationLiveData

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val locationData =
        LocationLiveData(
            application
        )

    fun getLocationData() = locationData
}
