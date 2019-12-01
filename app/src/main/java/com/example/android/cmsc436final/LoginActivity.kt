package com.example.android.cmsc436final

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.reset_password_dialogue.view.*


class LoginActivity : Activity()  {

    private lateinit var uname: EditText
    private lateinit var passwd: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private var TAG = "Something Bad Happened"
// ...


    public override fun onCreate(savedInstanceState: Bundle?) {
        //request permissions to everything somewhere here
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

    private fun resetPassword(){
        Log.i(TAG, "Im about to reset password")
        val resetPassView = LayoutInflater.from(this).inflate(R.layout.reset_password_dialogue, null)
        val builder = AlertDialog.Builder(this)
            .setView(resetPassView)
            .setTitle("Enter email for reset instructions")
        val mAlertDialog = builder.show()
        resetPassView.resetPassButton.setOnClickListener{
            mAlertDialog.dismiss()
            //actually reset here
            val email = resetPassView.resetPasswordEmailEditText.text.toString()
            auth.sendPasswordResetEmail(email).addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    Toast.makeText(applicationContext, "Reset instructions have been sent to email", Toast.LENGTH_LONG).show()
                }
            }
        }

        resetPassView.cancelPassReset.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }


    fun onClick(v: View?) {
        if (v!!.getId() == R.id.resetPasswordButton) {
            resetPassword()

        } else if (v!!.getId() == R.id.registerLink) {
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
           Toast.makeText(applicationContext, "Please enter email...", Toast.LENGTH_LONG).show()
            progressBar.visibility = View.GONE
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Please enter password!", Toast.LENGTH_LONG).show()
            progressBar.visibility = View.GONE
            return
        }

        auth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, "Login successful!", Toast.LENGTH_LONG)
                        .show()
                    uname.text.clear()
                    passwd.text.clear()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    //MainActivity::class.java USE THIS TO SEE THE MAP)
                } else {
                    progressBar.visibility = View.GONE
                    Log.w(TAG, "signInUserWithEmail:failure", task.exception)
                    Toast.makeText(applicationContext, "Login failed!" + task.exception + "Please try again", Toast.LENGTH_LONG).show()
                    uname.text.clear()
                    passwd.text.clear()
                }
            }

    }

}