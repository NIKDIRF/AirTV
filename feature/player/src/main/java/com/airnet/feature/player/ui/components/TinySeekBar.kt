package com.airnet.feature.player.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun TinySeekBar(
    fraction: Float,
    onFractionChange: (Float) -> Unit,
    onChangeEnd: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp,
    trackThickness: Dp,
    activeColor: Color,
    inactiveColor: Color,
    thumbRadius: Dp,
    drawAtCenter: Boolean = false
) {
    val density = LocalDensity.current
    var wPx by remember { mutableStateOf(1f) }
    val trackH = with(density) { trackThickness.toPx() }
    val thumbR = with(density) { thumbRadius.toPx() }
    var current by remember(fraction) { mutableStateOf(fraction.coerceIn(0f, 1f)) }

    fun setFromX(x: Float) {
        current = (x / wPx).coerceIn(0f, 1f); onFractionChange(current)
    }

    Canvas(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { pos -> setFromX(pos.x) },
                    onDrag = { change, _ -> setFromX(change.position.x) },
                    onDragEnd = { onChangeEnd() }
                )
            }
            .onSizeChanged { wPx = it.width.toFloat() }
    ) {
        val centerY = if (drawAtCenter) size.height / 2f else size.height - trackH
        val cx = current * size.width

        drawLine(
            inactiveColor,
            Offset(0f, centerY),
            Offset(size.width, centerY),
            strokeWidth = trackH
        )
        drawLine(activeColor, Offset(0f, centerY), Offset(cx, centerY), strokeWidth = trackH)
        drawCircle(activeColor, radius = thumbR, center = Offset(cx, centerY))
    }
}
