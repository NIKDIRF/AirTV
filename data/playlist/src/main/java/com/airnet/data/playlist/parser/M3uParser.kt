package com.airnet.data.playlist.parser

import com.airnet.domain.channels.Channel
import java.security.MessageDigest

object M3uParser {
    private fun md5(input: String): String =
        MessageDigest.getInstance("MD5").digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }

    fun parse(text: String): List<Channel> {
        val lines = text.lines()
        val result = mutableListOf<Channel>()
        var attrs: Map<String, String?> = emptyMap()
        var title: String? = null
        val attrRegex = """(\w+?)="(.*?)"""".toRegex()

        var i = 0
        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("#EXTINF", true)) {
                val after = line.substringAfter(":").trim()
                val parts = after.split(",")
                val left = parts.dropLast(1).joinToString(",")
                title = parts.lastOrNull()?.trim()
                val map = mutableMapOf<String, String?>()
                attrRegex.findAll(left).forEach { m -> map[m.groupValues[1]] = m.groupValues[2] }
                attrs = map
            } else if (line.isNotEmpty() && !line.startsWith("#")) {
                val url = line
                val name = attrs["tvg-name"] ?: title ?: url
                val group = attrs["group-title"]
                val logo = attrs["tvg-logo"]
                result += Channel(id = md5(url), name = name, url = url, group = group, logo = logo)
                attrs = emptyMap(); title = null
            }
            i++
        }
        return result
    }
}