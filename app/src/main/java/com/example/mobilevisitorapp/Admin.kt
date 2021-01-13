package com.example.mobilevisitorapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_admin.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Admin : AppCompatActivity() {

    private val firebaseRepo: FirebaseRepo = FirebaseRepo()
    private var visitorList : List<FirebaseRepo.Visitor> = ArrayList()
    private val storage : FirebaseStorage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        //Expandable view control
        add_employee_linearlayout.setOnClickListener {
            if (add_employee_expand.visibility == View.GONE) {
                //TransitionManager.beginDelayedTransition(LinearLayout,AutoTransition)
                add_employee_expand.visibility = View.VISIBLE
            } else {
                add_employee_expand.visibility = View.GONE
            }
        }

        fun getVisitorList(): Task<QuerySnapshot> {
            return db
                .collection("Visitors")
                .whereEqualTo("signedIn", false)
                .get()
        }

        //Save employee to backend Firebase
        saveEmployee!!.setOnClickListener {
            val empName = employeeName.text.toString()
            val empEmail = employeeEmail.text.toString()

            fun saveEmployee(name: String, email: String) {
                val employee = FirebaseRepo.Employee(name, email)
                db.collection("Employees").add(employee)
                    //On success refresh submission form and user feedback to confirm success
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        employeeName.setText(null)
                        employeeEmail.setText(null)
                        add_employee_expand.visibility = View.GONE
                        Toast.makeText(this, "Employee saved successfully", Toast.LENGTH_LONG)
                            .show()
                    }
                    //On failure display error message and retain field entries
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                        Toast.makeText(
                            this,
                            "Employee not added please try again",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            saveEmployee(empName, empEmail)
        }

        fun deleteSignedOutVisitor(){
            db.collection("Visitors")
                .whereEqualTo("signedOut", true)
               // .delete()

        }

        exportButton!!.setOnClickListener {
            //Confirmation check
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirm Export")
            builder.setMessage("Have all Visitors been signed out?")

            builder.setPositiveButton("Yes") { dialog, which ->
                getVisitorList()
                //function to to serve as an End of day export for a company to clean signed out visitors from the app and transfer to permanent storage
                fun loadVisitorList() {
                    getVisitorList().addOnCompleteListener {
                        if (it.isSuccessful) {
                            visitorList = it.result!!.toObjects(FirebaseRepo.Visitor::class.java)
                            Log.d(TAG, "Visitor list ${visitorList}")
                            val visLog = visitorList.toString()
                            Log.d(TAG, "Visitor String ${visLog}")
                            //Current date captured to name file, for company records to check dates of visitors
                            val currentDate: String = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
                            //create local file containing list of signed out visitors
                            this.openFileOutput(currentDate, Context.MODE_PRIVATE).use {
                                it.write(visLog.toByteArray())
                            }

                            //Export local file to cloud storage on firebase
                            val storageRef = storage.reference
                            val firebaseFile =
                                Uri.fromFile(File(this.getFilesDir().getPath() + "/" + currentDate))
                            val logRef = storageRef.child("Logs/${firebaseFile.lastPathSegment}")
                            val uploadTask: UploadTask = logRef.putFile(firebaseFile)
                            //Disable button to prevent multiple presses
                            exportButton.setEnabled(false)
                            uploadTask.addOnCompleteListener { taskSnapshot ->
                                Log.w(TAG, "Logs Exported")
                                Toast.makeText(this, "Export successful", Toast.LENGTH_LONG).show()
                                exportButton.setEnabled(true)
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }.addOnFailureListener {
                                Log.w(TAG, "Error exporting")
                                Toast.makeText(this, "Export failed", Toast.LENGTH_LONG).show()
                                exportButton.setEnabled(true)
                            }

                        } else {
                            Log.d(TAG, "Error: ${it.exception!!.message}")
                        }

                    }

                }
                loadVisitorList()
                deleteSignedOutVisitor()
            }
            builder.setNegativeButton("No"){dialog, which ->
                Toast.makeText(this,"Export Cancelled", Toast.LENGTH_LONG).show()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }
}


