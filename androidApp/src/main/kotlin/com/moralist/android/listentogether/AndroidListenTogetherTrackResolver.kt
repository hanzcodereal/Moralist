package com.moralist.android.listentogether

import com.moralist.shared.listentogether.ListenTogetherTrackResolver
import com.moralist.shared.listentogether.toSongItem
import com.moralist.shared.model.SongItem

internal fun androidListenTogetherTrackResolver(
    metadata: suspend (String) -> SongItem?,
    fallbackArtist: suspend () -> String,
): ListenTogetherTrackResolver =
    ListenTogetherTrackResolver { track ->
        metadata(track.id) ?: track.toSongItem(
            fallbackArtist = fallbackArtist(),
            omitNonPositiveDuration = true,
        )
    }
