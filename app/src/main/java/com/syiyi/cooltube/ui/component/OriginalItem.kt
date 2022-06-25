package com.syiyi.cooltube.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun OriginalItem(
    imageVector: ImageVector,
    text: String,
    click: () -> Unit
) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clickable { click.invoke() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.padding(8.dp))
            Icon(
                imageVector,
                contentDescription = null,
                modifier = Modifier.clickable { })
            Spacer(modifier = Modifier.padding(8.dp))
            Text(text)

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Filled.ChevronRight,
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))
        }
    }

}