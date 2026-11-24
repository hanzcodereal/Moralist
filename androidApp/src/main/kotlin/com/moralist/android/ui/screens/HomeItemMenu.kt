/**
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 */

package com.moralist.android.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import com.moralist.android.playback.LocalPlayerConnection
import com.moralist.android.ui.menu.AlbumMenu
import com.moralist.android.ui.menu.ArtistMenu
import com.moralist.android.ui.menu.CollectionExportDialog
import com.moralist.android.ui.menu.PlaylistMenu
import com.moralist.android.ui.menu.PodcastMenu
import com.moralist.android.ui.menu.SongDetailsSheet
import com.moralist.android.ui.menu.SongMenu
import com.moralist.android.ui.utils.playSong
import com.moralist.android.utils.startActivitySafely
import com.moralist.shared.api.musicvideo.MusicVideoResolver
import com.moralist.shared.db.MusicDatabase
import com.moralist.shared.generated.resources.Res
import com.moralist.shared.generated.resources.home_load_item_failed
import com.moralist.shared.generated.resources.metadata_refreshed
import com.moralist.shared.generated.resources.no_newer_metadata
import com.moralist.shared.generated.resources.no_playable_items
import com.moralist.shared.generated.resources.share_album
import com.moralist.shared.generated.resources.share_artist
import com.moralist.shared.generated.resources.share_playlist
import com.moralist.shared.generated.resources.share_podcast
import com.moralist.shared.generated.resources.share_song
import com.moralist.shared.model.AlbumItem
import com.moralist.shared.model.ArtistItem
import com.moralist.shared.model.BuiltinPlaylist
import com.moralist.shared.model.EpisodeItem
import com.moralist.shared.model.PlaylistItem
import com.moralist.shared.model.PodcastItem
import com.moralist.shared.model.SongItem
import com.moralist.shared.model.YTItem
import com.moralist.shared.model.refetchSongMetadata
import com.moralist.shared.model.toSongItem
import com.moralist.shared.state.SongLikeState
import com.moralist.shared.ui.components.PopupContent
import com.moralist.shared.ui.menu.BuiltinPlaylistMenu
import com.moralist.shared.ui.menu.loadMediaItemSongs
import com.moralist.shared.utils.ShareUrls
import com.moralist.shared.utils.evictArtworkCache
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
internal fun HomeItemMenu(
    selectedItem: YTItem?,
    onDismiss: () -> Unit,
    onNavigateToAlbum: (String) -> Unit,
    onNavigateToArtist: (String) -> Unit,
    onNavigateToPodcast: (String) -> Unit,
    onSongMetadataUpdated: (SongItem) -> Unit,
    active: Boolean = true,
) {
    val shareSongText = stringResource(Res.string.share_song)
    val sharePlaylistText = stringResource(Res.string.share_playlist)
    val shareAlbumText = stringResource(Res.string.share_album)
    val shareArtistText = stringResource(Res.string.share_artist)
    val sharePodcastText = stringResource(Res.string.share_podcast)
    val context = LocalContext.current
    val playerConnection = LocalPlayerConnection.current
    val database: MusicDatabase = koinInject()
    val musicVideoResolver: MusicVideoResolver = koinInject()
    val imageLoader: ImageLoader = koinInject()
    val scope = rememberCoroutineScope()
    var detailsSong by remember { mutableStateOf<SongItem?>(null) }
    var exportItem by remember { mutableStateOf<YTItem?>(null) }

    fun share(
        url: String,
        chooserTitle: String,
    ) {
        val intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, url)
            }
        context.startActivitySafely(
            Intent.createChooser(intent, chooserTitle).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
        )
    }

    fun withCollectionSongs(
        item: YTItem,
        action: (List<SongItem>) -> Unit,
    ) {
        scope.launch {
            val songs =
                runCatching { loadMediaItemSongs(item) }.getOrElse {
                    Toast
                        .makeText(
                            context,
                            getString(Res.string.home_load_item_failed, item.title),
                            Toast.LENGTH_SHORT,
                        ).show()
                    return@launch
                }
            if (songs.isEmpty()) {
                Toast.makeText(context, getString(Res.string.no_playable_items), Toast.LENGTH_SHORT).show()
            } else {
                action(songs)
            }
        }
    }

    fun queueCollection(
        item: YTItem,
        playNext: Boolean,
    ) {
        withCollectionSongs(item) { songs ->
            playerConnection?.addToQueue(songs, playNext)
        }
    }

    PopupContent(selectedItem?.takeIf { active }) { item ->
        when (item) {
            is SongItem, is EpisodeItem -> {
                val song = item as? SongItem ?: (item as EpisodeItem).toSongItem()
                SongMenu(
                    song = song,
                    isLiked = SongLikeState.isLiked(song.id),
                    onDismiss = { onDismiss() },
                    onPlay = { playSong(song, playerConnection, scope, startVideo = false) },
                    onStartRadio = { playerConnection?.startRadio(song) },
                    onPlayNext = { playerConnection?.addToQueue(song, playNext = true) },
                    onAddToQueue = { playerConnection?.addToQueue(song) },
                    onLike = { SongLikeState.toggleLike(song) },
                    onShare = {
                        share(
                            ShareUrls.song(song.id),
                            shareSongText,
                        )
                    },
                    onGoToAlbum =
                        if (item is EpisodeItem) {
                            item.podcastId?.let { podcastId -> { onNavigateToPodcast(podcastId) } }
                        } else {
                            song.album?.id?.let { albumId -> { onNavigateToAlbum(albumId) } }
                        },
                    onGoToArtist = onNavigateToArtist,
                    onShowInfo = { detailsSong = song },
                    onRefetch = {
                        scope.launch {
                            val refreshed = refetchSongMetadata(database, musicVideoResolver, song)
                            if (refreshed != null) {
                                imageLoader.evictArtworkCache(song.thumbnail)
                                imageLoader.evictArtworkCache(refreshed.thumbnail)
                                onSongMetadataUpdated(refreshed)
                            }
                            Toast
                                .makeText(
                                    context,
                                    getString(if (refreshed != null) Res.string.metadata_refreshed else Res.string.no_newer_metadata),
                                    Toast.LENGTH_SHORT,
                                ).show()
                        }
                    },
                )
            }

            is PlaylistItem -> {
                if (item.id.startsWith("auto:") && BuiltinPlaylist.fromId(item.id) != null) {
                    BuiltinPlaylistMenu(item, database) { onDismiss() }
                } else {
                    PlaylistMenu(
                        playlist = item,
                        onDismiss = { onDismiss() },
                        onPlay = { playerConnection?.playPlaylist(item.id, item.title) },
                        onShuffle = {
                            playerConnection?.playPlaylist(item.id, item.title, shuffle = true)
                        },
                        onShare = {
                            share(
                                ShareUrls.playlist(item.id),
                                sharePlaylistText,
                            )
                        },
                        onPlayNext = { queueCollection(item, playNext = true) },
                        onAddToQueue = { queueCollection(item, playNext = false) },
                        onExport = { exportItem = item },
                    )
                }
            }

            is AlbumItem -> {
                AlbumMenu(
                    album = item,
                    onDismiss = { onDismiss() },
                    onPlay = { playerConnection?.playAlbum(item.id, item.title) },
                    onShuffle = {
                        playerConnection?.playAlbum(item.id, item.title, shuffle = true)
                    },
                    onShare = {
                        share(
                            ShareUrls.album(item.id),
                            shareAlbumText,
                        )
                    },
                    onPlayNext = { queueCollection(item, playNext = true) },
                    onAddToQueue = { queueCollection(item, playNext = false) },
                    onGoToArtist = onNavigateToArtist,
                    onExport = { exportItem = item },
                )
            }

            is ArtistItem -> {
                ArtistMenu(
                    artist = item,
                    onDismiss = { onDismiss() },
                    onShuffle = {
                        withCollectionSongs(item) {
                            playerConnection?.shuffleQueue(it, queueTitle = item.title)
                        }
                    },
                    onShare = {
                        share(
                            ShareUrls.artist(item.id),
                            shareArtistText,
                        )
                    },
                )
            }

            is PodcastItem -> {
                PodcastMenu(
                    podcast = item,
                    onDismiss = { onDismiss() },
                    onPlay = { withCollectionSongs(item) { playerConnection?.playQueue(it, queueTitle = item.title) } },
                    onShuffle = {
                        withCollectionSongs(item) {
                            playerConnection?.shuffleQueue(it, queueTitle = item.title)
                        }
                    },
                    onShare = {
                        share(
                            ShareUrls.podcast(item.id),
                            sharePodcastText,
                        )
                    },
                    onPlayNext = { queueCollection(item, playNext = true) },
                    onAddToQueue = { queueCollection(item, playNext = false) },
                )
            }

            else -> {
                onDismiss()
            }
        }
    }

    detailsSong?.takeIf { active }?.let { song ->
        SongDetailsSheet(song = song, onDismiss = { detailsSong = null })
    }

    exportItem?.takeIf { active }?.let { item ->
        CollectionExportDialog(
            title = item.title,
            onDismiss = { exportItem = null },
            loadSongs = { loadMediaItemSongs(item) },
        )
    }
}
