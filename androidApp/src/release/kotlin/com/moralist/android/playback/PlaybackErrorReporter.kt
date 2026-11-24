package com.moralist.android.playback

import com.moralist.shared.model.SongItem
import com.moralist.shared.playback.PlaybackErrorContext
import com.moralist.shared.youtube.playback.PlaybackResource

internal object PlaybackErrorReporter {
    fun setActivePlayback(
        song: SongItem,
        resource: PlaybackResource?,
    ) = Unit

    fun clearActivePlayback() = Unit

    fun report(
        error: Throwable,
        context: PlaybackErrorContext,
    ) = Unit
}
