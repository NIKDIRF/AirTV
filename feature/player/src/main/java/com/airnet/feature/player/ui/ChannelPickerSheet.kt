@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.airnet.feature.player.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airnet.domain.channels.Channel
import com.airnet.feature.player.theme.AirBlue
import com.airnet.feature.player.theme.AirBlueLight
import com.airnet.feature.player.theme.AirGreen
import com.airnet.feature.player.theme.CardBg
import com.airnet.feature.player.theme.CardBgAdded
import com.airnet.feature.player.theme.SheetBg

@Composable
fun ChannelPickerSheet(
    all: List<Channel>?,
    already: Set<String>,
    onPick: (Channel) -> Unit,
    onClose: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onClose,
        containerColor = SheetBg,
        dragHandle = { BottomSheetDefaults.DragHandle(color = AirBlueLight) }
    ) {
        if (all == null) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    "Выберите канал",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                repeat(8) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x22FFFFFF))
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
            return@ModalBottomSheet
        }

        val listState = rememberLazyListState()
        val filtered = remember(query, all) {
            val q = query.trim()
            if (q.isEmpty()) all else {
                val iq = q.lowercase()
                all.filter { c ->
                    c.name.lowercase().contains(iq) || (c.group?.lowercase()?.contains(iq) == true)
                }
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
        ) {
            stickyHeader {
                Surface(color = SheetBg) {
                    Column {
                        Text(
                            "Выберите канал",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("Поиск по имени/группе") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AirBlue,
                                unfocusedBorderColor = AirBlue.copy(alpha = 0.6f),
                                focusedLabelColor = AirBlue,
                                cursorColor = AirBlue,
                                focusedContainerColor = Color(0xFF111A22),
                                unfocusedContainerColor = Color(0xFF0F1A24),
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }

            items(items = filtered, key = { it.id }) { ch ->
                val added = ch.id in already
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (added) CardBgAdded else CardBg)
                        .clickable(enabled = !added) { onPick(ch) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                ) {
                    Text(
                        ch.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    if (added) Text(
                        "добавлен",
                        color = AirGreen,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Spacer(Modifier.height(6.dp))
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}
