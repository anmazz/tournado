package com.example.android.cmsc436final

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.locationbasedtourguide.ui.home.HomeFragment
import java.util.Random
import com.google.firebase.auth.FirebaseAuth



class LoginActivity : Activity() {

    private lateinit var uname: EditText
    private lateinit var passwd: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private var TAG = "Something Bad Happened"
// ...


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        uname = findViewById(R.id.email)
        passwd = findViewById(R.id.password)
        progressBar = findViewById(R.id.progressBar)

    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        //updateUI(currentUser)
    }

    fun onClick(v: View?) {
        if (v!!.getId() == R.id.registerBttn) {
            //user is about to register
            val helloAndroidIntent = Intent(
                this@LoginActivity,
                RegistrationActivity::class.java
            )
            startActivity(helloAndroidIntent)
            return

        } else
            //user is logging in: validate input
            progressBar.visibility = View.VISIBLE

        val email: String = uname.text.toString()
        val password: String = passwd.text.toString()

        if (TextUtils.isEmpty(email)) {
           // Toast.makeText(applicationContext, "Please enter email...", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Please enter password!", Toast.LENGTH_LONG).show()
            return
        }

        auth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Login successful!", Toast.LENGTH_LONG)
                        .show()
                    startActivity(Intent(
                        this@LoginActivity,
                        SearchActivity::class.java))
                    //MainActivity::class.java USE THIS TO SEE THE MAP)
                } else {
                    Log.w(TAG, "signInUserWithEmail:failure", task.exception)
                    Toast.makeText(applicationContext, "Login failed! Please try again", Toast.LENGTH_LONG).show()
                    uname.text.clear()
                    passwd.text.clear()
                }
            }

    }

}