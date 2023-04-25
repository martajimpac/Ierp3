package com.toools.ierp.ui.videoFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoSize
import com.otaliastudios.zoom.ZoomSurfaceView
import com.toools.ierp.R
import com.toools.ierp.databinding.FragmentVideoBinding


private const val TAG = "VideoFragment"
class VideoFragment : Fragment() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentVideoBinding.inflate(layoutInflater)
    }

    //crear exoplayer
    private var player: ExoPlayer ? =null

    //inicializar los botones para parar y reanudar el video
    private var playWhenReady = true //que empieze por defecto
    private var currentItem = 0 //desde el principio
    private var playbackPosition = 0L //recordar en que punto estaba viendo el usuario

    //esto es para saber en que estado esta el video y hacer callbacks
    private val playbackStateListener: Player.Listener = playbackStateListener()

    private val viewModel: VideoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePlayer()
    }

    //crear el exoplayer
    private fun initializePlayer() {
        //se encarga de elegir las pistas en base a una serie de cirterios como la resolucion, tasa de bits..
        val trackSelector = DefaultTrackSelector(requireContext()).apply{
            setParameters(buildUponParameters().setMaxVideoSizeSd())
            //para priorizar la selección de pistas de video con resolución SD, en lugar de pistas con resoluciones superiores, como HD o 4K, si están disponibles.
        }

        binding.apply{
            player = ExoPlayer.Builder(requireContext())
                .setTrackSelector(trackSelector)
                .build()
                .also{ player ->
                    player.addListener(playbackStateListener)

                    //crear el elemento multimedia para pasarle el contenido que va a reproducir
                    // usaremos un builder personalizado para poder especificar mejor las propiedades del video

                    val mediaItem = MediaItem.Builder()
                        .setUri(getString(R.string.media_url_mp4))
                        .setMediaMetadata(MediaMetadata.Builder()
                            .setTitle(getString(R.string.titulo_video_1))
                            .build())
                        .build()
                    player.setMediaItem(mediaItem)

                    tituloVideo.text = mediaItem.mediaMetadata.title

                    val secondMediaItem = MediaItem.Builder()
                        .setUri(getString(R.string.media_url_mp3))
                        .setMediaMetadata(MediaMetadata.Builder()
                            .setTitle(getString(R.string.titulo_video_2))
                            .build())
                        .build()
                    player.addMediaItem(secondMediaItem)

                    //inicializar las variables
                    player.playWhenReady = playWhenReady
                    player.seekTo(currentItem, playbackPosition)

                    //hacer que la superficie en la que se muestre el video sea surfaceview
                    surfaceView.addCallback(object : ZoomSurfaceView.Callback {
                        override fun onZoomSurfaceCreated(view: ZoomSurfaceView) {
                            player.setVideoSurface(view.surface)
                        }
                        override fun onZoomSurfaceDestroyed(view: ZoomSurfaceView) {}
                    })

                    playerControlView.player = player
                    playerControlView.showTimeoutMs = 0
                    playerControlView.show()

                    player.prepare()
                }
        }
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }


    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (player == null) {
            initializePlayer()
        }
    }


    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    //permitir que se vea en pantalla completa
    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window,false)
        WindowInsetsControllerCompat(requireActivity().window, binding.playerControlView).let{ controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
        }
    }

    //metodo para liberar los recursos y destruir el player
    private fun releasePlayer(){
        player?.let{ exoPlayer ->
            playWhenReady = exoPlayer.playWhenReady
            currentItem = exoPlayer.currentMediaItemIndex
            playbackPosition = exoPlayer.currentPosition
            exoPlayer.removeListener(playbackStateListener)//remove the listener
            exoPlayer.release()
        }
    }

    /*---------------------------
    LISTENER
    -----------------------------*/
    private fun playbackStateListener()= object: Player.Listener{
        //actualizar el titulo del video
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val newTitle = mediaItem?.mediaMetadata?.title
            binding.tituloVideo.text = newTitle
        }
        //para que el zoom funcione correctamente
        override fun onVideoSizeChanged(videoSize: VideoSize) {
            binding.surfaceView.setContentSize(videoSize.width.toFloat(), videoSize.height.toFloat())
        }
    }
}