package com.airnet.feature.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airnet.data.playlist.PlaylistRepository
import com.airnet.domain.channels.Channel
import com.airnet.domain.channels.FilterChannelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChannelsState(
    val isLoading: Boolean = false,
    val query: String = "",
    val channels: List<Channel> = emptyList(),
    val filtered: List<Channel> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ChannelsViewModel @Inject constructor(
    private val repo: PlaylistRepository,
    private val filter: FilterChannelsUseCase,
    private val queue: com.airnet.shared.queue.PlaybackQueue
) : ViewModel() {

    private val _state = MutableStateFlow(ChannelsState())
    val state = _state.asStateFlow()

    fun preparePlayback(selected: Channel) {
        val list = _state.value.filtered.ifEmpty { _state.value.channels }
        if (list.isNotEmpty()) queue.set(list, selected)
    }

    fun load(url: String) {
        if (_state.value.isLoading || _state.value.channels.isNotEmpty()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repo.loadChannels(url).onSuccess { list ->
                _state.update { it.copy(isLoading = false, channels = list, filtered = list) }
                val withLogos = list.count { !it.logo.isNullOrBlank() }
                android.util.Log.d("Channels", "logos: $withLogos / ${list.size}")
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    fun setQuery(q: String) {
        _state.update { st -> st.copy(query = q, filtered = filter(st.channels, q)) }
    }
}