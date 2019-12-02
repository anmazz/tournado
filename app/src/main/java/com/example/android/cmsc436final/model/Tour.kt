package com.example.android.cmsc436final.model

data class Tour (
    var id: String = "",
    var name: String = "",
    var pic: String = "",
    var pplCompleted: Int = 0,
    var description: String = "",
    var checkpoints: List<Checkpoint>? = null,
    var tags: List<String>? = null


)
