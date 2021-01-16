package com.example.mobilevisitorapp

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/** TO-DO
 * Create spinner adapter to populate spinner with employee names stored in DB, then using that info send an email to said employee
 * Edit spinner to show hint
 * Include back arrow
 */


const val TAG = "TAG"
val db = Firebase.firestore
var employeeVisiting = ""

private const val REQUEST_CODE = 69 //Arbitrary number
private lateinit var photoFile : File

class SignIn : AppCompatActivity(),(FirebaseRepo.Visitor) -> Unit, AdapterView.OnItemSelectedListener {

    private var photoUri : Uri? = null
    private var savedPhoto = ""
    private val storage : FirebaseStorage = FirebaseStorage.getInstance()
    private var visitor = FirebaseRepo.Visitor()

    private val activeEmployees = db.collection("Employees")

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

        btn_take_photo.setOnClickListener {
            capturePhoto()
        }

        visitor_submit.setOnClickListener {
            val visName = visitor_name.text.toString()
            val visEmail = visitor_email.text.toString()
            val visPurpose = visit_purpose.text.toString()
            val inTime = Timestamp.now()
            val visId = ""
            val signedIn = true

            saveVisitor(visName,visEmail, employeeVisiting, visPurpose, inTime, visId, signedIn, savedPhoto)
            employeeVisiting = ""
            visitor_photo.visibility = View.GONE

        }
    }

    //Take data from entry fields and create new
    private fun saveVisitor(name: String, email: String, empVisiting : String, visPurpose : String, inTime : Timestamp, visId : String, signedIn : Boolean, savedPhoto : String){
        visitor = FirebaseRepo.Visitor(name, email, empVisiting, visPurpose, inTime, visId, signedIn,savedPhoto)
        db.collection("Visitors").add(visitor)
            //On success clear entry fields and return to main menu
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                db.collection("Visitors").document(documentReference.id).update("visId", documentReference.id)
                uploadPhoto(documentReference.id)
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

    //Id passed from saveVisitor document reference to enable updating of fields
    private fun uploadPhoto(id : String) {
        val storageRef = storage.reference
        val logRef = storageRef.child("Photos/${photoUri!!.lastPathSegment}")
        val uploadTask: UploadTask = logRef.putFile(photoUri!!)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            //Create url address to open photo on browser
            val downloadUrl = logRef.downloadUrl
            downloadUrl.addOnSuccessListener {
                savedPhoto = it.toString() //Convert to string for Json database
                //Update firestore with public image uri
                updateVisitor(id, savedPhoto)
            }
        }
        uploadTask.addOnFailureListener{
            Log.e(TAG, it.message)
        }
    }

    private fun updateVisitor(id: String, savedPhoto : String) {
        db.collection("Visitors")
            .document(id)
            .update("photoUri", savedPhoto)
    }


    private fun capturePhoto(){
        //declare intent and action desired to achieve
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = createPhotoFile()

        photoUri = FileProvider.getUriForFile(this, "com.example.mobilevisitorapp.fileprovider", photoFile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

        //Check to ensure device has camera to prevent app crashes
        if (takePictureIntent.resolveActivity(this.packageManager) !=null){
            startActivityForResult(takePictureIntent,REQUEST_CODE)
        }else{
            Toast.makeText(this,"Camera error", Toast.LENGTH_LONG).show()
        }
    }

    //Photo file with name format
    private fun createPhotoFile(): File {
        // generate a unique filename with date.
        val currentDate: String = SimpleDateFormat("dd.MM.yyyy_HH.MM.SS", Locale.getDefault()).format(Date())
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile("VisPhoto${currentDate}", ".jpg", storageDirectory)
    }

    // Take/ cancel taking picture
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //If picture taken successfully
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            //display photo to appear on device for user feedback
            val displayPhoto = BitmapFactory.decodeFile(photoFile.absolutePath)
            visitor_photo.setImageBitmap(displayPhoto)
            visitor_photo.visibility = View.VISIBLE
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    //Invoke of visitor data class to allow passing of values
    override fun invoke(p1: FirebaseRepo.Visitor) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(this,"Please select the employee you are here to visit", Toast.LENGTH_LONG).show()
    }


    //NOT FINISHED - NEEDS FIXING
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

