package com.airnet.feature.channels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airnet.feature.channels.theme.AirBlue
import com.airnet.feature.channels.theme.SheetBg
import com.airnet.feature.channels.ui.ChannelRowCard
import com.airnet.feature.channels.ui.ChannelsSkeletonList


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelsScreen(
    playlistUrl: String,
    onOpenPlayer: () -> Unit,
    vm: ChannelsViewModel = hiltViewModel()
) {
    val st by vm.state.collectAsState()

    LaunchedEffect(playlistUrl) { vm.load(playlistUrl) }

    Column(
        Modifier
            .fillMaxSize()
            .background(SheetBg)
            .padding(12.dp)
    ) {
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = st.query,
            onValueChange = vm::setQuery,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Поиск по имени") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AirBlue,
                unfocusedBorderColor = AirBlue.copy(alpha = 0.6f),
                focusedLabelColor = AirBlue,
                cursorColor = AirBlue,
                focusedContainerColor = Color(0xFF111A22),
                unfocusedContainerColor = Color(0xFF0F1A24),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(Modifier.height(10.dp))

        if (st.isLoading) {
            ChannelsSkeletonList()
        } else {
            if (st.error != null) {
                Text(
                    "Error: ${st.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(st.filtered) { ch ->
                    ChannelRowCard(
                        channel = ch,
                        onClick = {
                            vm.preparePlayback(ch)
                            onOpenPlayer()
                        }
                    )
                }
            }
        }
    }
}