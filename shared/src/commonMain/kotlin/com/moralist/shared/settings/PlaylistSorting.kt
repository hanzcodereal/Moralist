package com.moralist.shared.settings

import com.moralist.shared.model.CollectionSort
import com.moralist.shared.model.PlaylistSongSort
import com.moralist.shared.model.SortDirection
import kotlinx.serialization.Serializable

@Serializable
data class SavedPlaylistSongSorting(
    val sort: PlaylistSongSort,
    val direction: SortDirection,
)

@Serializable
data class SavedPlaylistCollectionSorting(
    val sort: CollectionSort,
    val direction: SortDirection,
)
