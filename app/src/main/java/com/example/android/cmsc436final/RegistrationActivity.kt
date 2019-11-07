package com.example.android.cmsc436final

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import android.app.Activity
import android.widget.EditText


class RegistrationActivity : Activity() {

    private lateinit var uname: EditText
    private lateinit var passwd: EditText
    private lateinit var confPasswd: EditText

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        uname = findViewById(R.id.regUsername)
        passwd = findViewById(R.id.regPassword)
        confPasswd = findViewById(R.id.regPasswordConfirm)


    }
    fun onClick(v: View?) {
        if (!TextUtils.isEmpty(uname.text) &&
            !TextUtils.isEmpty(passwd.text)
            && (passwd.text.toString().compareTo(confPasswd.text.toString()) == 0)// && username is not already in database
        ) {

            // Create an explicit Intent for starting the HelloAndroid
            // Activity
            val helloAndroidIntent = Intent(
                this@RegistrationActivity,
                LoginActivity::class.java
            )
            Toast.makeText(applicationContext, "Registration successful!", Toast.LENGTH_LONG)
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
            confPasswd.text.clear()
        }
    }

 }

