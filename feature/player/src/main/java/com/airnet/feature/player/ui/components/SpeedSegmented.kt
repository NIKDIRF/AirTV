package com.airnet.feature.player.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airnet.feature.player.theme.AirBlue
import com.airnet.feature.player.theme.AirGreen
import com.airnet.feature.player.util.noRippleClickable

@Composable
fun SpeedSegmented(
    items: List<Float>,
    selected: Float,
    onSelect: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(6.dp)

    Row(
        modifier
            .background(Color(0x66000000), shape)
            .border(1.dp, AirBlue.copy(alpha = 0.5f), shape)
            .clip(shape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val textStyle = MaterialTheme.typography.labelSmall

        items.forEachIndexed { index, f ->
            val sel = f == selected
            Box(
                modifier = Modifier
                    .noRippleClickable { onSelect(f) }
                    .background(if (sel) AirGreen.copy(alpha = 0.25f) else Color.Transparent)
                    .padding(horizontal = 4.dp, vertical = 1.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (f % 1f == 0f) "${f.toInt()}x" else "${f}x",
                    style = textStyle,
                    color = if (sel) AirGreen else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    softWrap = false
                )
            }
            if (index < items.lastIndex) {
                Box(
                    Modifier
                        .width(1.dp)
                        .height(16.dp)
                        .background(AirBlue.copy(alpha = 0.35f))
                )
            }
        }
    }
}
