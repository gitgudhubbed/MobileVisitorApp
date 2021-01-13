package com.example.mobilevisitorapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.time.LocalDateTime


/** TO-DO
 * Create spinner adapter to populate spinner with employee names stored in DB, then using that info send an email to said employee
 * Edit spinner to show hint
 * Include back arrow
 */

val db = Firebase.firestore
const val TAG = "TAG"
val activeEmployees = db.collection("Employees")
var employeeVisiting = ""

class SignIn : AppCompatActivity(),(FirebaseRepo.Visitor) -> Unit, AdapterView.OnItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)


        //Spinner, Arraylist and adapter used to populate drop down spinner with names from employee collection in database
        val spinner : Spinner = findViewById(R.id.employee_list)
        spinner.onItemSelectedListener = this
        val employeeList : ArrayList<Pair<String,String>> = ArrayList()
        val spinnerList : MutableList<String> = ArrayList()
        val employeeAdapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, spinnerList)
        employeeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = employeeAdapter

        //Function to iterate through employee collection capturing string stored in name field and add it to arraylist which populates spinner
        activeEmployees.get().addOnCompleteListener {
            fun onComplete(@NonNull task : Task<QuerySnapshot> ){
                if (it.isSuccessful){
                    for (document : QueryDocumentSnapshot in task.result!!){
                        val empName = document.getString("name")
                        val empEmail = document.getString("email")
                        val employees = Pair(empName, empEmail)
                        //Separate Spinner and employee list to only display names in spinner but capture email for employee notification
                        employeeList.add(employees as Pair<String, String>)
                        spinnerList.add (employees.first)

                        Log.w(TAG, "Spinner List $spinnerList")
                    }
                    employeeAdapter.notifyDataSetChanged()
                }
            }
            onComplete(task = it)
        }

        visitor_submit!!.setOnClickListener {
            val visName = visitor_name.text.toString()
            val visEmail = visitor_email.text.toString()
            val visPurpose = visit_purpose.text.toString()
            val inTime = Timestamp.now()
            val visId = ""
            val signedIn = true

            //Take data from entry fields and create new
            fun saveVisitor(name: String, email: String, empVisiting : String, visPurpose : String, inTime : Timestamp, visId : String, signedIn : Boolean){
                val visitor = FirebaseRepo.Visitor(name, email, empVisiting, visPurpose, inTime, visId, signedIn)
                db.collection("Visitors").add(visitor)
                        //On success clear entry fields and return to main menu
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        db.collection("Visitors").document(documentReference.id).update("visId", documentReference.id)
                        visitor_name.text = null
                        visitor_email.text = null
                        visit_purpose.text = null
                        Toast.makeText(this,"Signed-In", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                        Toast.makeText(this,"Sign-in failed", Toast.LENGTH_LONG).show()
                    }
            }
            saveVisitor(visName,visEmail, employeeVisiting, visPurpose, inTime, visId, signedIn)
            employeeVisiting = ""
        }
    }

    override fun invoke(p1: FirebaseRepo.Visitor) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(this,"Please select the employee you are here to visit", Toast.LENGTH_LONG).show()
    }

//    override View getDropDownView (position: Int, convertView : View, parent: ViewGroup){
//        view View = super.getDropDownView(position, convertView, parent)
//        tv TextView = TextView view
//        if (position == 0){
//            tv.setTextColor(Gray)
//        }
//
//    }
//     open fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? {
//        val view: View = getDropDownView(position, convertView, parent)!!
//        val tv = view as TextView
//        if (position == 0) { // Set the hint text color gray
//            tv.setTextColor(Color.GRAY)
//        } else {
//            tv.setTextColor(Color.BLACK)
//        }
//        return view
//}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedEmp = parent!!.getItemAtPosition(position)
        //if (position == 0){
            Toast.makeText(this,"Please select the employee you are here to visit", Toast.LENGTH_LONG).show()

        //}else{
            employeeVisiting = selectedEmp.toString()
            Log.w(TAG, "Employee Snapshot $selectedEmp")
        //}

    }
}

//private operator fun SpinnerAdapter.invoke(employeeAdapter: Adapter) {

//}
