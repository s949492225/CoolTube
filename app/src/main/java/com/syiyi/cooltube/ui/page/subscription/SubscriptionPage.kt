package com.syiyi.cooltube.ui.page.subscription

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.syiyi.cooltube.ui.page.GlobalViewModel
import com.syiyi.cooltube.ui.component.ChannelItem
import com.syiyi.cooltube.ui.component.FeedCard
import com.syiyi.cooltube.ui.component.StatusBox
import com.syiyi.cooltube.util.RefreshState

@Composable
fun SubscriptionPage(
    subscriptionVM: SubscriptionViewModel = hiltViewModel(),
    globalVM: GlobalViewModel = hiltViewModel()
) {

    val mainState by subscriptionVM.mainStateFlow.collectAsState()
    val subscriptionList by subscriptionVM.subscriptionListFlow.collectAsState()

    val token by globalVM.tokenFlow.collectAsState()

    fun fetch() {
        subscriptionVM.fetchFeed(token!!)
        subscriptionVM.fetchChannel(token!!)
    }

    Surface(modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 0.dp)) {

        if (mainState.refreshState == RefreshState.INIT
            || mainState.refreshState == RefreshState.ERROR
        ) {
            fetch()
        }

        SwipeRefresh(
            state = rememberSwipeRefreshState((mainState.refreshState == RefreshState.PULL_REFRESH)),
            onRefresh = ::fetch,
        ) {
            StatusBox(mainState.refreshState, mainState.error, ::fetch)
            {
                LazyColumn(
                    state = rememberLazyListState(),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    item {
                        LazyRow(
                            state = rememberLazyListState(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(items = subscriptionList) { item ->
                                ChannelItem(item)
                            }
                        }
                    }
                    items(items = mainState.data ?: mutableListOf()) { item ->
                        FeedCard(item)
                    }
                }
            }

        }
    }
}