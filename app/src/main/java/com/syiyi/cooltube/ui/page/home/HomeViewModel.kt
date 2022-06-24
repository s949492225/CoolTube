package com.syiyi.cooltube.ui.page.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.syiyi.cooltube.api.RetrofitInstance
import com.syiyi.cooltube.const.HOME_DATA
import com.syiyi.cooltube.model.StreamItem
import com.syiyi.cooltube.util.RefreshState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.rerere.compose_setting.preference.mmkvPreference
import javax.inject.Inject

data class HomeState<T>(
    var data: T? = null,
    val error: String? = null,
    val refresh: RefreshState = RefreshState.INIT
)

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    var homeState = MutableStateFlow<HomeState<List<StreamItem>>>(HomeState())

    fun fetch() {
        viewModelScope.launch {
            homeState.value.data = getCacheData()?: listOf()
            val data = homeState.value.data;
            try {
                val refreshState = if (data.isNullOrEmpty()) {
                    RefreshState.REFRESH_FIRST
                } else {
                    RefreshState.REFRESH
                }
                homeState.value = homeState.value.copy(refresh = refreshState)
                val trendingList = RetrofitInstance.api.getTrending("US")
                homeState.value = HomeState(trendingList, refresh = RefreshState.READY)
                cacheData(trendingList)
            } catch (e: Exception) {
                homeState.value = HomeState(error = e.message, refresh = RefreshState.REFRESH_ERROR)
            }
        }
    }

    private fun cacheData(trendingList: List<StreamItem>) {
        mmkvPreference.putString(HOME_DATA, ObjectMapper().writeValueAsString(trendingList))
    }

    private fun getCacheData(): List<StreamItem>? {
        val localData = mmkvPreference.getString(HOME_DATA, null) ?: return null

        return ObjectMapper().readValue(localData, object : TypeReference<List<StreamItem>>() {})
    }

}