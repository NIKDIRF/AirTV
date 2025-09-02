package com.airnet.data.playlist

import com.airnet.data.playlist.parser.M3uParser
import com.airnet.data.playlist.remote.PlaylistApi
import com.airnet.domain.channels.Channel
import javax.inject.Inject

class PlaylistRepository @Inject constructor(
    private val api: PlaylistApi
) {
    suspend fun loadChannels(url: String): Result<List<Channel>> = runCatching {
        val resp = api.getPlaylist(url)
        require(resp.isSuccessful && resp.body() != null) { "HTTP ${resp.code()}" }
        M3uParser.parse(resp.body()!!)
    }
}