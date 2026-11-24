/**
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 */

package com.moralist.shared.db.entities

import androidx.room3.Embedded
import androidx.room3.Relation
import com.moralist.shared.model.SongItem

data class PlaylistSong(
    @Embedded val map: PlaylistSongMap,
    @Relation(
        parentColumns = ["songId"],
        entityColumns = ["id"],
        entity = SongEntity::class,
    )
    val song: Song,
)

fun PlaylistSong.toSongItem(): SongItem =
    song.toSongItem().copy(
        playlistSetVideoId = map.setVideoId,
        playlistLocalMappingId = map.id,
        playlistAddedOrder = map.id,
    )
