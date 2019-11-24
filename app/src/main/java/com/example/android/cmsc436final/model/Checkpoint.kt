package com.example.android.cmsc436final.model

import com.google.firebase.firestore.GeoPoint

data class Checkpoint (
    var name: String = "",
    var location: GeoPoint = GeoPoint(0.0,0.0),
    var description:  String = "",
    var media: List<String>? = null
)
