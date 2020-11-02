package com.example.mobilevisitorapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sign_in!!.setOnClickListener{
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }

        sign_out!!.setOnClickListener{
            val intent = Intent(this, SignOut::class.java)
            startActivity(intent)
        }

        admin!!.setOnClickListener{
            val intent = Intent(this, Admin::class.java)
            startActivity(intent)
        }
    }
}
