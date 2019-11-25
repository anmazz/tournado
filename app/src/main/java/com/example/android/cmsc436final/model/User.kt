package com.example.android.cmsc436final.model

data class User (
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var profilePic:  String = "",
    var toursCreated: List<Tour>? = null,
    var toursCompleted: List<Tour>? = null
)