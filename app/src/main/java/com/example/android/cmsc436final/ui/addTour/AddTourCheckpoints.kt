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
import android.media.AudioDeviceCallback
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Observer
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.add_audio_dialogue.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.add_image_dialogue.view.*
import kotlinx.android.synthetic.main.add_video_dialogue.view.*
import kotlinx.android.synthetic.main.fragment_add_media.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
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
    //Uri's where user media selections go
    private var allCheckpointUris: MutableList<Array<Uri?>> = ArrayList()
    private var urisOfSingleCheckpoint: MutableList<Uri> = ArrayList()
    private var array: Array<Uri?> = arrayOfNulls<Uri?>(3)
    private var selectedPic: Uri? = null
    private var selectedVideo: Uri? = null
    private var selectedAudio: Uri? = null
    private var toUploadSelectedPic: Uri? = null
    private var toUploadSelectedVideo: Uri? = null
    private var toUploadSelectedAudio: Uri? = null
    private val TAG = "In addTourCheckpoints"
    //MediaPlayers for media selection
    private var audioPlayer :MediaPlayer? = MediaPlayer()

    //connection to firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private lateinit var userRef: StorageReference

    private lateinit var arrayCheck: Array<Boolean>

    private lateinit var location: GeoPoint
    private var mCheckpointsRecycler: RecyclerView? = null

    //Views for dialogues
    private var  addImageView: View? = null
    private var  addVideoView: View? = null
    private var  addAudioView: View? = null

    //urls in database
    private var  imageUrl: String = ""
    private var  videoUrl: String = ""
    private var  audioUrl: String = ""

    // Arraylist of Checkpoints
    private lateinit var checkpoints: MutableList<Checkpoint>
    //request codes for activityresult
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private var PICK_IMAGE = 4
    private var PICK_VIDEO = 5
    private var PICK_AUDIO = 6

    //private var vidFlag = false
    //private var audFlag = false

    companion object{
        private val TAG = "AddTourCheckpoints"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedViewModel =
            ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_add_tour_2checkpoints, container, false)

        // UI elements
        checkptName = root.findViewById<View>(R.id.cp_name) as TextInputEditText
        checkptDesc = root.findViewById<View>(R.id.cp_descrip) as TextInputEditText

        // buttons
        buttonAddLocation = root.findViewById<View>(R.id.add_cp_location_button) as Button
        buttonAddPicture = root.findViewById<View>(R.id.add_cp_picture_button) as Button
        buttonAddVideo = root.findViewById<View>(R.id.add_cp_video_button) as Button
        buttonAddAudio = root.findViewById<View>(R.id.add_cp_audio_button) as Button


        //firebase initiation
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        db = FirebaseFirestore.getInstance()
        storageRef = storage.reference

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


        //3 buttons for adding media below
        buttonAddPicture.setOnClickListener() {
            selectPicture()
        }
        buttonAddVideo.setOnClickListener() {
            selectVideo()
        }
        buttonAddAudio.setOnClickListener() {
            selectAudio()
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


        // array to check if things are added
        var arrayCheck = booleanArrayOf(false,false,false)

        return root
    }


    private fun selectPicture() {
        Log.i(TAG, "Im about to select picture")
        addImageView = LayoutInflater.from(context).inflate(R.layout.add_image_dialogue, null)
        val builder = AlertDialog.Builder(context)
            .setView(addImageView)
            .setTitle("Select main photo for Checkpoint")
        //TODO: make sure to populate imageview if image was already selected
            if(selectedPic != null){
                addImageView!!.imageToBeAdded.setImageURI(selectedPic)
                addImageView!!.selectImageButton.text = "Edit Image"
                addImageView!!.cancelPicSelectButton.text = "Done"
            }

            val mAlertDialog = builder.show()
            addImageView!!.selectImageButton.setOnClickListener{
                //now select a picture
                val toGallery = Intent(
                    Intent.ACTION_GET_CONTENT
                    , MediaStore.Images.Media.INTERNAL_CONTENT_URI).setType("image/*")
                    startActivityForResult(toGallery,PICK_IMAGE)

            }
            //TODO: add another button for taking pic and set click listner on it: time permitting

            addImageView!!.cancelPicSelectButton.setOnClickListener {
                mAlertDialog.dismiss()
            }

    }

    // TODO properly handle audio(maybe get rid of slider and pause/play)
    private fun selectAudio(){
        Log.i(TAG, "Im about to select audio")
        addAudioView = LayoutInflater.from(context).inflate(R.layout.add_audio_dialogue, null)
        val builder = AlertDialog.Builder(context)
            .setView(addAudioView)
            .setTitle("Select audio for Checkpoint")
        //TODO: make sure to populate audio if audio was already selected
        if(selectedAudio != null){
            //enter audio stuff here
            //audFlag = true
            addAudioView!!.selectAudioButton.text = "Edit Audio"
            addAudioView!!.cancelAudioSelectButton.text = "Done"
        }

        val mAlertDialog = builder.show()
        addAudioView!!.selectAudioButton.setOnClickListener{
            //now select audio
            val toGallery = Intent(
//                Intent.ACTION_GET_CONTENT).setType("audio/*")
            Intent.ACTION_PICK
            , MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(toGallery,PICK_AUDIO)

        }
        //play audio
        addAudioView!!.playButton.setOnClickListener {
            if(selectedAudio != null){
                audioPlayer!!.reset()
                audioPlayer!!.setDataSource(context!!, selectedAudio!!)
                audioPlayer!!.prepare()
                audioPlayer!!.setOnPreparedListener(object: MediaPlayer.OnPreparedListener{
                    override fun onPrepared(mediaPlayer: MediaPlayer) {
                        mediaPlayer.start()
                    }
                })

            }
        }
        //pause audio
        addAudioView!!.pauseButton.setOnClickListener {
            if(audioPlayer != null){
                audioPlayer!!.stop()
            }
        }

        addAudioView!!.cancelAudioSelectButton.setOnClickListener {
            mAlertDialog.dismiss()
        }

    }

    // TODO
    private fun selectVideo(){
        Log.i(TAG, "Im about to select video")
        addVideoView = LayoutInflater.from(context).inflate(R.layout.add_video_dialogue, null)
        val builder = AlertDialog.Builder(context)
            .setView(addVideoView)
            .setTitle("Select video for Checkpoint")
        //TODO: make sure to populate imageview if image was already selected
        if(selectedVideo != null){
            addVideoView!!.videoToBeAdded.setVideoURI(selectedVideo)
//            var mediaController = MediaController(context)
//            addVideoView.videoToBeAdded.setMediaController(mediaController)
//            mediaController.setAnchorView(addVideoView)
            //vidFlag = true
//            addVideoView.selectVideoButton.text = "Edit Video"
//            addVideoView.cancelVidSelectButton.text = "Done"
        }

        val mAlertDialog = builder.show()
        addVideoView!!.selectVideoButton.setOnClickListener{
            //now select a video
            val toGallery = Intent(
                Intent.ACTION_GET_CONTENT).setType("video/*")
            startActivityForResult(toGallery,PICK_VIDEO)
             //do this last but before it make sure to change cancel button to done
        }
        addVideoView!!.videoPlayButton.setOnClickListener {
            if(selectedVideo != null){
                addVideoView!!.videoToBeAdded.visibility = View.VISIBLE
                addVideoView!!.videoPlayButton.visibility = View.VISIBLE
                addVideoView!!.videoPauseButton.visibility = View.VISIBLE
                addVideoView!!.videoToBeAdded.start()

            }
        }

        addVideoView!!.videoPauseButton.setOnClickListener {
            if(selectedVideo != null) {
                addVideoView!!.videoToBeAdded.pause()
            }
        }


        addVideoView!!.cancelVidSelectButton.setOnClickListener {
            mAlertDialog.dismiss()
        }

    }


    fun saveAndNext() {
        if(checkpoints.isEmpty()) {
            Toast.makeText(context, "Please enter a checkpoint", Toast.LENGTH_LONG).show()
        } else {
            // add to viewModel
            sharedViewModel.addCheckpoints(checkpoints)

            navigateToAddTags()
        }
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


        if (name == null || name.equals("")
            || description == null || description.equals("")) {
            Toast.makeText(context, "Please fill out these fields", Toast.LENGTH_LONG).show()
        } else {
//        GlobalScope.launch(Dispatchers.Main) {
//            withContext(Dispatchers.Default) { uploadPicture() }
//        }

//        lifecycleScope.launch {
//            uploadAudio()
//            uploadPicture()
//            uploadVideo()
//        }
            Log.i(TAG, "imageURL is :" + imageUrl)
//        sharedViewModel.setImageUrl("hi")
//        sharedViewModel.getImageUrl().observe(this, Observer {
//                url -> Log.i(TAG, url)
//        })
//
//


            toUploadSelectedPic = selectedPic
            toUploadSelectedVideo = selectedVideo
            toUploadSelectedAudio = selectedAudio

            if (toUploadSelectedPic == null) {
                Toast.makeText(context, "Image not selected or still uploading", Toast.LENGTH_LONG)
                    .show()
            } else {
                uploadPicture()
                val newCP = Checkpoint(name, location, description, imageUrl, audioUrl, videoUrl)

                // need to add this checkpoint to tour arrayList
                checkpoints.add(newCP)
                //clearing data in dialogs for next checkPoint
                clearCheckpointMediaForNext()

                // TO display added checkpoints
                //creating adapter using CheckpointAdapter
                val adapter = CheckpointAdapter(activity!!, checkpoints)
                //attaching adapter to the listview
                mCheckpointsRecycler!!.adapter = adapter
                mCheckpointsRecycler!!.layoutManager = LinearLayoutManager(context)

                checkptName.setText("")
                checkptDesc.setText("")
            }
        }
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
                            "Place: " + place.name,
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
                if(data != null) {
                    selectedPic = data!!.data as Uri
                    //adds image to dialog
                    addImageView!!.imageToBeAdded.setImageURI(selectedPic)
                    //changes button text
                    addImageView!!.selectImageButton.text = "Edit Image"
                    addImageView!!.cancelPicSelectButton.text = "Done"
                }

            }
            PICK_VIDEO->{
                if(data != null) {
                    selectedVideo = data!!.data as Uri
                    //adds video to video view
                    addVideoView!!.videoToBeAdded.setVideoURI(selectedVideo)
                    addVideoView!!.videoToBeAdded.visibility = View.VISIBLE
                    addVideoView!!.videoPlayButton.visibility = View.VISIBLE
                    addVideoView!!.videoPauseButton.visibility = View.VISIBLE
                    //changes button text
                    addVideoView!!.selectVideoButton.text = "Edit Video"
                    addVideoView!!.cancelVidSelectButton.text = "Done"
                    //sends video off to upload to data base
                }
            }

            PICK_AUDIO->{
                if(data != null) {
                    selectedAudio = data!!.data as Uri
                    addAudioView!!.pauseButton.visibility = View.VISIBLE
                    addAudioView!!.playButton.visibility = View.VISIBLE
                    addAudioView!!.selectAudioButton.text = "Edit Audio"
                    addAudioView!!.cancelAudioSelectButton.text = "Done"
                    Log.i(TAG, "Uri is : "+ selectedAudio.toString())
                    //audioPlayer!!.setDataSource(context!!, selectedAudio!!)
                    //addAudioView.(selectedAudio) need to attribute the selected audio to a media player
                    //to play it in

                }
            }

        }
    }

    //functions below will add media to database
    fun uploadPicture(){
        if(toUploadSelectedPic!= null) {
            userRef = storageRef.child("/checkpointPictures")
                .child(toUploadSelectedPic!!.lastPathSegment.toString() + LocalDateTime.now() + ".jpg")

            val inputStream = context!!.contentResolver.openInputStream(toUploadSelectedPic!!)
            val yourDrawable = Drawable.createFromStream(inputStream, toUploadSelectedPic.toString())

            val imageBitmap = (addImageView!!.imageToBeAdded.drawable as BitmapDrawable).bitmap
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
                    imageUrl = task.result.toString()
                    Log.i(TAG, "uploaded pic to firebase")
                    uploadAudio()
                } else {
                    //Toast.makeText(applicationContext, "Could not upload pic", Toast.LENGTH_LONG).show()
                    // ...
                }
            }
        } else{
            Log.i(TAG, "Im in uploadpic- selectedPic==null")
            uploadAudio()
        }
    }

     fun uploadAudio(){
        if(toUploadSelectedAudio!=null) {
            Log.i(TAG, "Im in uploadAudio- selectedaudio!=null")
            userRef = storageRef.child("/checkpointAudio").
                child(toUploadSelectedAudio!!.lastPathSegment!!.toString() + LocalDateTime.now() + ".audio")
            val uploadTask = userRef.putFile(toUploadSelectedAudio!!)
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                userRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "task for audio is:" + task.result.toString())
                    //actual url stored here
                    audioUrl = task.result.toString()
                    Log.i(TAG, "uploaded audio to firebase")
                    uploadVideo()
                } else {
                    //Toast.makeText(applicationContext, "Could not upload pic", Toast.LENGTH_LONG).show()
                    // ...
                }
            }
        } else {
            Log.i(TAG, "Im in uploadAudio- selectedaudio == null")
            uploadVideo()
        }
    }
    fun uploadVideo(){
        if(toUploadSelectedVideo!=null) {
            Log.i(TAG, "Im in uploadAudio- selectedVideo!=null")
            userRef = storageRef.child("/checkpointVideos").
                child(toUploadSelectedVideo!!.lastPathSegment!!.toString() + LocalDateTime.now() + ".audio")
            val uploadTask = userRef.putFile(toUploadSelectedVideo!!)
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                userRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "task for video is:" + task.result.toString())
                    //actual url stored here
                    videoUrl = task.result.toString()
                    Log.i(TAG, "uploaded video to firebase")
                } else {
                    //Toast.makeText(applicationContext, "Could not upload pic", Toast.LENGTH_LONG).show()
                    // ...
                }
            }
        } else {
            Log.i(TAG, "Im in uploadVideo- selectedVideo == null")
        }

    }
    private fun clearCheckpointMediaForNext(){
        //urisOfSingleCheckpoint.clear()
        selectedPic = null
        if(addImageView != null) {
            addImageView!!.imageToBeAdded.visibility = View.INVISIBLE
            addImageView!!.selectImageButton.text = "Add Image"
            addImageView!!.cancelPicSelectButton.text = "Cancel"
            addImageView = null
        }
        selectedAudio = null
        if(addAudioView != null) {
            addAudioView!!.pauseButton.visibility = View.INVISIBLE
            addAudioView!!.playButton.visibility = View.INVISIBLE
            addAudioView!!.selectAudioButton.text = "Add Audio"
            addAudioView!!.cancelAudioSelectButton.text = "Cancel"
            addAudioView = null
        }

        selectedVideo = null
        if(addVideoView != null) {
            addVideoView!!.videoToBeAdded.visibility = View.INVISIBLE
            addVideoView!!.videoPlayButton.visibility = View.INVISIBLE
            addVideoView!!.videoPauseButton.visibility = View.INVISIBLE
            addVideoView!!.selectVideoButton.text = "Add Video"
            addVideoView!!.cancelVidSelectButton.text = "Cancel"
            addAudioView = null
        }
    }

}
