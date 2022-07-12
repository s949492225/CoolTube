package com.syiyi.cooltube.ui.page.home

import com.fasterxml.jackson.databind.ObjectMapper
import com.syiyi.cooltube.api.RetrofitInstance
import com.syiyi.cooltube.base.UiEffect
import com.syiyi.cooltube.base.UiIntent
import com.syiyi.cooltube.base.UiState
import com.syiyi.cooltube.base.UiViewModel
import com.syiyi.cooltube.const.HOME_DATA
import com.syiyi.cooltube.model.StreamItem
import com.syiyi.cooltube.util.RefreshState
import com.syiyi.cooltube.util.toObjet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import me.rerere.compose_setting.preference.mmkvPreference
import javax.inject.Inject

sealed interface HomeIntent : UiIntent {
    object LoadLocal : HomeIntent
    object Refresh : HomeIntent
}

sealed interface HomeEffect : UiEffect {
    class Toast(val message: String) : HomeEffect
}

data class HomeUiState(
    val data: List<StreamItem> = emptyList(),
    val rs: RefreshState = RefreshState.INIT,
    val error: String = "网络错误!"
) : UiState

@HiltViewModel
class HomeViewModel @Inject constructor() : UiViewModel<HomeIntent, HomeUiState>() {

    override fun defaultState(): HomeUiState = HomeUiState()

    override fun defaultIntent(): List<HomeIntent> =
        listOf(HomeIntent.LoadLocal, HomeIntent.Refresh)

    override fun Flow<HomeIntent>.mapIntent(): Flow<HomeUiState> = map {
        when (it) {
            is HomeIntent.LoadLocal -> cacheDataFlow()
            is HomeIntent.Refresh -> remoteFlow()
        }
    }.flattenConcat()

    private suspend fun remoteFlow(): Flow<HomeUiState> {
        return flow { emit(RetrofitInstance.api.getTrending("US")) }
            .map { it.toHomeState() }
            .onEach { if (it.rs == RefreshState.SUCCESS) it.saveCacheData() }
            .onStart {
                state.value.data.apply {
                    if (isNullOrEmpty()) {
                        emit(HomeUiState(rs = RefreshState.REFRESH))
                    } else {
                        emit(state.value.copy(rs = RefreshState.PULL_REFRESH))
                    }
                }
            }
            .catch { error ->
                val message = error.message ?: "网络错误"
                if (state.value.data.isEmpty()) {
                    emit(HomeUiState(rs = RefreshState.REFRESH, error = message))
                } else {
                    emit(
                        HomeUiState(
                            data = state.value.data,
                            rs = RefreshState.PULL_ERROR,
                            error = message
                        )
                    )
                    dispatch(HomeEffect.Toast(message))
                }
            }
    }

    private suspend fun cacheDataFlow(): Flow<HomeUiState> {
        return flow {
            val localData = mmkvPreference.getString(HOME_DATA, null)
            if (localData == null) {
                emit(HomeUiState(rs = RefreshState.LOCAL_EMPTY))
            } else {
                emit(
                    HomeUiState(
                        rs = RefreshState.LOCAL_SUCCESS,
                        data = localData.toObjet()
                    )
                )
            }
        }
    }

    private fun List<StreamItem>?.toHomeState(): HomeUiState {
        return if (this.isNullOrEmpty()) {
            HomeUiState(rs = RefreshState.EMPTY)
        } else HomeUiState(rs = RefreshState.SUCCESS, data = this)
    }

    private fun HomeUiState.saveCacheData() {
        mmkvPreference.putString(HOME_DATA, ObjectMapper().writeValueAsString(this.data))
    }


}