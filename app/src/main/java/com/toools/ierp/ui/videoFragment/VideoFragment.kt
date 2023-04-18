package com.toools.ierp.ui.videoFragment

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.toools.ierp.R
import com.toools.ierp.databinding.FragmentVideoBinding
import com.toools.ierp.ui.guardiasFragment.GuardiasViewModel

class VideoFragment : Fragment() {

    private lateinit var binding: FragmentVideoBinding
    private val viewModel: VideoViewModel by viewModels()
    private var player: ExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }

    override fun onResume() {
        super.onResume()
    }

    fun setUpView(){
        initializePlayer()
    }

    //crear el exoplayer
    private fun initializePlayer() {
        player = ExoPlayer.Builder(requireContext())
            .build()
            .also { exoPlayer ->
                binding.videoView.player = exoPlayer
                //crear el elemento multimedia
                val mediaItem = MediaItem.fromUri(getString(R.string.media_url_mp3))
                exoPlayer.setMediaItem(mediaItem)
            }
    }

    public override fun onStart() {
        super.onStart()
            initializePlayer()
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUi()
        initializePlayer()
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        binding.videoView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }


}