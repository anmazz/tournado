package com.example.android.cmsc436final.model

data class User (
    var name: String = "",
    var email: String = "",
    var description:  String = "",
    var toursCreated: ArrayList<Tour> = arrayListOf(),
    var toursCompleted: ArrayList<Tour> = arrayListOf()
)