package com.moralist.android.playback

import com.metrolist.innertubex.extraction.AudioQuality
import com.moralist.shared.settings.Settings

internal fun playbackAudioQuality(): AudioQuality =
    try {
        AudioQuality.valueOf(Settings.shared.audioQuality)
    } catch (_: IllegalArgumentException) {
        AudioQuality.AUTO
    }
