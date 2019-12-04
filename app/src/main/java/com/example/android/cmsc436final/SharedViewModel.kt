package com.example.android.cmsc436final

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.cmsc436final.Data.LocationData.LocationLiveData
import com.example.android.cmsc436final.model.Checkpoint
import com.example.android.cmsc436final.model.Tour
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val locationData = LocationLiveData(application)
    private var toBeAdded = Tour()
    private var toBeUploaded: MutableList<Array<Uri?>> = ArrayList()
    private var currentTour :Tour? = null
    private var checkpointToBeAdded = Checkpoint()
    private var currentCheckpointNum = 0
    private var currChkptNumStartTour = 0
    private var checkpointList : ArrayList<Checkpoint> = ArrayList()
    private var dialogShown: Array<Boolean>? = null
    private var location : GeoPoint? = null

    private val liveImageHash = MutableLiveData<HashMap<String, String>>()
    private var hash = HashMap<String, String>()

    fun setLocation(geoPoint: GeoPoint){
        location = geoPoint
    }
    fun getLocation(): GeoPoint? = location

    fun updateCheckpointList(list : ArrayList<Checkpoint>){
        checkpointList = list
    }

    fun getCheckpoinlist(): ArrayList<Checkpoint>{
        return checkpointList
    }

    fun resetHash(){
        hash = HashMap<String, String>()
    }

    fun setImageUrl(url: String) {
        hash.set("image", url)
        liveImageHash.value = hash
    }

    fun setVideoUrl(url: String){
        hash.set("video", url)
        liveImageHash.value = hash
    }

    fun setAudioUrl(url: String){
        hash.set("audio", url)
        liveImageHash.value = hash
    }

    fun getStaticHash() : HashMap<String, String> {
        return hash
    }

    fun getHash() : LiveData<HashMap<String, String>> {
        return liveImageHash
    }

    fun reset(){
        dialogShown = null
        currChkptNumStartTour = 0
    }

    fun setDialogShown(arr : Array<Boolean>){
        dialogShown = arr
    }

    fun getDialogShown() : Array<Boolean>? {
        return dialogShown
    }

    private val imageurl = MutableLiveData<String>()

    fun getImageUrl(): LiveData<String> {
        return imageurl
    }


    fun setCurrentTour(tour : Tour){
        currentTour = tour
    }

    fun getCurrentTour(): Tour? {
        return currentTour
    }

    fun setCurrChkptNumStartTour(num : Int){
        currChkptNumStartTour = num
    }

    fun getCurrChkptNumStartTour() : Int{
        return currChkptNumStartTour
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




// ------TOUR INFORMATION--------
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

    fun addCheckpoints(cps: ArrayList<Checkpoint>) {
        Log.i("SharedViewModel", cps.toString())
        toBeAdded.checkpoints = cps
        Log.i("SharedViewModel", toBeAdded.checkpoints.toString())
    }

    fun addUris(uris: MutableList<Array<Uri?>>) {
        toBeUploaded = uris
    }

    fun addTags(tags: List<String>) {
        toBeAdded.tags = tags
    }


    fun getCreatedTour(): Tour {
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
