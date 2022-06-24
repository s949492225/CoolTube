package com.syiyi.cooltube.ui.view

import android.text.format.DateUtils
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.syiyi.cooltube.model.StreamItem
import com.syiyi.cooltube.util.formatShort

@Composable
fun FeedCard(item: StreamItem) {
    Column(Modifier.fillMaxWidth()) {
        Card {
            AsyncImage(
                model = item.thumbnail,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f),
                contentScale = ContentScale.FillWidth,
            )
        }

        Row(modifier = Modifier.padding(top = 8.dp)) {
            AsyncImage(
                model = item.uploaderAvatar,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    item.title ?: "",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    item.uploaderName + " • " +
                            item.views.formatShort() + " • " +
                            DateUtils.getRelativeTimeSpanString(item.uploaded!!),
                    fontSize = 14.sp
                )
            }
        }
        Spacer(modifier = Modifier.size(26.dp))
    }
}