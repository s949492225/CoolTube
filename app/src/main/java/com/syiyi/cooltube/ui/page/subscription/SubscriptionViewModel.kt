package com.syiyi.cooltube.ui.page.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.syiyi.cooltube.api.RetrofitInstance
import com.syiyi.cooltube.const.SUBSCRIPTION_LIST_DATA
import com.syiyi.cooltube.const.FEED_DATA
import com.syiyi.cooltube.model.StreamItem
import com.syiyi.cooltube.model.Subscription
import com.syiyi.cooltube.util.RefreshState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.rerere.compose_setting.preference.mmkvPreference
import javax.inject.Inject

data class SubscriptionState<T>(
    var data: T? = null,
    val error: String? = null,
    val refreshState: RefreshState = RefreshState.INIT
)

@HiltViewModel
class SubscriptionViewModel @Inject constructor() : ViewModel() {
    var mainStateFlow =
        MutableStateFlow<SubscriptionState<List<StreamItem>>>(SubscriptionState())

    var subscriptionListFlow = MutableStateFlow<List<Subscription>>(emptyList())

    fun fetchChannel(token: String) {
        viewModelScope.launch {
            subscriptionListFlow.value = getSubscriptionCacheData() ?: listOf()
            try {
                subscriptionListFlow.value = RetrofitInstance.api.subscriptions(token)
                cacheSubscriptionData(subscriptionListFlow.value)
            } catch (e: Exception) {
            }
        }
    }

    fun fetchFeed(token: String) {
        viewModelScope.launch {
            mainStateFlow.value.data = getFeedCacheData() ?: listOf()
            val data = mainStateFlow.value.data
            try {
                val rs = if (data.isNullOrEmpty()) {
                    RefreshState.REFRESH
                } else {
                    RefreshState.PULL_REFRESH
                }
                mainStateFlow.value = mainStateFlow.value.copy(refreshState = rs)
                val trendingList = RetrofitInstance.api.getFeed(token)
                mainStateFlow.value =
                    SubscriptionState(trendingList, refreshState = RefreshState.SUCCESS)
                cacheFeedData(trendingList)
            } catch (e: Exception) {
                mainStateFlow.value =
                    SubscriptionState(error = e.message, refreshState = RefreshState.ERROR)
            }
        }
    }

    private fun cacheFeedData(dataList: List<StreamItem>) {
        mmkvPreference.putString(FEED_DATA, ObjectMapper().writeValueAsString(dataList))
    }

    private fun getFeedCacheData(): List<StreamItem>? {
        val localData = mmkvPreference.getString(FEED_DATA, null) ?: return null

        return ObjectMapper().readValue(localData, object : TypeReference<List<StreamItem>>() {})
    }

    private fun cacheSubscriptionData(dataList: List<Subscription>) {
        mmkvPreference.putString(
            SUBSCRIPTION_LIST_DATA,
            ObjectMapper().writeValueAsString(dataList)
        )
    }

    private fun getSubscriptionCacheData(): List<Subscription>? {
        val localData = mmkvPreference.getString(SUBSCRIPTION_LIST_DATA, null) ?: return null

        return ObjectMapper().readValue(localData, object : TypeReference<List<Subscription>>() {})
    }

}