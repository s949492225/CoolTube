package com.syiyi.cooltube.player

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.util.Rational
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.video.VideoSize
import com.syiyi.cooltube.util.VideoCache
import kotlinx.coroutines.delay
import java.lang.ref.WeakReference

@Composable
fun rememberPlayerState(
    context: Context = LocalContext.current
) = remember {
    PlayerState(
        context = context,
        builder = {
            ExoPlayer.Builder(context)
                .setHandleAudioBecomingNoisy(true)
                .setMediaSourceFactory(
                    DefaultMediaSourceFactory(VideoCache.getCache(context.applicationContext))
                )
                .build()
                .apply {
                    playWhenReady = true
                    repeatMode = Player.REPEAT_MODE_OFF
                }
        }
    )
}

class PlayerState(
    context: Context,
    builder: (Context) -> ExoPlayer
) : Player.Listener {
    val player = builder(context).also {
        it.addListener(this)
    }
    var surfaceView: WeakReference<View>? = null

    // Media state (video quality to media item)
    val currentQuality = mutableStateOf("")

    var mediaSources = mutableStateOf<List<Pair<String, MediaSource>>>(emptyList())

    // player state
    val isPlaying = mutableStateOf(false)
    val isLoading = mutableStateOf(false)
    val videoSize = mutableStateOf(VideoSize.UNKNOWN)
    val playbackState = mutableStateOf(Player.STATE_IDLE)
    val videoDuration = mutableStateOf(0L)

    // controller state
    val showController = mutableStateOf(true)
    val showControllerTime = mutableStateOf(0L)
    val fullScreen = mutableStateOf(false)
    val fitMode = mutableStateOf(FitMode.FIT_VIDEO)
    private var previousOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

    enum class FitMode {
        FIT_VIDEO, FIT_SCREEN
    }

    fun changeFitMode() {
        surfaceView?.get()?.let { view ->
            val playerView = view as StyledPlayerView
            this.fitMode.value = when (this.fitMode.value) {
                FitMode.FIT_VIDEO -> FitMode.FIT_SCREEN
                FitMode.FIT_SCREEN -> FitMode.FIT_VIDEO
            }
            when (fitMode.value) {
                FitMode.FIT_VIDEO -> {
                    playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
                FitMode.FIT_SCREEN -> {
                    playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            }
        }
    }

    fun handleMediaItem(
        mediaSources: List<Pair<String, MediaSource>>,
        autoPlay: Boolean,
        quality: String
    ) {
        this.mediaSources.value = mediaSources
        currentQuality.value = quality
        player.setMediaSource(mediaSources.find { it.first == quality }!!.second)

        // 自动播放
        if (autoPlay) {
            player.prepare()
        }
    }

    fun changeQuality(quality: String) {
        currentQuality.value = quality
        mediaSources.value.find { it.first == quality }?.second?.let {
            player.setMediaSource(
                it,
                player.currentPosition
            )
        }
    }

    fun enterPIP(activity: Activity) {
        if (videoSize.value == VideoSize.UNKNOWN) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fullScreen.value = true
            activity.enterPictureInPictureMode(
                PictureInPictureParams.Builder()
                    .setAspectRatio(
                        Rational(
                            videoSize.value.width,
                            videoSize.value.height
                        ).coerceAtMost(
                            Rational(239, 100)
                        ).coerceAtLeast(
                            Rational(100, 239)
                        )
                    )
                    .build()
            )
        }
    }

    fun togglePlay() {
        if (playbackState.value == Player.STATE_IDLE) {
            player.prepare()
        }

        if (isPlaying.value) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun showController() {
        showController.value = true
        showControllerTime.value = System.currentTimeMillis()
    }

    fun toggleController() {
        if (!showController.value) {
            showControllerTime.value = System.currentTimeMillis()
            showController.value = true
        } else {
            showController.value = false
        }
    }

    fun toggleFullScreen(activity: Activity) {
        if (fullScreen.value) {
            exitFullScreen(activity)
        } else {
            enterFullScreen(activity)
        }
    }

    private fun enterFullScreen(activity: Activity) {
        fullScreen.value = true
        previousOrientation = activity.requestedOrientation
        activity.requestedOrientation = if (videoSize.value.width > videoSize.value.height) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }
        WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    fun exitFullScreen(activity: Activity) {
        WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
            show(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_TOUCH
        }
        activity.requestedOrientation = previousOrientation
        fullScreen.value = false
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        this.isPlaying.value = isPlaying
        surfaceView?.get()?.keepScreenOn = isPlaying
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {
        this.isLoading.value = isLoading
    }

    override fun onVideoSizeChanged(videoSize: VideoSize) {
        this.videoSize.value = videoSize
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        if (playbackState == Player.STATE_READY) {
            this.videoDuration.value = player.duration
        }
        this.playbackState.value = playbackState
    }
}

@Composable
fun PlayerState.observeVideoPositionState() = produceState(
    initialValue = player.currentPosition
) {
    while (true) {
        value = player.currentPosition
        delay(1000)
    }
}

@Composable
fun PlayerState.observeBufferPct() = produceState(
    initialValue = player.bufferedPercentage
) {
    while (true) {
        value = player.bufferedPercentage
        delay(1000)
    }
}
