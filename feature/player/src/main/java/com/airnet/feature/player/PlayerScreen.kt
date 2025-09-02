@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.airnet.feature.player

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTonalElevationEnabled
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import com.airnet.domain.channels.Channel
import com.airnet.feature.player.theme.AirGreen
import com.airnet.feature.player.theme.Corner
import com.airnet.feature.player.theme.MAX_MINIS
import com.airnet.feature.player.theme.SheetBg
import com.airnet.feature.player.ui.ChannelPickerSheet
import com.airnet.feature.player.ui.MiniTile
import com.airnet.feature.player.ui.VideoBox
import com.airnet.feature.player.util.ApplyFullscreenAndOrientation
import com.airnet.shared.player.rememberExoHolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

@ExperimentalMaterial3Api
@UnstableApi
@Composable
fun PlayerScreen(
    vm: PlayerViewModel = hiltViewModel()
) {
    val st by vm.state.collectAsState()

    var panels by remember { mutableStateOf<List<Channel>>(emptyList()) }
    LaunchedEffect(st.current?.id) {
        val cur = st.current ?: return@LaunchedEffect
        panels = if (panels.isEmpty()) listOf(cur)
        else listOf(cur) + panels.drop(1).filter { it.id != cur.id }
    }
    val main = panels.firstOrNull()

    val mainHolder = rememberExoHolder()
    PauseWhenBackground(holder = mainHolder)

    var channelsCache by remember {
        mutableStateOf<List<Channel>?>(
            vm.allChannels().takeIf { it.isNotEmpty() })
    }

    LaunchedEffect(main?.url) {
        main?.url?.let { url ->
            mainHolder.playLiveAtOffset(
                url = url,
                offsetFromLiveMs = 10_000L,
                speed = st.speed,
                lockSpeed = true
            )
        }
    }
    LaunchedEffect(st.speed) { mainHolder.setSpeed(st.speed) }

    var position by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }
    var isSeekable by remember { mutableStateOf(false) }

    LaunchedEffect(mainHolder.player) {
        while (true) {
            val p = mainHolder.player
            position = p.currentPosition
            duration = p.duration
            isSeekable = p.isCurrentMediaItemSeekable && duration != C.TIME_UNSET && duration > 0
            delay(500)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var hasVideo by remember { mutableStateOf(true) }
    DisposableEffect(mainHolder.player) {
        val l = object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        error.cause?.message ?: error.message ?: "Playback error"
                    )
                }
            }

            override fun onTracksChanged(tracks: Tracks) {
                hasVideo = tracks.groups.any { it.type == C.TRACK_TYPE_VIDEO && it.isSelected }
            }
        }
        mainHolder.player.addListener(l)
        onDispose { mainHolder.player.removeListener(l) }
    }

    var controlsVisible by remember { mutableStateOf(true) }
    LaunchedEffect(controlsVisible) {
        if (controlsVisible) {
            delay(3000); controlsVisible = false
        }
    }

    var isFullscreen by remember { mutableStateOf(false) }
    ApplyFullscreenAndOrientation(isFullscreen)

    var showPicker by remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxSize()
            .background(SheetBg)
            .padding(horizontal = 6.dp)
    ) {
        Column(
            modifier = Modifier.align(if (showPicker) Alignment.TopCenter else Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val videoBoxMod =
                if (isFullscreen) Modifier.fillMaxSize()
                else Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(Corner))

            if (main != null) {
                VideoBox(
                    modifier = videoBoxMod,
                    holder = mainHolder,
                    hasVideo = hasVideo,
                    controlsVisible = controlsVisible,
                    setControlsVisible = { controlsVisible = it },
                    position = position,
                    duration = duration,
                    onSeekChange = { frac -> position = (frac * max(1L, duration)).toLong() },
                    onSeekFinish = { mainHolder.player.seekTo(position) },
                    speed = st.speed,
                    onSpeedSelected = vm::setSpeed,
                    canPrev = st.canPrev,
                    canNext = st.canNext,
                    onPrev = vm::prev,
                    onNext = vm::next,
                    isFullscreen = isFullscreen,
                    toggleFullscreen = {
                        isFullscreen = !isFullscreen
                        controlsVisible = true
                    },
                    snackbarHostState = snackbarHostState,
                    onClose = {
                        if (panels.size <= 1) mainHolder.stopAndClear()
                        panels = panels.drop(1)
                    },
                    title = main.name
                )
            } else {
                Box(
                    videoBoxMod.background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) { Text("Нет выбранного канала", color = Color.White) }
            }

            val minis = panels.drop(1).take(MAX_MINIS)
            if (!isFullscreen && minis.isNotEmpty()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    minis.forEachIndexed { idx, ch ->
                        MiniTile(
                            channel = ch,
                            onSwapToMain = {
                                val list = panels.toMutableList()
                                val absoluteIdx = idx + 1
                                if (absoluteIdx in list.indices && main != null) {
                                    val tmp = list[0]
                                    list[0] = list[absoluteIdx]
                                    list[absoluteIdx] = tmp
                                    panels = list
                                }
                            },
                            onClose = {
                                val absoluteIdx = idx + 1
                                panels = panels.filterIndexed { i, _ -> i != absoluteIdx }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    repeat(MAX_MINIS - minis.size) {
                        Box(
                            Modifier
                                .weight(1f)
                                .aspectRatio(16f / 9f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }
                }
            }

            val hasFreeSlot = panels.size < 1 + MAX_MINIS
            if (!isFullscreen && hasFreeSlot) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CompositionLocalProvider(LocalTonalElevationEnabled provides false) {
                        ExtendedFloatingActionButton(
                            onClick = { showPicker = true },
                            icon = { Icon(Icons.Rounded.Add, null) },
                            text = { Text("Добавить канал") },
                            containerColor = AirGreen,
                            contentColor = Color.Black,
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp,
                                focusedElevation = 0.dp,
                                hoveredElevation = 0.dp
                            )
                        )
                    }
                }
            }
        }

        if (showPicker) {
            ChannelPickerSheet(
                all = channelsCache,
                already = panels.map { it.id }.toSet(),
                onPick = { ch ->
                    if (panels.any { it.id == ch.id }) return@ChannelPickerSheet
                    if (panels.size >= 1 + MAX_MINIS) return@ChannelPickerSheet
                    panels = panels + ch
                    showPicker = false
                },
                onClose = { showPicker = false }
            )
        }
    }
}

@UnstableApi
@Composable
private fun PauseWhenBackground(holder: com.airnet.shared.player.ExoHolder) {
    val owner = LocalLifecycleOwner.current
    var shouldResume by remember { mutableStateOf(false) }

    DisposableEffect(owner, holder.player) {
        val obs = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    shouldResume = holder.player.playWhenReady
                    holder.player.playWhenReady = false
                }

                Lifecycle.Event.ON_START -> {
                    if (shouldResume) {
                        holder.resumeToLiveCushion(cushionMs = 10_000L)
                        shouldResume = false
                    }
                }

                else -> Unit
            }
        }
        owner.lifecycle.addObserver(obs)
        onDispose { owner.lifecycle.removeObserver(obs) }
    }
}