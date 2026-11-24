/**
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 */

package com.moralist.shared.db

import androidx.room3.ColumnTypeConverter
import com.moralist.shared.model.PlaylistItem
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlin.time.Instant

class Converters {
    private val playlistJson = Json { ignoreUnknownKeys = true }

    @ColumnTypeConverter
    fun playlistHeaderToJson(header: PlaylistItem?): String? = header?.let { playlistJson.encodeToString(it) }

    @ColumnTypeConverter
    fun playlistHeaderFromJson(value: String?): PlaylistItem? = value?.let { playlistJson.decodeFromString<PlaylistItem>(it) }

    @ColumnTypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? =
        value?.let {
            Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC)
        }

    @ColumnTypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? = date?.toInstant(TimeZone.UTC)?.toEpochMilliseconds()
}
