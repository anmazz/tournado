package com.example.android.cmsc436final.model


data class Tour (
    var name: String = "",
    var pplCompleted: Int = 0,
    var description: String = "",
    var checkpoints:  ArrayList<Checkpoint> = arrayListOf(),
    var tags: ArrayList<String> = arrayListOf()
)