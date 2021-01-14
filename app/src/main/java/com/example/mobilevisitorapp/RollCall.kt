package com.example.mobilevisitorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_roll_call.*
import kotlinx.android.synthetic.main.activity_sign_out.*

class RollCall : AppCompatActivity(), (FirebaseRepo.Visitor) -> Unit  {

    private var visitorList : List<FirebaseRepo.Visitor> = ArrayList()
    private val rollCallAdapter : RollCallAdapter = RollCallAdapter (visitorList, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roll_call)

        val db : FirebaseFirestore = FirebaseFirestore.getInstance()

        //Get List of signed in visitors from database and sort alphabetically
        fun getVisitorList() : Task<QuerySnapshot> {
            return db
                .collection("Visitors")
                .whereEqualTo("signedIn", true)
                //.orderBy("name", Query.Direction.ASCENDING)
                .get()
        }

        //Init recyclerview
        roll_call_recycler.layoutManager = LinearLayoutManager(this)
        roll_call_recycler.adapter = rollCallAdapter

        //Load visitor list into Viewholder
        fun loadVisitorList(){
            getVisitorList().addOnCompleteListener {
                if(it.isSuccessful){
                    visitorList = it.result!!.toObjects(FirebaseRepo.Visitor::class.java)
                    rollCallAdapter.visitorListItems = visitorList
                    rollCallAdapter.notifyDataSetChanged()

                } else {
                    Log.d(TAG, "Error: ${it.exception!!.message}")
                }
            }
        }
        loadVisitorList()
        Log.d("tag", "List loaded")
    }

    override fun invoke(p1: FirebaseRepo.Visitor) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
