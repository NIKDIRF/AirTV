package com.airnet.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airnet.data.prefs.PlayerPrefs
import com.airnet.domain.channels.Channel
import com.airnet.shared.queue.PlaybackQueue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val current: Channel? = null,
    val canPrev: Boolean = false,
    val canNext: Boolean = false,
    val speed: Float = 1f,
    val zoom: Boolean = false
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val queue: PlaybackQueue,
    private val prefs: PlayerPrefs
) : ViewModel() {

    private val _state = MutableStateFlow(PlayerUiState())
    val state = _state.asStateFlow()

    init {
        refreshFromQueue()
        viewModelScope.launch {
            prefs.speedFlow.collect { v -> _state.update { it.copy(speed = v) } }
        }
        viewModelScope.launch {
            prefs.zoomFlow.collect { z -> _state.update { it.copy(zoom = z) } }
        }
    }

    private fun refreshFromQueue() {
        val cur = queue.current()
        _state.update {
            it.copy(
                current = cur,
                canPrev = queue.hasPrev(),
                canNext = queue.hasNext()
            )
        }
        cur?.id?.let { id ->
            viewModelScope.launch { prefs.setLastId(id) }
        }
    }

    fun prev() {
        queue.prev(); refreshFromQueue()
    }

    fun next() {
        queue.next(); refreshFromQueue()
    }

    fun setSpeed(v: Float) {
        _state.update { it.copy(speed = v) }
        viewModelScope.launch { prefs.setSpeed(v) }
    }

    fun allChannels(): List<Channel> = queue.all()
}