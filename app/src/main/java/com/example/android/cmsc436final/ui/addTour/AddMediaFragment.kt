package com.example.android.cmsc436final.ui.addTour

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.android.cmsc436final.R
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_add_media.view.*

class AddMediaFragment: Fragment() {
    private lateinit var addPhotoBttn: Button
    private lateinit var addVideoBttn: Button
    private lateinit var addAudioBttn: Button
    private lateinit var uploadMediaBttn: Button


    private var PICK_IMAGE = 4
    private var PICK_VIDEO = 5
    private var PICK_AUDIO = 6
    private lateinit var selectedMedia: Uri


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(com.example.android.cmsc436final.R.layout.fragment_add_media, container, false)
        addPhotoBttn = root.findViewById(R.id.addPhotoButton)
        addVideoBttn = root.findViewById(R.id.addVideoButton)
        addAudioBttn = root.findViewById(R.id.addAudioButton)
        uploadMediaBttn = root.findViewById(R.id.uploadSelectedMedia)

        addPhotoBttn.setOnClickListener{selectImage()}
        addVideoBttn.setOnClickListener{selectVideo()}
        addAudioBttn.setOnClickListener{selectAudio()}
        uploadMediaBttn.setOnClickListener{uploadMedia()}

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
        val toGallery = Intent(
            Intent.ACTION_PICK
            , MediaStore.Video.Media.INTERNAL_CONTENT_URI)
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
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                PICK_IMAGE->{
                    selectedMedia = data!!.data as Uri
                    //TODO: add selected media to list view, upload, and set upload indicator on list
                    //view as true
                }
                PICK_VIDEO->{
                    selectedMedia = data!!.data as Uri
                    //TODO: add selected media to list view, upload, and set upload indicator on list
                    //view as true
                }
                PICK_AUDIO->{
                    selectedMedia = data!!.data as Uri
                    //TODO: add selected media to list view, upload, and set upload indicator on list
                    //view as true
                }
            }

        }
    }
}