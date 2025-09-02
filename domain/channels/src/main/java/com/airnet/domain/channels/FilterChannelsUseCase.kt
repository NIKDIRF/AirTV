package com.airnet.domain.channels

import javax.inject.Inject

class FilterChannelsUseCase @Inject constructor() {
    operator fun invoke(list: List<Channel>, query: String): List<Channel> {
        if (query.isBlank()) return list
        val q = query.trim().lowercase()
        return list.filter {
            it.name.lowercase().contains(q) || (it.group?.lowercase()?.contains(q) == true)
        }
    }
}