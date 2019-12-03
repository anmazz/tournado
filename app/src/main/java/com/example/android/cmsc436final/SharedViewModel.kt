package com.example.android.cmsc436final

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.cmsc436final.Data.LocationData.LocationLiveData
import com.example.android.cmsc436final.model.Checkpoint
import com.example.android.cmsc436final.model.Tour
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val locationData = LocationLiveData(application)
    private var toBeAdded = Tour()
    private var currentTour :Tour? = null
    private var currentCheckpointNum = 0

    fun setCurrentTour(tour : Tour){
        currentTour = tour
    }

    fun getCurrentTour(): Tour? {
        return currentTour
    }

    fun setCurrentCheckpointNum(num: Int){
        currentCheckpointNum = num
    }

    fun getCurrentCheckpointNum():Int{
        return currentCheckpointNum
    }

    val tourId = MutableLiveData<String>()
    private var tour = MutableLiveData<Tour>()

    fun getTourID(): LiveData<String> {
        return tourId
    }

    fun addID(id: String) {
        toBeAdded.id = id
    }

    fun addName(name: String) {
        toBeAdded.name = name
    }

    fun addPic(pic: String) {
        toBeAdded.pic = pic
    }

    fun addDescription(descr: String) {
        toBeAdded.description = descr
    }

    fun addCheckpoints(cps: List<Checkpoint>) {
        toBeAdded.checkpoints = cps
    }

    fun addTags(tags: List<String>) {
        toBeAdded.tags = tags
    }


    fun getAddedTour(): Tour {
        return toBeAdded
    }

    suspend fun selectTour(id : String) {
        tourId.value = id
        tour.value = loadTour(id)
        Log.i("select tour", "done")
    }


    fun getTour(): LiveData<Tour> {
        Log.i("get tour", "Data: "+ tour.value.toString())
        return tour
    }

    suspend fun loadTour(id: String): Tour? {
        val result = FirebaseFirestore.getInstance().collection("tours").document(id).get().await()
        Log.i("loadtour", "done")
        return result.toObject(Tour::class.java)
    }

    fun getLocationData() = locationData

}
