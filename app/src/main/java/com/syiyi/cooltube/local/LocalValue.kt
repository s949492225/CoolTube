package com.syiyi.cooltube.local

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

val LocalNavController = compositionLocalOf<NavController> { error("Not Init") }

val LocalDarkMode = compositionLocalOf { false }

val LocalWindowSizeClass = compositionLocalOf<WindowSizeClass> { error("Not Init") }