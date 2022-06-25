package com.syiyi.cooltube.player

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.syiyi.cooltube.state.OnLifecycleEvent
import com.syiyi.cooltube.ui.component.Center
import java.lang.ref.WeakReference


@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    title: String,
    state: PlayerState,
) {
    CompositionLocalProvider(
        LocalContentColor provides Color.White
    ) {
        Center(
            modifier = Modifier
                .background(Color.Black)
                .then(modifier)
        ) {
            VideoPlayerSurface(
                state = state
            )
            PlayerController(
                state = state,
                title = title,
            )
        }

        OnLifecycleEvent { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    state.player.pause()
                }
                Lifecycle.Event.ON_START -> {
                    state.player.play()
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun VideoPlayerSurface(
    state: PlayerState
) {
    val context = LocalContext.current
    AndroidView(
        factory = {
            StyledPlayerView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }.also {
                state.surfaceView = WeakReference(it)
                it.player = state.player
                it.useController = false
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .semantics {
                contentDescription = "Video Player"
            }
    )
    DisposableEffect(state) {
        onDispose {
            state.player.release()
        }
    }
}
