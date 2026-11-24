/**
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 */

package com.moralist.android.ui.screens.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.moralist.android.BuildConfig
import com.moralist.android.LocalMiniPlayerBottomInset
import com.moralist.android.playback.LocalPlayerConnection
import com.moralist.android.playback.resolveIncomingPlaybackSong
import com.moralist.shared.discord.openDiscordUrl
import com.moralist.shared.generated.resources.Res
import com.moralist.shared.generated.resources.app_name
import com.moralist.shared.generated.resources.logo
import com.moralist.shared.generated.resources.youtube_music
import com.moralist.shared.state.GlobalYouTubeRepository
import com.moralist.shared.ui.screens.AboutCommunityLinkKind
import com.moralist.shared.ui.screens.AboutScreen
import com.moralist.shared.ui.settings.LocalSettingsPageHasTopBar
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AndroidAboutScreen(
    onNavigateBack: () -> Unit,
    onDevtoolsUnlocked: () -> Unit = {},
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val miniPlayerInset = LocalMiniPlayerBottomInset.current
    val playerConnection = LocalPlayerConnection.current
    val coroutineScope = rememberCoroutineScope()
    val youtubeMusicLabel = stringResource(Res.string.youtube_music)

    AboutScreen(
        logo = painterResource(Res.drawable.logo),
        appName = stringResource(Res.string.app_name),
        version = BuildConfig.VERSION_NAME,
        isDebug = BuildConfig.DEBUG,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        bottomInset = miniPlayerInset,
        onNavigateBack = onNavigateBack.takeUnless { LocalSettingsPageHasTopBar.current },
        onOpenCommunityLink = { link ->
            if (link.kind == AboutCommunityLinkKind.DISCORD) {
                runCatching { context.openDiscordUrl(link.url) }
            } else {
                runCatching { uriHandler.openUri(link.url) }
            }
        },
        onDevtoolsUnlocked = onDevtoolsUnlocked,
        onPlayFavoriteSong =
            playerConnection?.let { connection ->
                { videoId ->
                    coroutineScope.launch {
                        val song =
                            resolveIncomingPlaybackSong(
                                videoId = videoId,
                                fallbackArtist = youtubeMusicLabel,
                                loadMetadata = GlobalYouTubeRepository.instance::getSongMetadata,
                                loadPlayerInformation = GlobalYouTubeRepository.instance::getSongPlayerInformation,
                            )
                        connection.playSong(song, "")
                    }
                }
            },
    )
}
