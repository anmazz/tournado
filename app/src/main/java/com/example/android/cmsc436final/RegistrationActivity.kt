package com.example.android.cmsc436final

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.example.android.cmsc436final.model.Tour
import com.example.android.cmsc436final.model.User
import android.graphics.BitmapFactory


class RegistrationActivity : Activity() {
    private lateinit var uEmail: EditText
    private lateinit var uName: EditText
    private lateinit var passwd: EditText
    private lateinit var confPasswd: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var userPic: ImageView
    private lateinit var selectPhotoBttn: Button
    private val storage = FirebaseStorage.getInstance()
    private lateinit var selectedPhoto: Uri
    private var photoHasBeenSelected: Boolean = false
    private val PICK_IMAGE = 4
    private val TAG = "OUTPUT HERE"


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        auth = FirebaseAuth.getInstance()
        uEmail = findViewById(R.id.regUserEmail)
        uName = findViewById(R.id.regName)
        passwd = findViewById(R.id.regPassword)
        confPasswd = findViewById(R.id.regPasswordConfirm)
        userPic = findViewById(R.id.regUserPic)
        selectPhotoBttn = findViewById((R.id.regSelectPhotobutton))
        progressBar = findViewById(R.id.progressBar)

        }

    private fun selectProfilePic(){
        val toGallery = Intent(
            Intent.ACTION_GET_CONTENT
            , MediaStore.Images.Media.INTERNAL_CONTENT_URI).setType("image/*")
        startActivityForResult(toGallery,PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            Log.i(TAG, "data is" + data!!.data.toString())

            selectedPhoto = data!!.data as Uri
            if(selectedPhoto != null){
                photoHasBeenSelected=true
                userPic.setImageURI(selectedPhoto)
            }
        }
    }





    fun onClick(v: View?) {
        if (v!!.getId() == R.id.regSelectPhotobutton) {
            selectProfilePic()
            return

        } else {
            progressBar.visibility = View.VISIBLE
            val name: String = uName.text.toString()
            val email: String = uEmail.text.toString()
            val password: String = passwd.text.toString()
            val confirmedPassword: String = confPasswd.text.toString()

            //Checking to see if all necessary fields have been provided
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(applicationContext, "Please enter name...", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
                return
            }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(applicationContext, "Please enter email...", Toast.LENGTH_LONG)
                    .show()
                progressBar.visibility = View.GONE
                return
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(applicationContext, "Please enter password!", Toast.LENGTH_LONG)
                    .show()
                progressBar.visibility = View.GONE
                return
            }
            if (password.compareTo(confirmedPassword) != 0) {
                Toast.makeText(
                    applicationContext,
                    "Passwords do not match. Try again",
                    Toast.LENGTH_LONG
                ).show()
                passwd.text.clear()
                confPasswd.text.clear()
                progressBar.visibility = View.GONE
                return
            }

            val x = auth!!.createUserWithEmailAndPassword(email, password)

            x.addOnCompleteListener { task ->
               progressBar.visibility = View.GONE
                if (task.isSuccessful) {

                    val db = FirebaseFirestore.getInstance()
                    //uploading pic url to firestore
                    Log.i(TAG, "about to upload to firebase")
                    val storageRef = storage.reference
                    val userRef = storageRef.child("/userProfilePics").child(auth.uid as String + ".jpg")
                    var imageBitmap: Bitmap
                    when(photoHasBeenSelected) {
                        //grabs drawable from resource and converts it to bitmap
                        false -> imageBitmap = BitmapFactory.decodeResource(
                            applicationContext.resources,
                            R.drawable.default_user_pic
                        )
                        //converts image selected by user to bitmap
                        true -> imageBitmap = (userPic.drawable as BitmapDrawable).bitmap
                    }

                    val baos = ByteArrayOutputStream()
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    val uploadTask = userRef.putBytes(data)


                    val urlTask = uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        userRef.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.i(TAG, "task result is:" + task.result.toString())
                            //actual url stored here
                            val uristring = task.result.toString()
                            Log.i(TAG, "uploaded to firebase")
                            val newUser = User(auth.uid as String, name, email, uristring, arrayListOf<Tour>(),arrayListOf<Tour>())
                            db.collection("users").document(auth.uid as String).set(newUser)
                            Toast.makeText(applicationContext, "Registration successful!", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this@RegistrationActivity, MainActivity::class.java))
                        } else {
                            Toast.makeText(applicationContext, "Could not upload pic", Toast.LENGTH_LONG).show()
                            // ...
                        }
                    }

                } else {

                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        Toast.makeText(applicationContext, "Password was weak. Please try again.", Toast.LENGTH_LONG).show()
                        passwd.text.clear()
                        confPasswd.text.clear()
                    }  catch (e: FirebaseAuthUserCollisionException) {
                        Toast.makeText(applicationContext, "User already exists please sign in.", Toast.LENGTH_LONG).show()
                        uEmail.text.clear()
                        uName.text.clear()
                        passwd.text.clear()
                        confPasswd.text.clear()
                    } catch (e: Exception) {
                        Toast.makeText(applicationContext, "Registration error." + task.exception.toString(), Toast.LENGTH_LONG).show()
                        uEmail.text.clear()
                        uName.text.clear()
                        passwd.text.clear()
                        confPasswd.text.clear()
                    }

                    Log.w(TAG, "createUserWithEmail:failure", task.exception)

                }
            }
        }
    }



 }
