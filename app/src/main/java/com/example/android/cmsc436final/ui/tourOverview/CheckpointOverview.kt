package com.example.android.cmsc436final.ui.tourOverview

import android.location.Geocoder
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.android.cmsc436final.R
import com.example.android.cmsc436final.SharedViewModel
import com.example.android.cmsc436final.model.Checkpoint


class CheckpointOverview: Fragment() {
    private lateinit var mModel: SharedViewModel
    private lateinit var nameView: TextView
    private lateinit var descriptionView: TextView
    private lateinit var locationView: TextView
    private lateinit var imageView: ImageView


    private lateinit var audioButton: Button
    private lateinit var videoButton: Button

    private lateinit var currCheckpoint: Checkpoint

    companion object {
        private const val TAG = "InfoTab"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.tour_overview_info_tab, container, false)
//        mCheckpointsRecycler = root.findViewById(R.id.recycler_checkpoints)
//        mCheckpointsRecycler!!.layoutManager = LinearLayoutManager(context)
        nameView = root.findViewById(R.id.checkpoint_name)
        descriptionView = root.findViewById(R.id.cp_description)
        locationView = root.findViewById(R.id.location)
        imageView = root.findViewById(R.id.checkpt_image)

        audioButton = root.findViewById(R.id.audio_button)
        videoButton = root.findViewById(R.id.video_button)

        mModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
        var tour = mModel.getCurrentTour()
        currCheckpoint = tour!!.checkpoints?.get(mModel.getCurrentCheckpointNum())!!

        nameView.text = (currCheckpoint!!.name)
        descriptionView.text = currCheckpoint!!.description
        Glide.with(imageView.context).load(currCheckpoint.image).centerCrop().into(imageView)

        //Retrieve starting checkpoint location's city
        val geocoder = Geocoder(context)
        val lat = currCheckpoint.location.latitude
        val long = currCheckpoint.location.longitude
        locationView.text = geocoder.getFromLocation(lat, long, 1)[0].locality


        audioButton.setOnClickListener() {
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

    fun playAudio() {
        val url = currCheckpoint.audio
        val mediaPlayer: MediaPlayer? = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(url)
            prepare() // might take long! (for buffering, etc)
            start()
        }
    }

    fun playVideo() {
        val url = currCheckpoint.video
        val mediaPlayer: MediaPlayer? = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(url)
            prepare() // might take long! (for buffering, etc)
            start()
        }
    }

}