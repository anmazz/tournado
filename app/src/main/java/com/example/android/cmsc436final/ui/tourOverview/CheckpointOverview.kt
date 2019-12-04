package com.example.android.cmsc436final.ui.tourOverview


import android.app.AlertDialog
import android.app.Dialog
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.SharedViewModel
import com.example.android.cmsc436final.model.Checkpoint
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.play_audio_dialogue.view.*



class CheckpointOverview: Fragment() {
    private lateinit var mModel: SharedViewModel
    private lateinit var nameView: TextView
    private lateinit var descriptionView: TextView
    private lateinit var locationView: TextView
    private lateinit var imageView: ImageView


    private lateinit var audioButton: Button
    private lateinit var videoButton: Button
    private lateinit var continueButton : FloatingActionButton

    private lateinit var currCheckpoint: Checkpoint

    private var  playAudioDialog: View? = null
    private var  playVideoDialog: View? = null

    private var audioPlayer :MediaPlayer? = MediaPlayer()

    companion object {
        private const val TAG = "InfoTab"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_checkpoint_overview, container, false)
        nameView = root.findViewById(R.id.checkpoint_name)
        descriptionView = root.findViewById(R.id.cp_description)
        locationView = root.findViewById(R.id.location)
        imageView = root.findViewById(R.id.checkpt_image)

        audioButton = root.findViewById(R.id.audio_button)
        videoButton = root.findViewById(R.id.video_button)
        continueButton = root.findViewById(R.id.continue_button)


        mModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
        var tour = mModel.getCurrentTour()!!
        currCheckpoint = tour!!.checkpoints?.get(mModel.getCurrentCheckpointNum())!!

        nameView.text = (currCheckpoint!!.name)
        descriptionView.text = currCheckpoint!!.description
        Glide.with(imageView.context).load(currCheckpoint.image).centerCrop().into(imageView)

        continueButton.setOnClickListener{
            if(mModel.getCurrChkptNumStartTour()==tour.checkpoints!!.size){
                val title = "Congrats!"
                val body = "You just completed the ${tour.name} tour!"
                showDialog(title, body)
            } else {
                findNavController().navigate(R.id.action_checkpoint_overview_to_start_tour)
            }
        }

        audioButton.setOnClickListener {
            if (currCheckpoint.audio.equals("")) {
                Toast.makeText(context, "No audio available", Toast.LENGTH_LONG).show()
            } else {
                playAudio()
            }
        }

        videoButton.setOnClickListener() {
            if (currCheckpoint.audio.equals("")) {
                Toast.makeText(context, "No video available", Toast.LENGTH_LONG).show()
            } else {
                playVideo()
            }
        }

        return root
    }

    private fun showDialog(title: String, body: String) {
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_checkpoint)

        val titleView = dialog.findViewById(R.id.tvTitle) as TextView
        val bodyView = dialog.findViewById(R.id.tvBody) as TextView
        titleView.text = title
        bodyView.text = body
        val yesBtn = dialog .findViewById(R.id.btn_yes) as Button
        yesBtn.setOnClickListener {
            findNavController().navigate(R.id.action_checkpoint_overview_to_navigation_home)
            //TODO: increase user tours done count

            mModel.reset()
            dialog.dismiss()
        }
        dialog.show()

    }

    fun playAudio() {
            Log.i(TAG, "Im about to select picture")
            playAudioDialog = LayoutInflater.from(context).inflate(R.layout.play_audio_dialogue, null)
            val builder = AlertDialog.Builder(context)
                .setView(playAudioDialog)
                .setTitle("Checkpoint Audio")

            val mAlertDialog = builder.show()


        playAudioDialog!!.playButton.setOnClickListener {
            //get url or something
                audioPlayer!!.reset()
                audioPlayer!!.setDataSource(currCheckpoint!!.audio)
                audioPlayer!!.prepare()
                audioPlayer!!.setOnPreparedListener(object: MediaPlayer.OnPreparedListener{
                    override fun onPrepared(mediaPlayer: MediaPlayer) {
                        mediaPlayer.start()
                    }
                })
        }
        //pause audio
        playAudioDialog!!.pauseButton.setOnClickListener {
            if (audioPlayer != null) {
                audioPlayer!!.stop()
            }
        }

        playAudioDialog!!.donePlayingAudio.setOnClickListener {
            mAlertDialog.dismiss()

        }

    }

    fun playVideo() {
        val url = currCheckpoint.video
        Log.i("PlayVideo", "Url "+ url)
        val uri = Uri.parse(url)
        Log.i("PlayVideo", "Uri "+ uri)
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.play_video_dialogue)
        val videoViewLandscape = dialog.findViewById(R.id.videoToBeAdded) as VideoView

        videoViewLandscape.setVideoURI(uri)
        videoViewLandscape.requestFocus()
        videoViewLandscape.start()


//        val play = dialog.findViewById(R.id.videoPlayButton) as ImageButton
//        play.setOnClickListener {
//            videoViewLandscape.start()
//        }
//
//        val pause = dialog.findViewById(R.id.videoPlayButton) as ImageButton
//        pause.setOnClickListener {
//            videoViewLandscape.pause()
//        }
        val done = dialog.findViewById(R.id.donePlayingVideoButton) as Button
        done.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

}