package com.example.mobilevisitorapp

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties

class FirebaseRepo {

    val database = FirebaseDatabase.getInstance().getReference()
    //private lateinit var dataRef : DatabaseReference
    //dataRef = Firebase.dataRef.reference

    data class Visitor(
        var name : String = "",
        var email : String = "",
        var expandable : Boolean = false
    )

    data class Employee(
        var name : String = "",
        var email : String = ""
    )


}