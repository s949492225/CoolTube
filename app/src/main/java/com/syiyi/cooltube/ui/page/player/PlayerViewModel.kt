package com.syiyi.cooltube.ui.page.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syiyi.cooltube.api.RetrofitInstance
import com.syiyi.cooltube.model.Streams
import com.syiyi.cooltube.util.RefreshState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainState<T>(
    var data: T? = null,
    val error: String? = null,
    val refresh: RefreshState = RefreshState.INIT
)

@HiltViewModel
class PlayerViewModel @Inject constructor() : ViewModel() {

    var mainState = MutableStateFlow<MainState<Streams>>(MainState())


    fun fetchSteam(id: String) {
        viewModelScope.launch {
            try {
                mainState.value = mainState.value.copy(refresh = RefreshState.PULL_REFRESH)
                val steam = RetrofitInstance.api.getStreams(id)
                mainState.value = MainState(
                    steam,
                    refresh = RefreshState.SUCCESS
                )
            } catch (e: Exception) {
                mainState.value = MainState(error = e.message, refresh = RefreshState.ERROR)
            }
        }
    }

}