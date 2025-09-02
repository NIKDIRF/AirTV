package com.airnet.data.playlist.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface PlaylistApi {
    @GET
    suspend fun getPlaylist(@Url url: String): Response<String>
}