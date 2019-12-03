package com.example.android.cmsc436final.ui.addTour

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.MediaController
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.SharedViewModel
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.add_image_dialogue.view.*
import kotlinx.android.synthetic.main.add_video_dialogue.view.*
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime

class AddTourBasicInfo: Fragment() {

        private lateinit var sharedViewModel: SharedViewModel
        private lateinit var tourName: TextInputEditText
        private lateinit var tourDescrip: TextInputEditText
        private lateinit var buttonAddPicture: Button
        private lateinit var buttonNext: Button
        private lateinit var buttonCancel: Button
        private val TAG = "Add Tour Checkpoints:"
        private var selectedHeaderPic: Uri? = null
        private lateinit var  addHeaderImageView: View
        private var PICK_IMAGE = 4
        private var picUrl = ""
        //connection to firebase
        private lateinit var auth: FirebaseAuth
        private lateinit var storage: FirebaseStorage
        private lateinit var db: FirebaseFirestore
        private lateinit var storageRef: StorageReference
        private lateinit var userRef: StorageReference


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_add_tour_1, container, false)

        // UI elements
        tourName = root.findViewById<View>(R.id.tour_name_) as TextInputEditText
        tourDescrip = root.findViewById<View>(R.id.tour_descrip) as TextInputEditText
        buttonAddPicture = root.findViewById<View>(R.id.add_tour_picture_button) as Button
        buttonNext = root.findViewById<View>(R.id.next_button) as Button
        buttonCancel = root.findViewById<View>(R.id.cancel_button) as Button

        //firebase initiation
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        db = FirebaseFirestore.getInstance()
        storageRef = storage.reference
        buttonNext.setOnClickListener() {
            saveAndNext()
        }


        buttonAddPicture.setOnClickListener{
            selectTourPicture()
        }

        // TODO navigate to home button
        buttonCancel.setOnClickListener {
            tourName.setText("")
            tourDescrip.setText("")
        }

        return root
    }


    private fun selectTourPicture() {
        Log.i(TAG, "Im about to select header picture")
        addHeaderImageView = LayoutInflater.from(context).inflate(R.layout.add_image_dialogue, null)
        val builder = AlertDialog.Builder(context)
            .setView(addHeaderImageView)
            .setTitle("Select main photo for Tour")
        if(selectedHeaderPic != null){
            addHeaderImageView.imageToBeAdded.setImageURI(selectedHeaderPic)
            addHeaderImageView.selectImageButton.text = "Edit Image"
            addHeaderImageView.cancelPicSelectButton.text = "Done"
        }

        val mAlertDialog = builder.show()
        addHeaderImageView.selectImageButton.setOnClickListener{
            //now select a picture
            val toGallery = Intent(
                Intent.ACTION_GET_CONTENT
                , MediaStore.Images.Media.INTERNAL_CONTENT_URI).setType("image/*")
            startActivityForResult(toGallery,PICK_IMAGE)
        }
        //TODO: add another button for taking pic and set click listener on it

        addHeaderImageView.cancelPicSelectButton.setOnClickListener {
            mAlertDialog.dismiss()
        }

    }

    fun saveAndNext() {
        // get strings from textboxes
        val tourNameStr = tourName.text.toString().trim { it <= ' ' }
        val tourDescripStr = tourDescrip.text.toString()
        //TODO: check if text fields and picture are empty before allowing them to continue
        // add to viewModel
        uploadPicture()
        sharedViewModel.addName(tourNameStr)
        sharedViewModel.addDescription(tourDescripStr)
        sharedViewModel.addPic(picUrl)

        tourName.setText("")
        tourDescrip.setText("")
        navigateToAddCheckpoints()

    }

//    TODO navigate to the add checkpoints page
    private fun navigateToAddCheckpoints(){
        findNavController().navigate(R.id.action_add_tour1_to_add_tour2)
    }

    fun uploadPicture(){
        if(selectedHeaderPic!= null) {
            userRef = storageRef.child("/checkpointPictures")
                .child(selectedHeaderPic!!.lastPathSegment.toString() + LocalDateTime.now() + ".jpg")

            val inputStream = context!!.contentResolver.openInputStream(selectedHeaderPic!!)
            val yourDrawable = Drawable.createFromStream(inputStream, selectedHeaderPic.toString())

            val imageBitmap = (addHeaderImageView.imageToBeAdded.drawable as BitmapDrawable).bitmap
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
                    Log.i(TAG, "task result for pic is:" + task.result.toString())
                    //actual url stored here
                    picUrl = task.result.toString()
                    Log.i(TAG, "uploaded pic to firebase")

                } else {
                    //Toast.makeText(applicationContext, "Could not upload pic", Toast.LENGTH_LONG).show()
                    // ...
                }
            }
        } else {
            Log.i(TAG, "Im in uploadpic- selectedPic==null")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            if(requestCode == PICK_IMAGE){
                selectedHeaderPic = data!!.data as Uri
                if(selectedHeaderPic != null) {
                    //adds image to dialog
                    addHeaderImageView.imageToBeAdded.setImageURI(selectedHeaderPic)
                    //changes button text
                    addHeaderImageView.selectImageButton.text = "Edit Image"
                    addHeaderImageView.cancelPicSelectButton.text = "Done"
                }
            }
        }
}