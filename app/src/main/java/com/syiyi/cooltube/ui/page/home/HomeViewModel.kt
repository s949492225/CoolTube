package com.syiyi.cooltube.ui.page.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.databind.ObjectMapper
import com.syiyi.cooltube.api.RetrofitInstance
import com.syiyi.cooltube.const.HOME_DATA
import com.syiyi.cooltube.model.StreamItem
import com.syiyi.cooltube.util.toObjet
import dagger.hilt.android.lifecycle.HiltViewModel
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

open class HomeUiState(open val data: List<StreamItem>? = null) {
    object Init : HomeUiState()
    object LocalEmpty : HomeUiState()
    data class LocalSuccess(override val data: List<StreamItem>) : HomeUiState(data)
    object Refresh : HomeUiState()
    data class PullRefresh(override val data: List<StreamItem>) : HomeUiState(data)
    data class Success(override val data: List<StreamItem>) : HomeUiState(data)
    data class Error(val message: String) : HomeUiState()
    
    object Empty : HomeUiState()

    fun data(): List<StreamItem> {
        return when (this) {
            is LocalSuccess -> this.data
            is Success -> this.data
            else -> emptyList()
        }
    }

    fun message(): String? {
        return when (this) {
            is Error -> this.message
            else -> null
        }
    }
}

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val intendFlow = MutableSharedFlow<HomeIntent>(replay = 2)
    val effectFow = MutableSharedFlow<HomeEffect>()

    var homeUIState: StateFlow<HomeUiState> = intendFlow
        .dispatch()
        .flattenConcat()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState.Init)

    init {
        fireIntent(HomeIntent.LoadLocal, HomeIntent.Refresh)
    }

    fun fireIntent(vararg intents: HomeIntent) {
        viewModelScope.launch {
            intents.forEach {
                intendFlow.emit(it)
            }
        }
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
            .onEach { if (it is HomeUiState.Success) it.saveCacheData() }
            .onStart {
                homeUIState.value.data.apply {
                    if (isNullOrEmpty()) {
                        emit(HomeUiState.Refresh)
                    } else {
                        emit(HomeUiState.PullRefresh(this))
                    }
                }
            }
            .catch { error ->
                val message = error.message ?: "网络错误"
                if (homeUIState.value.data.isNullOrEmpty()) {
                    emit(HomeUiState.Error(message))
                } else {
                    effectFow.emit(HomeEffect.Toast(""))
                }
            }
    }

    private suspend fun cacheDataFlow(): Flow<HomeUiState> {
        return flow {
            val localData = mmkvPreference.getString(HOME_DATA, null)
            if (localData == null) {
                emit(HomeUiState.LocalEmpty)
            } else {
                emit(HomeUiState.LocalSuccess(localData.toObjet()))
            }
        }
    }

    private fun List<StreamItem>?.toHomeState(): HomeUiState {
        return if (this.isNullOrEmpty()) {
            HomeUiState.Empty
        } else HomeUiState.Success(this)
    }

    private fun HomeUiState.Success.saveCacheData() {
        mmkvPreference.putString(HOME_DATA, ObjectMapper().writeValueAsString(this.data))
    }
}