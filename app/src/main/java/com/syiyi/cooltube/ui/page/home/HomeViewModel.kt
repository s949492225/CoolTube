package com.syiyi.cooltube.ui.page.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.databind.ObjectMapper
import com.syiyi.cooltube.api.RetrofitInstance
import com.syiyi.cooltube.const.HOME_DATA
import com.syiyi.cooltube.model.StreamItem
import com.syiyi.cooltube.util.RefreshState
import com.syiyi.cooltube.util.toObjet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.rerere.compose_setting.preference.mmkvPreference
import javax.inject.Inject

sealed interface HomeIntent {
    object LoadLocal : HomeIntent
    object Refresh : HomeIntent
}

sealed interface HomeEffect {
    class Toast(val message: String) : HomeEffect
}

data class HomeUiState(
    val data: List<StreamItem> = emptyList(),
    val rs: RefreshState = RefreshState.INIT,
    val error: String = "网络错误!"
)

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val intendFlow = Channel<HomeIntent>()
    val effectFlow = MutableSharedFlow<HomeEffect>()

    var homeState: StateFlow<HomeUiState> = intendFlow
        .receiveAsFlow()
        .dispatch()
        .flattenConcat()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun dispatch(vararg intents: HomeIntent) {
        viewModelScope.launch {
            intents.forEach {
                intendFlow.send(it)
            }
        }
    }

    init {
        dispatch(HomeIntent.LoadLocal, HomeIntent.Refresh)
    }

    private fun Flow<HomeIntent>.dispatch(): Flow<Flow<HomeUiState>> = map {
        when (it) {
            is HomeIntent.LoadLocal -> cacheDataFlow()
            is HomeIntent.Refresh -> remoteFlow()
        }
    }

    private suspend fun remoteFlow(): Flow<HomeUiState> {
        return flow { emit(RetrofitInstance.api.getTrending("US")) }
            .map { it.toHomeState() }
            .onEach { if (it.rs == RefreshState.SUCCESS) it.saveCacheData() }
            .onStart {
                homeState.value.data.apply {
                    if (isNullOrEmpty()) {
                        emit(HomeUiState(rs = RefreshState.REFRESH))
                    } else {
                        emit(homeState.value.copy(rs = RefreshState.PULL_REFRESH))
                    }
                }
            }
            .catch { error ->
                val message = error.message ?: "网络错误"
                if (homeState.value.data.isEmpty()) {
                    emit(HomeUiState(rs = RefreshState.REFRESH, error = message))
                } else {
                    emit(
                        HomeUiState(
                            data = homeState.value.data,
                            rs = RefreshState.PULL_ERROR,
                            error = message
                        )
                    )
                    effectFlow.emit(HomeEffect.Toast(message))
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