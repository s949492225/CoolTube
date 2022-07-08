package com.syiyi.cooltube.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.core.view.WindowCompat
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.syiyi.cooltube.local.LocalNavController
import com.syiyi.cooltube.ui.page.IndexPage
import com.syiyi.cooltube.ui.page.player.PlayerPage
import com.syiyi.cooltube.ui.page.setting.SettingPage
import com.syiyi.cooltube.ui.theme.CoolTubeTheme
import dagger.hilt.android.AndroidEntryPoint
import soup.compose.material.motion.materialSharedAxisZIn
import soup.compose.material.motion.materialSharedAxisZOut

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

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
                        composable(
                            "player?id={id}",
                            arguments = listOf(navArgument("id") {})
                        ) { backStackEntry ->
                            PlayerPage(backStackEntry.arguments?.getString("id"))
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