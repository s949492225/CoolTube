package com.syiyi.cooltube.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.syiyi.cooltube.util.RefreshState

@Composable
fun StatusBox(
    refreshState: RefreshState,
    error: String?,
    retry: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                if (refreshState == RefreshState.REFRESH_ERROR) {
                    retry.invoke()
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        when (refreshState) {
            RefreshState.REFRESH_FIRST -> {
                CircularProgressIndicator()
            }
            RefreshState.REFRESH_ERROR -> {
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
            else -> {
                content()
            }
        }
    }
}