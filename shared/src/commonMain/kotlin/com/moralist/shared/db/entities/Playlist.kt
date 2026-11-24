/**
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 */

package com.moralist.shared.db.entities

import androidx.room3.Embedded

data class Playlist(
    @Embedded
    val playlist: PlaylistEntity,
    val songCount: Int,
) : LocalItem() {
    override val id: String
        get() = playlist.id
    override val title: String
        get() = playlist.name
    override val thumbnailUrl: String?
        get() = playlist.customThumbnailUrl ?: playlist.thumbnailUrl
}
