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
    private val TAG = "Something Bad Happened"


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
         val toGallery = Intent(Intent.ACTION_PICK
                ,MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(toGallery,PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            selectedPhoto = data!!.data as Uri
            if(selectedPhoto != null){
                photoHasBeenSelected=true
            }
            Log.i(TAG, "changing imageview to"+ selectedPhoto )
            userPic.setImageURI(selectedPhoto)


        }
    }

    private fun uploadUserPic(): String {
        Log.i(TAG, "about to upload to firebase")
        val storageRef = storage.reference
        val userRef = storageRef.child(auth.currentUser.toString())
        val userImagesRef = storageRef.child("userProfilePics/" + auth.currentUser.toString())

        val imageBitmap = (userPic.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        userRef.putBytes(data)

        return userImagesRef.downloadUrl.toString()

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


            if (TextUtils.isEmpty(name)) {
                Toast.makeText(applicationContext, "Please enter name...", Toast.LENGTH_LONG).show()
                return
            }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(applicationContext, "Please enter email...", Toast.LENGTH_LONG)
                    .show()
                return
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(applicationContext, "Please enter password!", Toast.LENGTH_LONG)
                    .show()
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
                return
            }
            if (!photoHasBeenSelected) {
                Toast.makeText(applicationContext, "Please select a photo", Toast.LENGTH_LONG)
                    .show()
                return
            }

            val x = auth!!.createUserWithEmailAndPassword(email, password)

            x.addOnCompleteListener { task ->
               progressBar.visibility = View.GONE
                if (task.isSuccessful) {

                    val db = FirebaseFirestore.getInstance()
                    val profilePicUrl = uploadUserPic()
                    Log.i(TAG, "uploaded to firebase")
                    val newUserInfo = hashMapOf(
                        "name" to name,
                        "profilePic" to profilePicUrl
                    )
                    db.collection("users").document(auth.currentUser.toString()).set(newUserInfo)
                    Toast.makeText(applicationContext, "Registration successful!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@RegistrationActivity, SearchActivity::class.java))
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        applicationContext,
                        "Login failed! Please try again",
                        Toast.LENGTH_LONG
                    ).show()
                    uEmail.text.clear()
                    uName.text.clear()
                    passwd.text.clear()
                    confPasswd.text.clear()
                }
            }
        }
    }



 }

