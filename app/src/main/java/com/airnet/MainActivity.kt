package com.airnet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.airnet.feature.channels.ChannelsScreen
import com.airnet.ui.systembars.MatchSystemBars
import com.airnet.ui.theme.AirNetTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val PLAYLIST_URL = "https://iptv-org.github.io/iptv/countries/ru.m3u"

    @UnstableApi
    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AirNetTheme {
                MatchSystemBars(Color(0xFF0C1218))
                AppNav(playlistUrl = PLAYLIST_URL)
            }
        }
    }
}

@UnstableApi
@ExperimentalMaterial3Api
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppNav(playlistUrl: String) {
    val nav = rememberNavController()
    NavHost(nav, startDestination = "channels") {
        composable("channels") {
            ChannelsScreen(
                playlistUrl = playlistUrl,
                onOpenPlayer = { nav.navigate("player") }
            )
        }
        composable("player") {
            com.airnet.feature.player.PlayerScreen()
        }
    }
}
