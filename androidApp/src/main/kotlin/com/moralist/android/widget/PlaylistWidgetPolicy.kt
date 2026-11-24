/**
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 */

package com.moralist.android.widget

internal const val PLAYLIST_WIDGET_COMPACT_MAX_WIDTH_DP = 340

internal fun playlistWidgetQuickPickCapacity(
    minWidth: Int,
    minHeight: Int,
): Int =
    when {
        minWidth < PLAYLIST_WIDGET_COMPACT_MAX_WIDTH_DP -> 1
        minHeight < 330 -> 4
        minWidth >= 360 -> 8
        else -> 4
    }

internal fun playlistWidgetDisplayCount(
    available: Int,
    capacity: Int,
): Int {
    val capped = minOf(available, capacity, 8)
    return when {
        capped >= 8 -> 8
        capped >= 4 -> 4
        else -> capped
    }
}

internal fun needsTopWidgetSong(
    precedingPicks: Int,
    capacity: Int,
): Boolean = precedingPicks < playlistWidgetDisplayCount(precedingPicks + 1, capacity)
