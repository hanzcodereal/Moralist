package com.moralist.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moralist.shared.model.HomeSection
import com.moralist.shared.model.SongItem
import com.moralist.shared.utils.withArtworkCacheRevision
import com.moralist.shared.viewmodels.BrowseViewModel

class HomeSectionViewModel(
    section: HomeSection,
) : ViewModel() {
    val shared = BrowseViewModel(section.browseId, section.params, section.title, viewModelScope, initialSection = section)
    private var artworkCacheRevision = System.nanoTime()

    fun updateSongMetadata(song: SongItem) {
        shared.updateSongMetadata(song.copy(thumbnail = song.thumbnail.withArtworkCacheRevision(++artworkCacheRevision)))
    }
}
