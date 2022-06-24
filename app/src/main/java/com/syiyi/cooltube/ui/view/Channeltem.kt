package com.syiyi.cooltube.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.syiyi.cooltube.model.Subscription

@Composable
fun ChannelItem(item: Subscription) {
    Column(modifier = Modifier.width(60.dp)) {
        AsyncImage(
            model = item.avatar,
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(item.name ?: "", maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis)
        Spacer(modifier = Modifier.size(6.dp))
    }
}