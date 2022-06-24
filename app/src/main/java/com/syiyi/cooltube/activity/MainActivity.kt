package com.syiyi.cooltube.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.core.view.WindowCompat
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.syiyi.cooltube.local.LocalNavController
import com.syiyi.cooltube.ui.page.IndexPage
import com.syiyi.cooltube.ui.page.setting.SettingPage
import com.syiyi.cooltube.ui.theme.CoolTubeTheme
import dagger.hilt.android.AndroidEntryPoint
import me.rerere.compose_setting.preference.mmkvPreference
import soup.compose.material.motion.materialSharedAxisZIn
import soup.compose.material.motion.materialSharedAxisZOut

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        // Night Mode
        mmkvPreference.getInt("nightMode", 0).let {
            when (it) {
                0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> error("bad const of night mode")
            }
        }

        setContent {
            val navController = rememberAnimatedNavController()
            val density = LocalDensity.current

            CompositionLocalProvider(
                LocalNavController provides navController,
            ) {
                CoolTubeTheme {
                    AnimatedNavHost(
                        modifier = Modifier
                            .fillMaxSize(),
//                            .background(MaterialTheme.colorScheme.background),
                        navController = navController,
                        startDestination = "index",
                        enterTransition = {
                            materialSharedAxisZIn().transition(true, density)
                        },
                        exitTransition = {
                            materialSharedAxisZOut().transition(true, density)
                        },
                        popEnterTransition = {
                            materialSharedAxisZIn().transition(false, density)
                        },
                        popExitTransition = {
                            materialSharedAxisZOut().transition(false, density)
                        }
                    ) {
                        composable("index") {
                            IndexPage()
                        }
                        composable("setting") {
                            SettingPage()
                        }
                    }
                }
            }
        }
    }
}