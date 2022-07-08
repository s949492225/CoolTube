package com.syiyi.cooltube.ui.page

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.syiyi.cooltube.R
import com.syiyi.cooltube.local.LocalNavController
import com.syiyi.cooltube.ui.page.home.HomePage
import com.syiyi.cooltube.ui.page.library.LibraryPage
import com.syiyi.cooltube.ui.page.subscription.SubscriptionPage
import com.syiyi.cooltube.util.toast
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed class IndexPageFragment(
    val route: String,
    val icon: ImageVector,
    val label: Int
) {
    object Home : IndexPageFragment("home", Icons.Outlined.Home, R.string.bottom_nav_home)
    object Attention :
        IndexPageFragment(
            "subscription",
            Icons.Outlined.Subscriptions,
            R.string.bottom_nav_subscription
        )

    object Library :
        IndexPageFragment("library", Icons.Filled.AccountCircle, R.string.bottom_nav_library)
}

val items = listOf(
    IndexPageFragment.Home,
    IndexPageFragment.Attention,
    IndexPageFragment.Library,
)

@Composable
@Preview
fun IndexPage(
    globalVM: GlobalViewModel = hiltViewModel()
) {
    val tabNavController = rememberNavController()
    val token by globalVM.tokenFlow.collectAsState()
    val context = LocalContext.current

    val msgChannel = remember { Channel<String>() }
    val msgFlow = remember { msgChannel.receiveAsFlow() }

    LaunchedEffect(Unit) {
        msgFlow.onEach { context.toast(it) }.collect()
    }
    Scaffold(
        topBar = { TopBar() },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                    )
                )
            ) {
                val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val rememberCoroutineScope = rememberCoroutineScope()
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(stringResource(screen.label), fontSize = 12.sp) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            if (token.isNullOrEmpty()) {
                                rememberCoroutineScope.launch { msgChannel.send("请先登录") }
                            } else {
                                tabNavController.navigate(screen.route) {
                                    popUpTo(tabNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            tabNavController,
            startDestination = IndexPageFragment.Home.route,
            Modifier.padding(innerPadding)
        ) {
            composable(IndexPageFragment.Home.route) { HomePage() }
            composable(IndexPageFragment.Attention.route) { SubscriptionPage() }
            composable(IndexPageFragment.Library.route) { LibraryPage() }
        }
    }
}

@Composable
fun TopBar() {
    val navController = LocalNavController.current

    SmallTopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title = { Text(text = stringResource(R.string.app_name)) },
        actions = {
            IconButton(onClick = { }) {
                Icon(
                    Icons.Outlined.Search,
                    null,
                    modifier = Modifier.size(25.dp)
                )
            }
            IconButton(onClick = {
                navController.navigate("setting")
            }) {
                Icon(
                    Icons.Outlined.Settings,
                    null,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    )
}
