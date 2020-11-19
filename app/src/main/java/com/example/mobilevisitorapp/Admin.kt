package com.example.mobilevisitorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_admin.*

class Admin : AppCompatActivity() {

    private val firebaseRepo: FirebaseRepo = FirebaseRepo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        //Expandable view control
        add_employee_linearlayout.setOnClickListener {
            if(add_employee_expand.visibility == View.GONE){
                //TransitionManager.beginDelayedTransition(LinearLayout,AutoTransition)
                add_employee_expand.visibility = View.VISIBLE
            } else{
                add_employee_expand.visibility = View.GONE
            }
        }

        //Save employee to backend Firebase
        saveEmployee!!.setOnClickListener {
            val empName = employeeName.text.toString()
            val empEmail = employeeEmail.text.toString()

            fun saveEmployee(name: String, email: String){
                val employee = FirebaseRepo.Employee(name, email)
                db.collection("Employees").add(employee)
                    //On success refresh submission form and user feedback to confirm success
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        employeeName.setText(null)
                        employeeEmail.setText(null)
                        add_employee_expand.visibility = View.GONE
                        Toast.makeText(this,"Employee saved successfully", Toast.LENGTH_LONG).show()
                    }
                    //On failure display error message and retain field entries
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                        Toast.makeText(this,"Employee not added please try again", Toast.LENGTH_LONG).show()
                    }
            }
            saveEmployee(empName,empEmail)
        }

    }

}


