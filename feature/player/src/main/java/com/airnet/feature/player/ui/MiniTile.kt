package com.airnet.feature.player.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.media3.common.util.UnstableApi
import com.airnet.domain.channels.Channel
import com.airnet.feature.player.theme.OverlayBg
import com.airnet.shared.player.PlayerViewCompose
import com.airnet.shared.player.rememberExoHolder

@ExperimentalFoundationApi
@UnstableApi
@Composable
fun MiniTile(
    channel: Channel,
    onSwapToMain: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val holder = rememberExoHolder()
    LaunchedEffect(Unit) { holder.player.volume = 0f }
    LaunchedEffect(channel.url) { holder.play(channel.url, 1f) }
    DisposableEffect(Unit) { onDispose { holder.release() } }

    Box(
        modifier
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        PlayerViewCompose(holder = holder, modifier = Modifier.fillMaxSize())

        Box(
            Modifier
                .matchParentSize()
                .combinedClickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onSwapToMain,
                    onDoubleClick = onSwapToMain,
                    onLongClick = null
                )
        )

        Row(
            Modifier
                .zIndex(1f)
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .background(OverlayBg)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                channel.name,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = onClose,
                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.size(24.dp)
            ) { Icon(Icons.Rounded.Close, null) }
        }
    }
}
