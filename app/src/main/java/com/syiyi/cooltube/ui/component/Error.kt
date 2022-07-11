package com.syiyi.cooltube.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun Error(
    error: String?,
    retry: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                retry.invoke()
            },
        contentAlignment = Alignment.Center,
    ) {

        Column {
            Text(
                text = error ?: "网络错误",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = "点击重试",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }

}