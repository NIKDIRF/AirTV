@file:OptIn(ExperimentalFoundationApi::class)

package com.airnet.feature.player.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.media3.common.util.UnstableApi
import com.airnet.feature.player.theme.AirBlue
import com.airnet.feature.player.theme.AirGreen
import com.airnet.feature.player.theme.ControlsOverlayVPad
import com.airnet.feature.player.theme.ControlsRowHeight
import com.airnet.feature.player.theme.OverlayBg
import com.airnet.feature.player.theme.TopOverlayHeight
import com.airnet.feature.player.ui.components.SpeedSegmented
import com.airnet.feature.player.ui.components.TinySeekBar
import com.airnet.feature.player.util.formatMinusFromLive
import com.airnet.shared.player.PlayerViewCompose

@UnstableApi
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun VideoBox(
    modifier: Modifier,
    holder: com.airnet.shared.player.ExoHolder,
    hasVideo: Boolean,
    controlsVisible: Boolean,
    setControlsVisible: (Boolean) -> Unit,
    position: Long,
    duration: Long,
    onSeekChange: (Float) -> Unit,
    onSeekFinish: () -> Unit,
    speed: Float,
    onSpeedSelected: (Float) -> Unit,
    canPrev: Boolean,
    canNext: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    isFullscreen: Boolean,
    toggleFullscreen: () -> Unit,
    snackbarHostState: SnackbarHostState,
    onClose: () -> Unit,
    title: String
) {
    var scale by remember { mutableStateOf(1f) }
    var tx by remember { mutableStateOf(0f) }
    var ty by remember { mutableStateOf(0f) }

    BoxWithConstraints(modifier) {
        val transformState = rememberTransformableState { zoomChange, panChange, _ ->
            val newScale = (scale * zoomChange).coerceIn(1f, 3f)
            var newTx = tx;
            var newTy = ty
            if (newScale > 1f) {
                newTx += panChange.x; newTy += panChange.y
            } else {
                newTx = 0f; newTy = 0f
            }
            val maxDx = (constraints.maxWidth * (newScale - 1f)) / 2f
            val maxDy = (constraints.maxHeight * (newScale - 1f)) / 2f
            tx = if (newScale <= 1f) 0f else newTx.coerceIn(-maxDx, maxDx)
            ty = if (newScale <= 1f) 0f else newTy.coerceIn(-maxDy, maxDy)
            scale = newScale
        }

        Box(Modifier.matchParentSize()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clipToBounds()
                    .graphicsLayer {
                        translationX = tx; translationY = ty; scaleX = scale; scaleY = scale
                    }
                    .transformable(
                        state = transformState,
                        canPan = { _: Offset -> scale > 1f },
                        lockRotationOnZoomPan = true
                    )
                    .combinedClickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { setControlsVisible(!controlsVisible) },
                        onDoubleClick = { scale = 1f; tx = 0f; ty = 0f; setControlsVisible(true) },
                        onLongClick = null
                    )
            ) {
                PlayerViewCompose(holder = holder, modifier = Modifier.fillMaxSize())
                if (!hasVideo) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Аудиопоток (без видео)", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            if (controlsVisible) {
                Row(
                    Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .height(TopOverlayHeight)
                        .background(OverlayBg)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        title,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onClose,
                        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                    ) { Icon(Icons.Rounded.Close, null) }
                }

                if (canPrev) {
                    IconButton(
                        onClick = onPrev,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(6.dp)
                            .size(40.dp)
                            .zIndex(1f),
                        colors = IconButtonDefaults.iconButtonColors(contentColor = AirGreen)
                    ) { Icon(Icons.Rounded.ChevronLeft, null) }
                }
                if (canNext) {
                    IconButton(
                        onClick = onNext,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(6.dp)
                            .size(40.dp)
                            .zIndex(1f),
                        colors = IconButtonDefaults.iconButtonColors(contentColor = AirGreen)
                    ) { Icon(Icons.Rounded.ChevronRight, null) }
                }

                IconButton(
                    onClick = {
                        val resumed = holder.resumeToLiveCushion(cushionMs = 10_000L)
                        if (!resumed) holder.player.playWhenReady = false
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .align(Alignment.Center)
                        .zIndex(1f),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = AirGreen)
                ) {
                    if (holder.player.playWhenReady) Icon(Icons.Rounded.Pause, null)
                    else Icon(Icons.Rounded.PlayArrow, null)
                }

                val bottomOverlayHeight = ControlsRowHeight + ControlsOverlayVPad * 2
                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(bottomOverlayHeight)
                        .background(OverlayBg)
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                            .height(ControlsRowHeight)
                            .padding(horizontal = 8.dp)
                            .zIndex(2f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TinySeekBar(
                            fraction = if (duration > 0) position.toFloat() / duration else 0f,
                            onFractionChange = onSeekChange,
                            onChangeEnd = onSeekFinish,
                            modifier = Modifier.weight(1f),
                            height = ControlsRowHeight,
                            trackThickness = 3.dp,
                            activeColor = AirBlue,
                            inactiveColor = AirBlue.copy(alpha = 0.35f),
                            thumbRadius = 5.dp,
                            drawAtCenter = true
                        )

                        Spacer(Modifier.width(4.dp))

                        Row(
                            modifier = Modifier.wrapContentWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = formatMinusFromLive(duration, position),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.End
                            )
                            SpeedSegmented(
                                items = listOf(0.5f, 1f, 1.5f, 2f),
                                selected = speed,
                                onSelect = onSpeedSelected,
                                modifier = Modifier.wrapContentWidth()
                            )
                        }

                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick = toggleFullscreen,
                            modifier = Modifier.size(28.dp),
                            colors = IconButtonDefaults.iconButtonColors(contentColor = AirGreen)
                        ) {
                            if (isFullscreen) Icon(Icons.Rounded.FullscreenExit, null) else Icon(
                                Icons.Rounded.Fullscreen,
                                null
                            )
                        }
                    }
                }

                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = bottomOverlayHeight)
                ) {
                    SnackbarHost(hostState = snackbarHostState)
                }
            }
        }
    }
}