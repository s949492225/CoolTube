package com.syiyi.cooltube.ui.page.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.syiyi.cooltube.local.LocalNavController

@Composable
fun TopBar() {
    val navController = LocalNavController.current
    SmallTopAppBar(
        modifier = Modifier
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(),
        title = { Text(text = "设置", modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 0.dp)) },
        navigationIcon = {
            Icon(
                Icons.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp, 0.dp, 0.dp, 0.dp)
                    .clickable {
                        navController.popBackStack()
                    }
            )
        }
    )
}