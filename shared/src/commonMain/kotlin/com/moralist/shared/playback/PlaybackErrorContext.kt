package com.moralist.shared.playback

import com.moralist.shared.model.SongItem
import com.moralist.shared.youtube.playback.PlaybackResource

data class PlaybackErrorContext(
    val stage: String,
    val videoId: String? = null,
    val clientName: String? = null,
    val profileId: String? = null,
    val sourceName: String? = null,
    val code: String? = null,
    val recovered: Boolean? = null,
    val retryCount: Int? = null,
    val song: SongItem? = null,
    val resource: PlaybackResource? = null,
)
