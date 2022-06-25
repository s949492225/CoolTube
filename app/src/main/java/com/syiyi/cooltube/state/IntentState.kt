package com.syiyi.cooltube.state

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import com.syiyi.cooltube.util.findActivity

/**
 * 处理activity的intent
 */
@Composable
fun OnNewIntentListener(handler: (Intent) -> Unit) {
    val context = LocalContext.current
    DisposableEffect(Unit){
        val activity = context.findActivity() as ComponentActivity
        activity.addOnNewIntentListener(handler)
        onDispose {
            activity.removeOnNewIntentListener(handler)
        }
    }
}