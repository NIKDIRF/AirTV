package com.airnet.shared.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberExoHolder(): ExoHolder {
    val ctx = LocalContext.current
    val holder = remember { ExoHolder(PlayerFactory.create(ctx)) }
    DisposableEffect(Unit) { onDispose { holder.release() } }
    return holder
}