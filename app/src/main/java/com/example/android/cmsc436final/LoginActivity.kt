package com.example.android.cmsc436final

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.locationbasedtourguide.ui.home.HomeFragment
import java.util.Random

class LoginActivity : Activity() {

    private lateinit var uname: EditText
    private lateinit var passwd: EditText

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        uname = findViewById(R.id.email)
        passwd = findViewById(R.id.password)

    }

    fun onClick(v: View?) {
        if (v!!.getId() == R.id.registerBttn) {
            //user is about to register
            val helloAndroidIntent = Intent(
                this@LoginActivity,
                RegistrationActivity::class.java
            )

            startActivity(helloAndroidIntent)
        } else
            //user is logging in: validate input
            if (!TextUtils.isEmpty(uname.text) &&
                !TextUtils.isEmpty(passwd.text)
                && checkPassword(uname.text, passwd.text)
            ) {

                // Create an explicit Intent for starting the HelloAndroid
                // Activity
                val helloAndroidIntent = Intent(
                    this@LoginActivity,
                    SearchActivity::class.java
                    //MainActivity::class.java USE THIS TO SEE THE MAP
                )
                Toast.makeText(applicationContext, "Login successful!", Toast.LENGTH_LONG)
                    .show()
                // Use the Intent to start the HelloAndroid Activity
                startActivity(helloAndroidIntent)

            } else {
                Toast.makeText(
                    applicationContext,
                    "Login failed! Please try again",
                    Toast.LENGTH_LONG
                ).show()
                uname.text.clear()
                passwd.text.clear()
            }
    }


    private fun checkPassword(uname: Editable, passwd: Editable): Boolean {
        // Just pretending to extract text and check password
        Log.i("Hi", uname.toString())
        Log.i("Hi", passwd.toString())
        return true
    }
}