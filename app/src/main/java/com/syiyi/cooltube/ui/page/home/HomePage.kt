package com.syiyi.cooltube.ui.page.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.syiyi.cooltube.ui.view.FeedCard
import com.syiyi.cooltube.ui.view.StatusBox
import com.syiyi.cooltube.util.RefreshState

@Composable
fun HomePage() {

    val homeViewModel: HomeViewModel = hiltViewModel()
    val homeState by homeViewModel.homeState.collectAsState()

    Surface(modifier = Modifier.padding(16.dp)) {

        if (homeState.refresh == RefreshState.INIT
            || homeState.refresh == RefreshState.REFRESH_ERROR
        ) {
            homeViewModel.fetch()
        }

        SwipeRefresh(
            state = rememberSwipeRefreshState((homeState.refresh == RefreshState.REFRESH)),
            onRefresh = { homeViewModel.fetch() },
        ) {
            StatusBox(homeState.refresh, homeState.error, homeViewModel::fetch)
            {
                LazyColumn(
                    state = rememberLazyListState(),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(items = homeState.data ?: mutableListOf()) { item ->
                        FeedCard(item)
                    }
                }
            }
        }
    }
}

