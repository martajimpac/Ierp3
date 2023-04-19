package com.toools.ierp.ui.videoFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.Util
import com.toools.ierp.R
import com.toools.ierp.databinding.FragmentVideoBinding


private const val TAG = "VideoFragment"
class VideoFragment : Fragment() {

    //todo no estoy segura de que esto haya que hacerlo asi
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentVideoBinding.inflate(layoutInflater)
    }
    private var player: ExoPlayer? = null

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
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView()
    }

    fun setUpView(){


        initializePlayer()
    }

    //crear el exoplayer
    private fun initializePlayer() {
        //se encarga de elegir las pistas en base a una serie de cirterios como la resolucion, tasa de bits..
        val trackSelector = DefaultTrackSelector(requireContext()).apply{
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        //para priorizar la selección de pistas de video con resolución SD, en lugar de pistas con resoluciones superiores, como HD o 4K, si están disponibles.
        }

        player = ExoPlayer.Builder(requireContext())
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                binding.videoView.player = exoPlayer

                //crear el elemento multimedia para pasarle el contenido que va a reproducir
                // usaremos un builder personalizado para poder especificar mejor las propiedades del video

                val mediaItem = MediaItem.Builder()
                    .setUri(getString(R.string.media_url_mp4))
                    .setMediaMetadata(MediaMetadata.Builder()
                        .setTitle(getString(R.string.titulo_video_1))
                        .build())
                    .build()
                exoPlayer.setMediaItem(mediaItem)

                binding.tituloVideo.text = mediaItem.mediaMetadata.title

                val secondMediaItem = MediaItem.Builder()
                    .setUri(getString(R.string.media_url_mp3))
                    .setMediaMetadata(MediaMetadata.Builder()
                        .setTitle(getString(R.string.titulo_video_2))
                        .build())
                    .build()
                exoPlayer.addMediaItem(secondMediaItem)

                //inicializar las variables
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentItem, playbackPosition)
                //añadir el listener
                exoPlayer.addListener(playbackStateListener)

                //cambiar los iconos de control de video TODO

                exoPlayer.prepare()
            }
    }



    override fun onStart() {

        //si la version del api es mayor soportan multiples ventanas, por lo que deberiamos inicializar el player aqui
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }


    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        //antes de api 23 no hay garantia de que se va a llamar a onstop, por lo que liberamos los recursos en onPause
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    //permitir que se vea en pantalla completa
    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window,false)
        WindowInsetsControllerCompat(requireActivity().window, binding.videoView).let{ controller ->
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

    //imprimir el estado actual del player
    private fun playbackStateListener()= object: Player.Listener{
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE" //el player ha sido creado pero no preparado
                ExoPlayer.STATE_BUFFERING -> "Exoplayer.STATE_BUFFERING" //cargando
                ExoPlayer.STATE_READY -> "Exoplayer.STATE_READY" //preparado para iniciar a reproducir desde la posicion actual
                ExoPlayer.STATE_ENDED -> "Exoplayer.STATE_ENDED" //el video a terminado
                else -> "UNKNOWN STATE"
            }
            Log.d(TAG, "changed state to $stateString")
        }

        //para saber si se esta reproduciendo o no
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            val playingString: String = if (isPlaying) "PLAYING" else "NOT PLAYING"
            Log.d(TAG, "player is currently $playingString")
        }

        //actualizar el titulo del video
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val newTitle = mediaItem?.mediaMetadata?.title
            binding.tituloVideo.text = newTitle
        }
    }
}
