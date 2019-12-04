package com.example.android.cmsc436final.ui.userProfile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.android.cmsc436final.LoginActivity
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.RegistrationActivity
import com.example.android.cmsc436final.model.Tour
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class UserProfileFragment : Fragment() {
    private lateinit var profilePic: ImageView
    private lateinit var signOutButton: Button
    private lateinit var changeProfilePicButton: ImageButton
    private lateinit var userName: TextView
    private lateinit var toursTraveledCount: TextView
    private lateinit var toursCreatedCount: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    //for changing pic
    private lateinit var selectedPhoto: Uri
    private var photoHasBeenSelected: Boolean = false
    private val PICK_IMAGE = 4
    private val LOAD_TRAVELED = 3
    private val LOAD_CREATED = 2
    private val TAG = "In User Profile Frag: "


    private lateinit var userProfileViewModel: UserProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userProfileViewModel =
            ViewModelProviders.of(this).get(UserProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_user_profile, container, false)

        //setup elements
        auth = FirebaseAuth.getInstance()
        profilePic = root.findViewById(R.id.profilePictureView)
        signOutButton = root.findViewById(R.id.signOutButton)
        changeProfilePicButton = root.findViewById(R.id.changeProfilePicButton)
        toursTraveledCount = root.findViewById(R.id.numToursTraveled)
        toursCreatedCount = root.findViewById(R.id.numToursCreated)
        userName = root.findViewById(R.id.usernameTextView)

        //for filling in data
        user = auth.currentUser as FirebaseUser
        val docRef = db.collection("users").document(user.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("here", "DocumentSnapshot data: ${document.data}")
                    //loads profile pic url into imageview
                    Glide.with(context)
                        .load(document["profilePic"])
                        .into(profilePic)
//                    val arrOfToursCompleted = document["toursCompleted"] as ArrayList<*>
//                    val arrOfToursCreated = document["toursCreated"] as ArrayList<*>
                    val arrOfToursCompleted = document.get("toursCompleted") as List<Tour>?
                    val arrOfToursCreated = document.get("toursCreated") as List<Tour>?
                    val username = document.get("name") as String?

                    if (arrOfToursCompleted == null) {
                        toursTraveledCount.text = "0"
                    }
                    if(arrOfToursCreated == null) {
                        toursCreatedCount.text = "0"
                    } else {
                        toursTraveledCount.text = (arrOfToursCompleted!!.size.toString())
                        toursCreatedCount.text = (arrOfToursCreated!!.size.toString())
                        userName.text = username
                    }



                } else {
                    Log.d("here", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("here", "get failed with ", exception)
            }


        signOutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(context, "Sign out successful!", Toast.LENGTH_LONG).show()
            val backToLogin = Intent(
                context,
                LoginActivity::class.java
            )
            backToLogin.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(backToLogin)
        }
        changeProfilePicButton.setOnClickListener{
            changeProfilePic()
        }


        //val textView: TextView = root.findViewById(R.id.ratingText)
        //userProfileViewModel.text.observe(this, Observer {
            //textView.text = it
        //})
        return root
    }

    private fun changeProfilePic(){
        val toGallery = Intent(
            Intent.ACTION_GET_CONTENT
            , MediaStore.Images.Media.INTERNAL_CONTENT_URI).setType("image/*")
        startActivityForResult(toGallery,PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE){
            Log.i(TAG, "data is" + data!!.data.toString())

            selectedPhoto = data!!.data as Uri
            if(selectedPhoto != null) {
                photoHasBeenSelected = true
                profilePic.setImageURI(selectedPhoto)
                //TODO: scale selected image to fit into imageview maybe?
                var imageBitmap = (profilePic.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val userRef = storage.reference.child("/userProfilePics").child(auth.uid as String + ".jpg")
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
                        Log.i("HERE", "task result is:" + task.result.toString())
                        //actual url stored here
                        val newUrl = task.result.toString()
                        val userFields = db.collection("users").document(user.uid)
                        userFields.update("profilePic", newUrl)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Profile pic update successful!", Toast.LENGTH_LONG).show()
                                Log.d(TAG, "DocumentSnapshot successfully updated!") }
                            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
                    } else {
                        Toast.makeText(context, "Could not update pic", Toast.LENGTH_LONG).show()
                        // ...
                    }
                }
            }
        }
    }

}