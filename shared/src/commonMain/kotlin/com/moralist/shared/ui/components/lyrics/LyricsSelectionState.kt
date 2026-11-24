/*
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 */

package com.moralist.shared.ui.components.lyrics

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class LyricsSelectionState {
    var selectedIndices by mutableStateOf(emptySet<Int>())
        private set
    var limitReached by mutableStateOf(false)
        private set
    val isActive: Boolean get() = selectedIndices.isNotEmpty()

    fun select(index: Int) {
        if (index !in selectedIndices) toggle(index)
    }

    fun toggle(index: Int) {
        val next = toggleLyricsShareLine(selectedIndices, index)
        limitReached = next == selectedIndices
        selectedIndices = next
    }

    fun clear() {
        selectedIndices = emptySet()
        limitReached = false
    }
}
