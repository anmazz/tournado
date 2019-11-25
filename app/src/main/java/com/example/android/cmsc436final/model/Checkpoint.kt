package com.example.android.cmsc436final.model

import com.google.firebase.firestore.GeoPoint

data class Checkpoint (
    var name: String = "",
    var location: GeoPoint = GeoPoint(0.0,0.0),
    var description:  String = "",
    var images: List<String>? = null,
    var audio: List<String>? = null,
    var video: List<String>? = null
)
