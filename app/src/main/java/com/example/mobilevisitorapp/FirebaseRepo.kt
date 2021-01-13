package com.example.mobilevisitorapp

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.util.*


class FirebaseRepo {

    data class Visitor(
        var name : String = "",
        var email : String = "",
        var empVisiting : String = "",
        var visPurpose : String = "",
        var inTime : Timestamp? = null,
        var visId : String = "",
        var signedIn : Boolean = true,
        var outTime : Timestamp? = null,
        var expandable : Boolean = false


    )

    data class Employee(
        var name : String = "",
        var email : String = ""
    )

    fun visitorSignOut(visitor : Visitor){
        // Timestamp, save visitor details for E.O.D export, remove from active visitors (could use boolean tag if this doesn't work)
        //val signOutVis = visitor(name, email, empVisiting, visPurpose, inTime)
        Log.d("tag", " ---- passed into repo ----  ${visitor}")
        val signOutVis = db.collection("Visitors").document(visitor.visId)
        signOutVis
            .update(mapOf(
                "outTime" to visitor.outTime,
                "signedIn" to false
            ))
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
        //SignOut.startActivity()

        Log.d("tag", "----- After FB call ------ ${visitor}")
    }



}