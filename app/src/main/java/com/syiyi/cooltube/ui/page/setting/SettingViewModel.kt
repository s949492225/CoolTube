package com.syiyi.cooltube.ui.page.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syiyi.cooltube.api.RetrofitInstance
import com.syiyi.cooltube.const.TOKEN
import com.syiyi.cooltube.model.Login
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import me.rerere.compose_setting.preference.mmkvPreference
import javax.inject.Inject


@HiltViewModel
class SettingViewModel @Inject constructor() : ViewModel() {

    private val msgChannel = Channel<String>()
    val msgFlow = msgChannel.receiveAsFlow()
    var showLoginDialogFlow = MutableStateFlow(false)

    fun setLoginVisible(show: Boolean = true) {
        showLoginDialogFlow.value = show
    }

    fun login(userName: String, password: String) {
        setLoginVisible(false)
        viewModelScope.launch {
            try {
                val loginResp = RetrofitInstance.api.login(Login(userName, password))
                if (!loginResp.error.isNullOrEmpty()) {
                    msgChannel.send(loginResp.error!!)
                } else {
                    mmkvPreference.putString(TOKEN, loginResp.token)
                    msgChannel.send("登录成功")
                }
            } catch (e: Exception) {
                msgChannel.send(e.message ?: "网络错误")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            mmkvPreference.putString(TOKEN, null)
            msgChannel.send("推出成功")
        }
    }

}