package com.example.android.cmsc436final.ui.addTour

import android.app.AlertDialog
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.SharedViewModel
import com.example.android.cmsc436final.model.Checkpoint
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.add_image_dialogue.view.*

class AddTourCheckpoints: Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var checkptName: TextInputEditText
    private lateinit var checkptDesc: TextInputEditText
//    Buttons
    private lateinit var buttonAddLocation: Button
    private lateinit var buttonAddPicture: Button
    private lateinit var buttonAddVideo: Button
    private lateinit var buttonAddAudio: Button
    private lateinit var buttonAddCheckpoint: Button
    private lateinit var buttonNext: Button
    private lateinit var selectedPic: ImageView
    private lateinit var selectedVideo: VideoView
    private lateinit var selectedAudio: MediaStore.Audio
    private val TAG = "In addTourCheckpoints"


    // ArrayList of Checkpoints
    private lateinit var checkpoints: MutableList<Checkpoint>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedViewModel =
            ViewModelProviders.of(this).get(sharedViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_add_tour_1, container, false)


        // UI elements
        checkptName = root.findViewById<View>(R.id.cp_name) as TextInputEditText
        checkptDesc = root.findViewById<View>(R.id.cp_descrip) as TextInputEditText

        // buttons
        buttonAddLocation = root.findViewById<View>(R.id.add_cp_location_button) as Button
        buttonAddPicture = root.findViewById<View>(R.id.add_cp_picture_button) as Button
        buttonAddVideo = root.findViewById<View>(R.id.add_cp_video_button) as Button
        buttonAddAudio = root.findViewById<View>(R.id.add_cp_audio_button) as Button


        buttonNext = root.findViewById<View>(R.id.next_button) as Button

        buttonAddCheckpoint = root.findViewById<View>(R.id.add_cp_button) as Button

        // list of checkpoints we keep adding to
        checkpoints = ArrayList()


        buttonNext.setOnClickListener() {
            saveAndNext()
        }

        buttonAddPicture.setOnClickListener() {
            addPicture()
        }
        buttonAddVideo.setOnClickListener() {
            addAudio()
        }
        buttonAddAudio.setOnClickListener() {
            addVideo()
        }

        return root
    }

    // TODO make sure this is adding just pictures
    private fun addPicture(){
            Log.i(TAG, "Im about to reset password")
            val addImageView = LayoutInflater.from(context).inflate(R.layout.add_image_dialogue, null)
            val builder = AlertDialog.Builder(context)
                .setView(addImageView)
                .setTitle("Enter email for reset instructions")
            val mAlertDialog = builder.show()
        addImageView.selectImageButton.setOnClickListener{
                //mAlertDialog.dismiss() do this last but before it make sure to change cancel button to done
                //actually reset here
//                val email = addImageView.resetPasswordEmailEditText.text.toString()
//                auth.sendPasswordResetEmail(email).addOnCompleteListener{ task ->
//                    if(task.isSuccessful){
//                        Toast.makeText(applicationContext, "Reset instructions have been sent to email", Toast.LENGTH_LONG).show()
//                    }
//                }
            }

            addImageView.cancelPicSelectButton.setOnClickListener {
                mAlertDialog.dismiss()
            }

    }

    // TODO make sure this is adding just audio
    private fun addAudio(){

    }

    // TODO make sure this is adding just video
    private fun addVideo(){

    }

    fun saveAndNext() {
        // get strings from textboxes
        val tourNameStr = checkptName.text.toString().trim { it <= ' ' }
        val tourDescripStr = checkptDesc.text.toString()

        // add to viewModel
        sharedViewModel.addCheckpoints(checkpoints)
    }

    //    TODO navigate to the add tags page
    private fun navigateToAddTags(){
        findNavController().navigate(R.id.action_navigation_add_tour_to_navigation_add_media)
    }

}