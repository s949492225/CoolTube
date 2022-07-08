package com.syiyi.cooltube.ui.page

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.syiyi.cooltube.const.TOKEN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.rerere.compose_setting.preference.mmkvPreference
import javax.inject.Inject


@HiltViewModel
class GlobalViewModel @Inject constructor(
    context: Application
) :
    AndroidViewModel(context) {

    var tokenFlow = MutableStateFlow<String?>(null)

    init {
        observeLogin()
    }

    private fun observeLogin() {
        viewModelScope.launch {
            tokenFlow.value = mmkvPreference.getString(TOKEN, null)
            mmkvPreference.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
                if (key == TOKEN) {
                    tokenFlow.value = sharedPreferences.getString(TOKEN, null)
                }
            }
        }
    }
}