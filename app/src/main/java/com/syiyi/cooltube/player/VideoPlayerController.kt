package com.syiyi.cooltube.player

import android.app.Activity
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.video.VideoSize
import com.syiyi.cooltube.local.LocalNavController
import com.syiyi.cooltube.state.OnNewIntentListener
import com.syiyi.cooltube.state.PipModeListener
import com.syiyi.cooltube.ui.component.Center
import com.syiyi.cooltube.util.findActivity
import com.syiyi.cooltube.util.prettyDuration
import kotlinx.coroutines.delay
import kotlin.math.roundToLong

fun Modifier.adaptiveVideoSize(state: PlayerState): Modifier =
    fillMaxWidth().then(
        if (state.fullScreen.value) {
            Modifier.fillMaxHeight()
        } else {
            Modifier.aspectRatio(16 / 9f)
        }
    )

@Composable
fun PlayerController(
    state: PlayerState,
    title: String,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        state.showController()
        while (true) {
            if (System.currentTimeMillis() - state.showControllerTime.value > 4000) {
                state.showController.value = false
            }
            delay(1000)
        }
    }

    // 返回退出全屏
    BackHandler(
        enabled = state.fullScreen.value
    ) {
        state.exitFullScreen(context.findActivity())
    }

    // PipMode
    PipModeListener {
        if (it.isInPictureInPictureMode) {
            if (state.fullScreen.value) {
                state.exitFullScreen(context.findActivity())
            }
            state.showController.value = false
            state.fullScreen.value = true
        } else {
            state.fullScreen.value = false
            state.showController()
        }
    }

    // PIP Intent
    OnNewIntentListener {

    }

    var dragState by remember {
        mutableStateOf(0f)
    }

    Center(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        state.togglePlay()
                    },
                    onTap = {
                        state.toggleController()
                    }
                )
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        dragState = 0f
                    },
                    onDragEnd = {
                        val player = state.player
                        val duration  = player.duration
                        val target = player.currentPosition + (dragState.roundToLong() * 10)
                            .coerceAtLeast(0)
                            .coerceAtMost(
                                if(duration == C.TIME_UNSET) 0 else duration
                            )
                        state.player.seekTo(target)
                        dragState = 0f
                    },
                    onHorizontalDrag = { _, amount ->
                        if (state.isPlaying.value) {
                            dragState += amount
                        }
                    },
                    onDragCancel = {
                        dragState = 0f
                    }
                )
            }
    ) {
        AnimatedVisibility(
            visible = (state.showController.value || state.playbackState.value <= 2),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Controller(
                title = title,
                state = state,
            )
        }

        // 显示手势进度
        if (dragState != 0f) {
            val player = state.player
            val target = player.currentPosition + (dragState.roundToLong() * 10)
            Text(
                text = prettyDuration(target),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun Controller(
    title: String,
    state: PlayerState,
) {
    val navController  = LocalNavController.current
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()

    ) {
        // TopBar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = {
                    if (state.fullScreen.value) {
                        state.exitFullScreen(context.findActivity())
                    } else {
                        navController.popBackStack()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )

            var expand by remember { mutableStateOf(false) }
            TextButton(
                onClick = { expand = !expand },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = state.currentQuality.value
                )
                DropdownMenu(
                    expanded = expand,
                    onDismissRequest = { expand = false }
                ) {
                    state.mediaSources.value.forEach { entry ->
                        DropdownMenuItem(
                            text = {
                                Text(entry.first)
                            },
                            onClick = {
                                state.changeQuality(entry.first)
                                expand = false
                            }
                        )
                    }
                }
            }

            // Picture in picture button
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                IconButton(
                    onClick = { state.enterPIP(context as Activity) }
                ) {
                    Icon(Icons.Outlined.PictureInPicture, null)
                }
            }

            // Change the fit mode of the video player
            IconButton(
                onClick = {
                    state.changeFitMode()
                }
            ) {
                Icon(
                    imageVector = when (state.fitMode.value) {
                        PlayerState.FitMode.FIT_VIDEO -> Icons.Outlined.AspectRatio
                        PlayerState.FitMode.FIT_SCREEN -> Icons.Outlined.FitScreen
                    },
                    contentDescription = null
                )
            }
        }

        Center(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (state.isLoading.value && !state.isPlaying.value) {
                CircularProgressIndicator(
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }


        // Bottom Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (state.playbackState.value == Player.STATE_ENDED) {
                IconButton(
                    onClick = {
                        state.player.seekTo(0)
                        state.player.play()
                    }
                ) {
                    Icon(Icons.Outlined.Replay, null, tint = Color.White)
                }
            } else {
                IconButton(
                    onClick = {
                        state.togglePlay()
                    }
                ) {
                    if (state.isPlaying.value) {
                        Icon(Icons.Outlined.Pause, null, tint = Color.White)
                    } else {
                        Icon(Icons.Outlined.PlayArrow, null, tint = Color.White)
                    }
                }
            }

            var sliding by remember { mutableStateOf(false) }
            val position = state.observeVideoPositionState()
            var progressSlide by remember {
                mutableStateOf(0f)
            }
            LaunchedEffect(position.value, state.videoDuration.value) {
                if (!sliding) {
                    progressSlide = if (state.videoDuration.value > 0) {
                        (position.value.toFloat() / state.videoDuration.value.toFloat())
                            .coerceIn(0.0f..1.0f)
                    } else {
                        0f
                    }
                }
            }

            Slider(
                modifier = Modifier.weight(1f),
                value = progressSlide,
                onValueChange = {
                    sliding = true
                    progressSlide = it
                    state.showController()
                },
                onValueChangeFinished = {
                    state.player.seekTo(
                        (state.videoDuration.value * progressSlide).roundToLong()
                    )
                    sliding = false
                }
            )



            Text(
                text = prettyDuration((state.videoDuration.value * progressSlide).roundToLong()) + " / " + prettyDuration(
                    state.videoDuration.value
                ),
                color = Color.White
            )

            IconButton(
                onClick = {
                    if (state.videoSize.value != VideoSize.UNKNOWN) {
                        state.toggleFullScreen(context.findActivity())
                    }
                }
            ) {
                Icon(
                    imageVector = if (state.fullScreen.value) Icons.Outlined.FullscreenExit else Icons.Outlined.Fullscreen,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        LinearProgressIndicator(
            progress = state.observeBufferPct().value / 100.0f,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f)
        )
    }
}