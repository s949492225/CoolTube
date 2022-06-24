package com.syiyi.cooltube.ui.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun WideButton(titleId: Int, click: () -> Unit) {
    Button(
        onClick = { click.invoke() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
    ) {
        Text(stringResource(id = titleId))
    }
}