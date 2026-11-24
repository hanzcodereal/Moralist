package com.moralist.android.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.moralist.android.LocalMiniPlayerBottomInset
import com.moralist.android.playback.LocalPlayerConnection
import com.moralist.android.ui.menu.SongMenu
import com.moralist.android.viewmodels.HistoryError
import com.moralist.android.viewmodels.HistoryViewModel
import com.moralist.shared.db.MusicDatabase
import com.moralist.shared.generated.resources.Res
import com.moralist.shared.generated.resources.delete
import com.moralist.shared.generated.resources.delete_failed
import com.moralist.shared.generated.resources.edit
import com.moralist.shared.generated.resources.error_unknown
import com.moralist.shared.model.BuiltinPlaylist
import com.moralist.shared.model.HistoryEntryUi
import com.moralist.shared.model.toSongItem
import com.moralist.shared.settings.Settings
import com.moralist.shared.state.SongLikeState
import com.moralist.shared.ui.components.PlayerMenuAction
import com.moralist.shared.ui.components.PopupContent
import com.moralist.shared.ui.icons.MoralistIcons
import com.moralist.shared.ui.menu.BuiltinPlaylistMenuButton
import com.moralist.shared.ui.screens.DeleteHistoryDialog
import com.moralist.shared.ui.screens.EditHistoryDialog
import com.moralist.shared.ui.screens.HistoryContent
import com.moralist.shared.utils.formatHistoryMonth
import org.jetbrains.compose.resources.stringResource
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: HistoryViewModel = koinViewModel(),
) {
    LaunchedEffect(
        Settings.shared.innerTubeCookie,
        Settings.shared.dataSyncId,
        Settings.shared.innerTubeAuthUser,
        Settings.shared.ytmSync,
        Settings.shared.pauseListenHistory,
    ) { viewModel.loadHistory() }
    val uiState by viewModel.uiState.collectAsState()
    val playerConnection = LocalPlayerConnection.current ?: return
    var menuEntry by remember { mutableStateOf<HistoryEntryUi?>(null) }
    var editEntry by remember { mutableStateOf<HistoryEntryUi?>(null) }
    var deleteEntry by remember { mutableStateOf<HistoryEntryUi?>(null) }
    val entries = uiState.uiEntries
    val displayedEntries = uiState.uiDisplayedEntries

    HistoryContent(
        headerActions = { BuiltinPlaylistMenuButton(BuiltinPlaylist.HISTORY, koinInject<MusicDatabase>()) },
        entries = entries,
        onLoadMore = if (uiState.hasMore) viewModel::loadMore else null,
        displayedEntries = displayedEntries,
        query = uiState.query,
        sortMode = uiState.sortMode,
        sortDirection = uiState.sortDirection,
        onSortDirectionChange = viewModel::setSortDirection,
        selectedIds = uiState.selectedIds.mapTo(mutableSetOf(), Long::toString),
        isLoading = uiState.isLoading,
        isDeleting = uiState.isDeleting,
        error =
            when (uiState.error) {
                HistoryError.DELETE -> stringResource(Res.string.delete_failed)
                HistoryError.LOAD -> stringResource(Res.string.error_unknown)
                null -> null
            },
        canDeleteRemote = uiState.canDeleteRemote,
        onQueryChange = viewModel::setQuery,
        onSortChange = viewModel::setSortMode,
        onRefresh = viewModel::retry,
        onPlay = { playerConnection.playSong(it.toSongItem(), url = "") },
        onSongMenu = { menuEntry = it },
        onToggleSelection = { viewModel.toggleSelection(it.toLong()) },
        onSelectAll = { viewModel.selectAll(it.mapTo(mutableSetOf(), String::toLong)) },
        onUpdate = { entry, title, artists, album ->
            viewModel.updateEntry(entry.id.toLong(), title, artists, album)
        },
        onDelete = { entry, alsoRemote ->
            viewModel.deleteEntry(entry.id.toLong(), deleteRemote = alsoRemote)
        },
        onDeleteSelected = { ids, alsoRemote ->
            viewModel.deleteEntries(ids.mapTo(mutableSetOf(), String::toLong), deleteRemote = alsoRemote)
        },
        formatMonth = { formatHistoryMonth(it) },
        onNavigateBack = onNavigateBack,
        showInlineEntryMenu = false,
        contentPadding = PaddingValues(bottom = LocalMiniPlayerBottomInset.current),
    )
    menuEntry?.let { entry ->
        val song = entry.toSongItem()
        SongMenu(
            song = song,
            isLiked = SongLikeState.isLiked(song.id),
            onDismiss = { menuEntry = null },
            onPlayNext = { playerConnection.addToQueue(song, playNext = true) },
            onAddToQueue = { playerConnection.addToQueue(song) },
            onLike = { SongLikeState.toggleLike(song) },
            showEditAction = false,
            leadingPrimaryActions =
                listOf(
                    PlayerMenuAction(MoralistIcons.Outlined.Edit, stringResource(Res.string.edit)) {
                        menuEntry = null
                        editEntry = entry
                    },
                ),
            trailingPrimaryActions =
                listOf(
                    PlayerMenuAction(MoralistIcons.Outlined.Delete, stringResource(Res.string.delete)) {
                        menuEntry = null
                        deleteEntry = entry
                    },
                ),
        )
    }
    PopupContent(editEntry) { entry ->
        EditHistoryDialog(
            entry = entry,
            onDismiss = { editEntry = null },
            onSave = { title, artists, album ->
                viewModel.updateEntry(entry.id.toLong(), title, artists, album)
                editEntry = null
            },
        )
    }
    PopupContent(deleteEntry) { entry ->
        DeleteHistoryDialog(
            entry = entry,
            canDeleteRemote = uiState.canDeleteRemote && !entry.remoteFeedbackToken.isNullOrBlank(),
            onDismiss = { deleteEntry = null },
            onDelete = { alsoRemote ->
                viewModel.deleteEntry(entry.id.toLong(), deleteRemote = alsoRemote)
                deleteEntry = null
            },
        )
    }
}
