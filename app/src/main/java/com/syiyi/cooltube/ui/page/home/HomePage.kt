package com.syiyi.cooltube.ui.page.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.syiyi.cooltube.local.LocalNavController
import com.syiyi.cooltube.ui.component.Empty
import com.syiyi.cooltube.ui.component.Error
import com.syiyi.cooltube.ui.component.FeedCard
import com.syiyi.cooltube.ui.component.Loading
import com.syiyi.cooltube.util.toast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun HomePage() {

    val homeVM: HomeViewModel = hiltViewModel()
    val homeState by homeVM.homeUIState.collectAsState()

    val navController = LocalNavController.current
    val context = LocalContext.current

    fun refresh() {
        homeVM.fireIntent(HomeIntent.Refresh)
    }

    fun handleEffect(homeEffect: HomeEffect) {
        when (homeEffect) {
            is HomeEffect.Toast -> context.toast(homeEffect.message)
        }
    }

    LaunchedEffect(Unit) {
        homeVM.effectFow.onEach { handleEffect(it) }.collect()
    }

    Surface(modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 0.dp)) {
        SwipeRefresh(
            state = rememberSwipeRefreshState((homeState is HomeUiState.PullRefresh)),
            onRefresh = { refresh() },
        ) {
            when (homeState) {
                is HomeUiState.Refresh -> Loading()
                is HomeUiState.LocalSuccess -> Content(homeState, navController)
                is HomeUiState.Success -> Content(homeState, navController)
                is HomeUiState.PullRefresh -> Content(homeState, navController)
                is HomeUiState.Error -> Error(homeState.message()) { refresh() }
                is HomeUiState.Empty -> Empty { refresh() }
                else -> {}
            }
        }
    }
}

@Composable
private fun Content(
    homeUIState: HomeUiState,
    navController: NavController
) {
    LazyColumn(
        state = rememberLazyListState(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(items = homeUIState.data()) { item ->
            FeedCard(item) {
                navController.navigate(
                    "player?id=${
                        item.url!!.replace(
                            "/watch?v=",
                            ""
                        )
                    }"
                )
            }
        }
    }
}

