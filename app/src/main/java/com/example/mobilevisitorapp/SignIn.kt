package com.example.mobilevisitorapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_in.*

/** TO-DO
 * Create spinner adapter to populate spinner with employee names stored in DB, then using that info send an email to said employee
 */

val db = Firebase.firestore
const val TAG = "TAG"

class SignIn : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)


    visitor_submit!!.setOnClickListener {
        val visName = visitor_name.text.toString()
        val visEmail = visitor_email.text.toString()

        //Take data from entry fields and create new
        fun saveVisitor(name: String, email: String){
            val visitor = FirebaseRepo.Visitor(name, email)
            db.collection("Visitors").add(visitor)
                    //On success clear entry fields and return to main menu
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    visitor_name.setText(null)
                    visitor_email.setText(null)
                    Toast.makeText(this,"Signed-In", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    Toast.makeText(this,"Sign-in failed", Toast.LENGTH_LONG).show()
                }
        }
        saveVisitor(visName,visEmail)
    }
    }



}
