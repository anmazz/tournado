package com.example.android.cmsc436final.model

data class User (
    var name: String = "",
    var email: String = "",
    var description:  String = "",
    var toursCreated: List<Tour>? = null,
    var toursCompleted: List<Tour>? = null
)