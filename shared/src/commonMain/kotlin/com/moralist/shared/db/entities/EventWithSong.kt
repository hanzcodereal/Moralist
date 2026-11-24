/**
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 */

package com.moralist.shared.db.entities

import androidx.room3.Embedded
import androidx.room3.Relation

data class EventWithSong(
    @Embedded
    val event: Event,
    @Relation(
        entity = SongEntity::class,
        parentColumns = ["songId"],
        entityColumns = ["id"],
    )
    val song: Song,
    @Relation(parentColumns = ["id"], entityColumns = ["eventId"])
    val snapshot: HistorySnapshot? = null,
)
