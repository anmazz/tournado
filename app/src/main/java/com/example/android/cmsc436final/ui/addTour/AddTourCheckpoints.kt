package com.example.android.cmsc436final.ui.addTour

import android.app.AlertDialog
import android.os.Bundle
import android.provider.MediaStore
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.SharedViewModel
import com.example.android.cmsc436final.adapter.CheckpointAdapter
import com.example.android.cmsc436final.model.Checkpoint
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.GeoPoint
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.add_image_dialogue.view.*
import kotlinx.android.synthetic.main.add_video_dialogue.view.*
import kotlinx.android.synthetic.main.fragment_add_media.view.*
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime

class AddTourCheckpoints: Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var checkptName: TextInputEditText
    private lateinit var checkptDesc: TextInputEditText
//    Buttons
    private lateinit var buttonAddLocation: Button
    private lateinit var buttonAddPicture: Button
    private lateinit var buttonAddVideo: Button
    private lateinit var buttonAddAudio: Button
    private lateinit var buttonAddCheckpoint: FloatingActionButton
    private lateinit var buttonNext: Button
    private lateinit var buttonCancel: Button
    private lateinit var selectedPic: Uri
    private lateinit var selectedVideo: Uri
    private lateinit var selectedAudio: Uri
    private val TAG = "In addTourCheckpoints"

    private lateinit var location: GeoPoint
    private var mCheckpointsRecycler: RecyclerView? = null

    //Views for dialogues
    private lateinit var  addImageView: View
    private lateinit var  addVideoView: View
    private lateinit var  addAudioView: View



    // ArrayList of Checkpoints
    private lateinit var checkpoints: MutableList<Checkpoint>
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private var PICK_IMAGE = 4
    private var PICK_VIDEO = 5
    private var PICK_AUDIO = 6

    companion object{
        private val TAG = "AddTourCheckpoints"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedViewModel =
            ViewModelProviders.of(this).get(SharedViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_add_tour_2checkpoints, container, false)

        // UI elements
        checkptName = root.findViewById<View>(R.id.cp_name) as TextInputEditText
        checkptDesc = root.findViewById<View>(R.id.cp_descrip) as TextInputEditText

        // buttons
        buttonAddLocation = root.findViewById<View>(R.id.add_cp_location_button) as Button
        buttonAddPicture = root.findViewById<View>(R.id.add_cp_picture_button) as Button
        buttonAddVideo = root.findViewById<View>(R.id.add_cp_video_button) as Button
        buttonAddAudio = root.findViewById<View>(R.id.add_cp_audio_button) as Button

        //Views for dialogues

        addVideoView = LayoutInflater.from(context).inflate(R.layout.add_video_dialogue, null)
        addAudioView = LayoutInflater.from(context).inflate(R.layout.add_audio_dialogue, null)



        buttonNext = root.findViewById<View>(R.id.next_button) as Button
        buttonCancel = root.findViewById<View>(R.id.cancel_button) as Button

        buttonAddCheckpoint = root.findViewById<View>(R.id.add_cp_button) as FloatingActionButton

        // list of checkpoints we keep adding to
        checkpoints = ArrayList()


        mCheckpointsRecycler = root.findViewById(R.id.recycler_checkpoints)


        //---------MAP STUFF---------//
        //For add location intent
        var fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

        // Start the autocomplete intent.
        var intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields).build(activity!!)

        buttonAddLocation.setOnClickListener{
            if (!Places.isInitialized()) {
                context?.let { it1 -> Places.initialize(it1, getString(R.string.api_key), Locale.US) }
            }
            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        buttonNext.setOnClickListener() {
            saveAndNext()
        }

        buttonAddPicture.setOnClickListener() {
            selectPicture()
        }
        buttonAddVideo.setOnClickListener() {
            selectAudio()
        }
        buttonAddAudio.setOnClickListener() {
            selectVideo()
        }


        buttonAddCheckpoint.setOnClickListener() {
            addCheckpoint()
        }

//      TODO navigate to home
        buttonCancel.setOnClickListener() {
            checkptName.setText("")
            checkptDesc.setText("")
            navigateToHome()
        }

        return root
    }

    private fun selectPicture() {
        Log.i(TAG, "Im about to select picture")
        addImageView = LayoutInflater.from(context).inflate(R.layout.add_image_dialogue, null)
        val builder = AlertDialog.Builder(context)
            .setView(addImageView)
            .setTitle("Select photo for Checkpoint")
        //TODO: make sure to populate imageview if image was already selected
            if(selectedPic != null){
                addImageView.imageToBeAdded.setImageURI(selectedPic)
                addImageView.selectImageButton.text = "Edit Image"
                addImageView.cancelPicSelectButton.text = "Done"
            }

            val mAlertDialog = builder.show()
            addImageView.selectImageButton.setOnClickListener{
                //now select a picture
                val toGallery = Intent(
                    Intent.ACTION_PICK
                    , MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(toGallery,PICK_IMAGE)
                //mAlertDialog.dismiss() do this last but before it make sure to change cancel button to done

            }

            addImageView.cancelPicSelectButton.setOnClickListener {
                mAlertDialog.dismiss()
            }

    }

    // TODO make sure this is adding just audio
    private fun selectAudio(){


    }

    private fun selectVideo(){
        Log.i(TAG, "Im about to select audio")
        val addImageView = LayoutInflater.from(context).inflate(R.layout.add_image_dialogue, null)
        val builder = AlertDialog.Builder(context)
            .setView(addImageView)
            .setTitle("Select photo for Checkpoint")
        //TODO: make sure to populate imageview if image was already selected
        if(selectedPic != null){
            addImageView.imageToBeAdded.setImageURI(selectedPic)
            addImageView.selectImageButton.text = "Edit Image"
            addImageView.cancelPicSelectButton.text = "Done"
        }

        val mAlertDialog = builder.show()
        addImageView.selectImageButton.setOnClickListener{
            //now select a picture
            val toGallery = Intent(
                Intent.ACTION_PICK
                , MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(toGallery,PICK_IMAGE)
            //mAlertDialog.dismiss() do this last but before it make sure to change cancel button to done

        }

        addImageView.cancelPicSelectButton.setOnClickListener {
            mAlertDialog.dismiss()
        }

    }


    fun saveAndNext() {
        // add to viewModel
        sharedViewModel.addCheckpoints(checkpoints)
        navigateToAddTags()
    }

    private fun navigateToAddTags(){
        findNavController().navigate(R.id.action_add_tour2_to_add_tour3)
    }

//    TODO fix this
    private fun navigateToHome(){
        findNavController().navigate(R.id.action_add_tour3_to_navigation_home)
    }


    private fun addCheckpoint() {
        val name = checkptName.text.toString()

        val description = checkptDesc.text.toString()
//        TODO figure out how to save media
//        images = arr
//        audio:  MutableList<String>
//        video: MutableList<String>
        val newCP = Checkpoint(name, location, description, "", "", "")

        // need to add this checkpoint to tour arrayList
        checkpoints.add(newCP)

        // TO display added checkpoints
        //creating adapter using CheckpointAdapter
        val adapter = CheckpointAdapter(activity!!, checkpoints)
        //attaching adapter to the listview
        mCheckpointsRecycler!!.adapter = adapter
        mCheckpointsRecycler!!.layoutManager = LinearLayoutManager(context)

        checkptName.setText("")
        checkptDesc.setText("")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

             AUTOCOMPLETE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val place = data?.let { Autocomplete.getPlaceFromIntent(it) }
                    if (place != null) {
                        Log.i(TAG, "Place: " + place.name + ", " + place.id)
//                        geoCode = Geocoder(context).
                        location = GeoPoint(place.latLng!!.latitude, place.latLng!!.longitude)
                        Toast.makeText(
                            activity,
                            "Place: " + place.name + ", " + place.id,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    // TODO: Handle the error.
                    Toast.makeText(
                        activity,
                        "Error with autocomplete. Please try again.",
                        Toast.LENGTH_LONG
                    )
                    var status = Autocomplete.getStatusFromIntent(data!!)
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                }
            }

            PICK_IMAGE->{
                selectedPic = data!!.data as Uri
                if(selectedPic != null) {
                    addImageView.imageToBeAdded.setImageURI(selectedPic)
                    addImage(selectedPic)
                }

            }
            PICK_VIDEO->{
                selectedVideo = data!!.data as Uri
                if(selectedVideo != null) {
                    //adds video to video view
                    addVideoView.videoToBeAdded.setVideoURI(selectedVideo)
                    var mediaController = MediaController(context)
                    addVideoView.videoToBeAdded.setMediaController(mediaController)
                    mediaController.setAnchorView(addVideoView.videoToBeAdded)
                    //sends video off to upload to data base
                    addVideo(selectedVideo)
                }
            }
            PICK_AUDIO->{
                selectedAudio = data!!.data as Uri
                if(selectedAudio != null) {
                    //addAudioView.(selectedAudio) need to attribute the selected audio to a media player
                    //to play it in
                    addImage(selectedPic)
                }
                addAudio(selectedAudio)
            }

        }
    }

    //functions below will add media to database

    private fun addImage(selectedMedia: Uri){
        //userRef = storageRef.child("/checkpointPictures").child(selectedMedia.toString() + LocalDateTime.now() + ".jpg")
        //for setting selected media into imageview
        //userPic.setImageURI(selectedPhoto)

//        val inputStream = context!!.contentResolver.openInputStream(selectedMedia)
//        val yourDrawable = Drawable.createFromStream(inputStream, selectedMedia.toString() )
//
//        val imageBitmap = (selectedMedia as BitmapDrawable).bitmap
//        val baos = ByteArrayOutputStream()
//        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//        val data = baos.toByteArray()
//        val uploadTask = userRef.putBytes(data)
//        val urlTask = uploadTask.continueWithTask { task ->
//            if (!task.isSuccessful) {
//                task.exception?.let {
//                    throw it
//                }
//            }
//            userRef.downloadUrl
//        }.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                Log.i(TAG, "task result is:" + task.result.toString())
//                //actual url stored here
//                val uristring = task.result.toString()
//                Log.i(TAG, "uploaded to firebase")
//                // val newUser = User(auth.uid as String, name, email, uristring, arrayListOf<Tour>(),arrayListOf<Tour>())
//                // db.collection("users").document(auth.uid as String).set(newUser)
//                //Toast.makeText(applicationContext, "Registration successful!", Toast.LENGTH_LONG).show()
//                //startActivity(Intent(this@RegistrationActivity, MainActivity::class.java))
//            } else {
//                //Toast.makeText(applicationContext, "Could not upload pic", Toast.LENGTH_LONG).show()
//                // ...
//            }
//        }

    }

    private fun addAudio(selectedMedia: Uri){}
    private fun addVideo(selectedMedia: Uri){}


}
