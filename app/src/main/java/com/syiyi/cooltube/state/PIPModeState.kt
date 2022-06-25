package com.syiyi.cooltube.state

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.PictureInPictureModeChangedInfo
import com.syiyi.cooltube.util.findActivity

@Composable
fun PipModeListener(handler: (PictureInPictureModeChangedInfo) -> Unit) {
    val activity = LocalContext.current.findActivity() as ComponentActivity
    DisposableEffect(Unit){
        activity.addOnPictureInPictureModeChangedListener(handler)
        onDispose {
            activity.removeOnPictureInPictureModeChangedListener(handler)
        }
    }
}