package com.example.android.cmsc436final.ui.addTour

import android.app.Activity
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
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.example.android.cmsc436final.MainActivity
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.model.Tour
import com.example.android.cmsc436final.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_add_media.view.*
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.util.*

class AddMediaFragment: Fragment() {
    private lateinit var addPhotoBttn: Button
    private lateinit var addVideoBttn: Button
    private lateinit var addAudioBttn: Button
    private lateinit var uploadMediaBttn: Button
    private lateinit var videoView: VideoView
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private lateinit var userRef: StorageReference

    private var PICK_IMAGE = 4
    private var PICK_VIDEO = 5
    private var PICK_AUDIO = 6
    private lateinit var selectedMedia: Uri
    private var TAG = "Add Media Fragment"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_add_media, container, false)
        addPhotoBttn = root.findViewById(R.id.addPhotoButton)
        addVideoBttn = root.findViewById(R.id.addVideoButton)
        addAudioBttn = root.findViewById(R.id.addAudioButton)
        uploadMediaBttn = root.findViewById(R.id.uploadSelectedMedia)
        videoView = root.findViewById(R.id.addMediaVideoView)

        //firebase initiation
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        db = FirebaseFirestore.getInstance()
        storageRef = storage.reference

        addPhotoBttn.setOnClickListener{selectImage()}
        addVideoBttn.setOnClickListener{selectVideo()}
        addAudioBttn.setOnClickListener{selectAudio()}
        uploadMediaBttn.setOnClickListener{uploadMedia()}

        //uploading pic url to firestore
        Log.i(TAG, "about to upload to firebase")



        return root
    }
    fun onClick(v: View?) {
        v.toString()
    }

    private fun uploadMedia(){

    }

    private fun selectImage(){
        val toGallery = Intent(
            Intent.ACTION_PICK
            , MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(toGallery,PICK_IMAGE)
    }
    private fun selectVideo(){
//        val toGallery = Intent(
//            Intent.ACTION_PICK
//            , MediaStore.Video.Media.INTERNAL_CONTENT_URI)
//        startActivityForResult(toGallery,PICK_VIDEO)
        val toGallery = Intent()
        toGallery.setType("video/*")
        toGallery.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(toGallery,PICK_VIDEO)
    }

    private fun selectAudio(){
        val toGallery = Intent(
            Intent.ACTION_PICK
            , MediaStore.Audio.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(toGallery,PICK_AUDIO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && data != null){
            when(requestCode){
                PICK_IMAGE->{
                    selectedMedia = data!!.data as Uri
                    addImage(selectedMedia)

                }
                PICK_VIDEO->{
                    selectedMedia = data!!.data as Uri
                    //TODO: add selected media to list view, upload, and set upload indicator on list
                    addVideo(selectedMedia)
                }
                PICK_AUDIO->{
                    selectedMedia = data!!.data as Uri
                    //TODO: add selected media to list view, upload, and set upload indicator on list
                    //view as true
                }
            }

        }
    }

    private fun addVideo(selectedMedia: Uri){
            videoView.setVideoURI(selectedMedia)
            var mediaController = MediaController(context)
            videoView.setMediaController(mediaController)
            //mediaController.setAnchorView(videoView)
    }

//    private fun getPath(uri:Uri):String{
//        var projectionArray = arrayOf(MediaStore.Video.Media.DATA)
//        var cursor = context!!.contentResolver.query(uri, projectionArray,null,null,null)
//        if(cursor!=null){
//            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
//            cursor.moveToFirst()
//            return cursor.getString(columnIndex)
//        }else
//            return ""
//
//
//    }

    private fun addImage(selectedMedia: Uri){
        userRef = storageRef.child("/checkpointPictures").child(selectedMedia.toString() + LocalDateTime.now() + ".jpg")
        //for setting selected media into imageview
        //userPic.setImageURI(selectedPhoto)


        val inputStream = context!!.contentResolver.openInputStream(selectedMedia)
        val yourDrawable = Drawable.createFromStream(inputStream, selectedMedia.toString() )

        val imageBitmap = (selectedMedia as BitmapDrawable).bitmap
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
               // val newUser = User(auth.uid as String, name, email, uristring, arrayListOf<Tour>(),arrayListOf<Tour>())
               // db.collection("users").document(auth.uid as String).set(newUser)
                //Toast.makeText(applicationContext, "Registration successful!", Toast.LENGTH_LONG).show()
                //startActivity(Intent(this@RegistrationActivity, MainActivity::class.java))
            } else {
                //Toast.makeText(applicationContext, "Could not upload pic", Toast.LENGTH_LONG).show()
                // ...
            }
        }

    }


}