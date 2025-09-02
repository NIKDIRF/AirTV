package com.airnet.feature.channels.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airnet.domain.channels.Channel
import com.airnet.feature.channels.theme.AirBlueLight
import com.airnet.feature.channels.theme.CardBg

@Composable
fun ChannelRowCard(
    channel: Channel,
    onClick: () -> Unit
) {
    val rowMinHeight = 36.dp
    val vPad = 10.dp

    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CardBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = vPad)
            .heightIn(min = rowMinHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            channel.name,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.weight(1f),
            maxLines = 1
        )
        channel.group?.let {
            Text(
                it,
                color = AirBlueLight,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }
    }
}