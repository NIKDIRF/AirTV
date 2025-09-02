package com.airnet.feature.channels.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airnet.feature.channels.theme.CardBg

@Composable
fun ChannelRowSkeleton() {
    val rowMinHeight = 36.dp
    val vPad = 10.dp

    val density = androidx.compose.ui.platform.LocalDensity.current
    val titleH = with(density) { MaterialTheme.typography.titleMedium.lineHeight.toDp() }
    val groupH = with(density) { MaterialTheme.typography.bodySmall.lineHeight.toDp() }

    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CardBg)
            .padding(horizontal = 12.dp, vertical = vPad)
            .heightIn(min = rowMinHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .weight(1f)
                .height(titleH)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF1A2632))
        )
        Spacer(Modifier.width(12.dp))
        Box(
            Modifier
                .width(64.dp)
                .height(groupH)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF1A2632))
        )
    }
}