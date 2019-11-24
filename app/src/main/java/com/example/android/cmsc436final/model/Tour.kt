package com.example.android.cmsc436final.model


data class Tour (
    var name: String = "",
    var pplCompleted: Int = 0,
    var description: String = "",
    var tags: List<Checkpoint>? = null
)