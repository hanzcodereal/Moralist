package com.moralist.shared.ui.menu

import com.moralist.shared.api.playlistSongs
import com.moralist.shared.model.AlbumItem
import com.moralist.shared.model.ArtistItem
import com.moralist.shared.model.EpisodeItem
import com.moralist.shared.model.PlaylistItem
import com.moralist.shared.model.PodcastItem
import com.moralist.shared.model.SongItem
import com.moralist.shared.model.YTItem
import com.moralist.shared.model.toSongItem
import com.moralist.shared.state.GlobalYouTubeRepository

suspend fun loadMediaItemSongs(item: YTItem): List<SongItem> {
    val repository = GlobalYouTubeRepository.instance
    return when (item) {
        is SongItem -> {
            listOf(item)
        }

        is EpisodeItem -> {
            listOf(item.toSongItem())
        }

        is AlbumItem -> {
            repository.getAlbum(item.id)?.songs.orEmpty()
        }

        is ArtistItem -> {
            repository.getArtistSongs(item.id)
        }

        is PlaylistItem -> {
            repository.getPlaylist(item.id).playlistSongs()
        }

        is PodcastItem -> {
            // Every page, so playback covers the whole podcast and can start from its first episode.
            buildList {
                repository.getPodcastPages(item.id).collect { page -> page.episodes.mapTo(this) { it.toSongItem(page.podcast.title) } }
            }
        }

        else -> {
            emptyList()
        }
    }
}
