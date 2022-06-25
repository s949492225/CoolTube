package com.syiyi.cooltube.ui.page.player

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.syiyi.cooltube.player.VideoPlayer
import com.syiyi.cooltube.player.adaptiveVideoSize
import com.syiyi.cooltube.player.rememberPlayerState
import com.syiyi.cooltube.ui.component.StatusBox
import com.syiyi.cooltube.util.*

@Composable
fun PlayerPage(id: String?) {

    val mainVM: PlayerViewModel = hiltViewModel()
    val mainState by mainVM.mainState.collectAsState()

    val playerState = rememberPlayerState()

    if (mainState.refresh == RefreshState.INIT) {
        mainVM.fetchSteam(id!!)
    }

    LaunchedEffect(mainState.data) {
        mainState.data?.apply {
            val mediaSources = mediaSources()
            playerState.handleMediaItem(
                mediaSources = mediaSources,
                autoPlay = true,
                quality = mediaSources.first().first
            )
        }
    }

    fun getTitle(): String {
        return ""
    }

    val playerComponent = remember {
        movableContentOf {
            VideoPlayer(
                modifier = Modifier
                    .padding(
                        if (playerState.fullScreen.value)
                            PaddingValues(0.dp)
                        else
                            WindowInsets.statusBars.asPaddingValues()
                    )
                    .adaptiveVideoSize(playerState),
                title = getTitle(),
                state = playerState
            )
        }
    }

    StatusBox(refreshState = mainState.refresh, error = mainState.error, retry = {
        mainVM.fetchSteam(id!!)
    }) {
        Column(modifier = Modifier.fillMaxSize()) {
            playerComponent()
        }
    }

}