package com.airnet.shared.player

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlin.math.max

class ExoHolder(val player: ExoPlayer) {

    fun play(url: String, speed: Float = 1f) {
        player.setMediaItem(PlayerFactory.itemHls(url))
        player.prepare()
        player.playWhenReady = true
        player.playbackParameters = PlaybackParameters(speed)
    }

    @UnstableApi
    fun playLiveAtOffset(
        url: String,
        offsetFromLiveMs: Long = 6_000L,
        speed: Float = 1f,
        lockSpeed: Boolean = true
    ) {
        val tgt = max(0L, offsetFromLiveMs)
        val liveCfg = MediaItem.LiveConfiguration.Builder()
            .setTargetOffsetMs(tgt)
            .setMinOffsetMs((tgt * 0.6f).toLong())
            .setMaxOffsetMs((tgt * 1.8f).toLong())
            .apply {
                if (lockSpeed) {
                    setMinPlaybackSpeed(1f)
                    setMaxPlaybackSpeed(1f)
                }
            }
            .build()

        val item = MediaItem.Builder()
            .setUri(url)
            .setLiveConfiguration(liveCfg)
            .build()

        player.setMediaItem(item)
        player.prepare()
        player.playWhenReady = true
        player.playbackParameters = PlaybackParameters(speed)
    }

    fun setSpeed(speed: Float) {
        player.playbackParameters = PlaybackParameters(speed)
    }

    @UnstableApi
    fun resumeToLiveCushion(cushionMs: Long = 10_000L): Boolean {
        if (player.playWhenReady) return false

        val isLive = player.isCurrentMediaItemLive
        if (isLive) {
            val d = player.duration
            if (d != C.TIME_UNSET && d > 0L) {
                val target = (d - cushionMs).coerceAtLeast(0L)
                player.seekTo(target)
            } else {
                player.seekToDefaultPosition()
            }
        }

        player.playWhenReady = true
        return true
    }

    fun stopAndClear() {
        player.stop()
        player.clearMediaItems()
    }

    fun release() {
        player.release()
    }
}
