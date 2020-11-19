package com.example.mobilevisitorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_sign_out.*
import kotlinx.android.synthetic.main.visitor_item.*

class SignOut : AppCompatActivity(), (FirebaseRepo.Visitor) -> Unit {

    private var visitorList : List<FirebaseRepo.Visitor> = ArrayList()
    private val visitorAdapter : VisitorAdapter = VisitorAdapter (visitorList, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_out)

        val db : FirebaseFirestore = FirebaseFirestore.getInstance()

        //Visitor sign-out function
        /*visitor_list_button!!.setOnClickListener {
            visitorSignOut()
        }*/


        //Get List of signed in visitors from database and sort alphabetically
        fun getVisitorList() : Task<QuerySnapshot>{
            return db
                .collection("Visitors")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
        }

        //Init recyclerview
        visitor_recycler.layoutManager = LinearLayoutManager(this)
        visitor_recycler.adapter = visitorAdapter

        //Load visitor list into Viewholder
        fun loadVisitorList(){
            getVisitorList().addOnCompleteListener {
                if(it.isSuccessful){
                    visitorList = it.result!!.toObjects(FirebaseRepo.Visitor::class.java)
                    visitorAdapter.visitorListItems = visitorList
                    visitorAdapter.notifyDataSetChanged()
                } else {
                    Log.d(TAG, "Error: ${it.exception!!.message}")
                }
            }
        }
        loadVisitorList()
    }

    override fun invoke(p1: FirebaseRepo.Visitor) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun visitorSignOut(){
        // Confirmation check, Timestamp, save visitor details for E.O.D export, remove from active visitors (could use boolean tag if this doesn't work)
    }
}
