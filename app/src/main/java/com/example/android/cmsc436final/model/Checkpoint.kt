package com.example.android.cmsc436final.model

import com.google.firebase.firestore.GeoPoint

data class Checkpoint (
    var name: String = "",
//    TODO: get current location
    var location: GeoPoint = GeoPoint(0.0,0.0),
    var media: ArrayList<String> = arrayListOf(),
    var description:  String = ""
)
