package com.airnet.shared.queue

import com.airnet.domain.channels.Channel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackQueue @Inject constructor() {

    private var list: List<Channel> = emptyList()
    private var index: Int = 0

    @Synchronized
    fun set(list: List<Channel>, current: Channel) {
        this.list = list
        this.index = list.indexOfFirst { it.id == current.id }
            .takeIf { it >= 0 } ?: 0
    }

    @Synchronized
    fun current(): Channel? = list.getOrNull(index)

    @Synchronized
    fun all(): List<Channel> = list

    @Synchronized
    fun hasPrev(): Boolean = index > 0

    @Synchronized
    fun hasNext(): Boolean = index < list.lastIndex

    @Synchronized
    fun prev(): Channel? {
        if (hasPrev()) index--
        return current()
    }

    @Synchronized
    fun next(): Channel? {
        if (hasNext()) index++
        return current()
    }

    @Synchronized
    fun clear() {
        list = emptyList()
        index = 0
    }
}