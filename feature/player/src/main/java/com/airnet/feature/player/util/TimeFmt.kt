package com.airnet.feature.player.util

import androidx.media3.common.C

fun formatMinusFromLive(durationMs: Long, positionMs: Long): String {
    if (durationMs <= 0L || durationMs == C.TIME_UNSET) return "-–:–"
    val behindSec = ((durationMs - positionMs).coerceAtLeast(0L)) / 1000
    val minutes = behindSec / 60
    val seconds = behindSec % 60
    return "-%02d:%02d".format(minutes, seconds)
}
