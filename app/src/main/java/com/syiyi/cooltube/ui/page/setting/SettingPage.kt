package com.syiyi.cooltube.ui.page.setting

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.syiyi.cooltube.R
import com.syiyi.cooltube.ui.page.GlobalViewModel
import com.syiyi.cooltube.ui.view.OriginalItem
import com.syiyi.cooltube.ui.view.WideButton
import com.syiyi.cooltube.util.toast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun SettingPage(
    globalVM: GlobalViewModel = hiltViewModel(),
    settingVM: SettingViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        settingVM.msgFlow.onEach { context.toast(it) }.collect()
    }

    Scaffold(
        topBar = { TopBar() },
    ) { paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {
            Column(modifier = Modifier.padding(16.dp)) {
                OriginalItem(Icons.Filled.AccountCircle, "设置域名") {

                }
                val token by globalVM.tokenFlow.collectAsState()
                if (token.isNullOrEmpty()) {
                    val showLoginDialog by settingVM.showLoginDialogFlow.collectAsState()
                    HandleLogin(
                        showLoginDialog,
                        onLogin = settingVM::login,
                        onCancel = { settingVM.setLoginVisible(false) }
                    )
                    WideButton(R.string.login) { settingVM.setLoginVisible() }
                } else {
                    WideButton(R.string.logout) {
                        settingVM.logout()
                    }
                }
            }
        }
    }
}

