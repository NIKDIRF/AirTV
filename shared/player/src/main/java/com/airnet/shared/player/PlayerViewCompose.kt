package com.airnet.shared.player

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

@UnstableApi
@Composable
fun PlayerViewCompose(
    holder: ExoHolder,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PlayerView(ctx).apply {
                useController = false
                isClickable = false
                isLongClickable = false
                isFocusable = false
                isFocusableInTouchMode = false
                setOnTouchListener { _, _ -> false }
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                player = holder.player
                setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT)
            }
        },
        update = { view ->
            view.player = holder.player
            view.keepScreenOn = true
            view.isClickable = false
            view.isLongClickable = false
            view.isFocusable = false
            view.isFocusableInTouchMode = false
            view.setOnTouchListener { _, _ -> false }
            view.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT)
        }
    )
}