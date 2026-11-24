/**
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 */

package com.moralist.android.di

import coil3.ImageLoader
import coil3.imageLoader
import com.moralist.android.playback.AudioCacheManager
import com.moralist.android.playback.ExoPlaybackBenchmarkPlayer
import com.moralist.android.playback.PlayerConnection
import com.moralist.android.playback.SongDownloadManager
import com.moralist.android.widget.MoralistWidgetManager
import com.moralist.android.widget.PlaylistWidgetManager
import com.metrolist.innertubex.InnerTube
import com.metrolist.innertubex.cipher.RemotePlayerConfigStore
import com.metrolist.innertubex.cipher.YouTubeCipherService
import com.metrolist.innertubex.extraction.InnerTubeExtractor
import com.metrolist.innertubex.extraction.TokenProvider
import com.metrolist.innertubex.extraction.YtConfigParserImpl
import com.metrolist.innertubex.extraction.strategy.ContentAwareFallbackStrategy
import com.moralist.shared.api.YouTubeRepository
import com.moralist.shared.api.musicvideo.MusicVideoResolver
import com.moralist.shared.db.MusicDatabase
import com.moralist.shared.db.daos.DatabaseDao
import com.moralist.shared.db.entities.toSongItem
import com.moralist.shared.db.getDatabaseBuilder
import com.moralist.shared.youtube.benchmark.PlaybackBenchmarkEnvironment
import com.moralist.shared.youtube.benchmark.PlaybackBenchmarkRunner
import com.moralist.shared.youtube.logging.MoralistInnerTubeLogger
import com.moralist.shared.youtube.playback.DefaultStreamResolver
import com.moralist.shared.youtube.playback.LocalAudioSource
import com.moralist.shared.youtube.strategy.ClientHealthTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule =
    module {
        single<CoroutineScope> {
            CoroutineScope(SupervisorJob() + Dispatchers.Default)
        }

        single<MusicDatabase> {
            getDatabaseBuilder(androidContext()).build()
        }

        single<DatabaseDao> {
            get<MusicDatabase>().dao()
        }

        single<AudioCacheManager> {
            AudioCacheManager(
                context = androidContext(),
                database = get(),
            )
        }

        single<LocalAudioSource> {
            LocalAudioSource { id, song ->
                val storedSong = song ?: get<MusicDatabase>().dao().getSongById(id)?.toSongItem()
                storedSong?.let { get<AudioCacheManager>().getCachedPlaybackResource(it) }
            }
        }

        single<SongDownloadManager> {
            SongDownloadManager(
                context = androidContext(),
                audioCacheManager = get(),
                database = get(),
            )
        }

        single<ImageLoader> {
            androidContext().imageLoader
        }

        single<PlayerConnection> {
            PlayerConnection(
                context = androidContext(),
                scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
            )
        }

        single<PlaylistWidgetManager> {
            PlaylistWidgetManager(
                context = androidContext(),
                database = get(),
                applicationScope = get(),
                imageLoader = get(),
            )
        }

        single<MoralistWidgetManager> {
            MoralistWidgetManager(
                context = androidContext(),
                playlistWidgetManager = get(),
                imageLoader = get(),
            )
        }

        single {
            val innerTube = get<InnerTube>()
            val tokenProvider = get<TokenProvider>()
            val cipherService = get<YouTubeCipherService>()
            val musicVideoResolver = get<MusicVideoResolver>()
            PlaybackBenchmarkRunner(
                environmentFor = { anonymousSession ->
                    val benchmarkHealthTracker = ClientHealthTracker(persistenceEnabled = false)
                    val benchmarkInnerTube = innerTube.createIsolatedSession(includeAccount = !anonymousSession)
                    val benchmarkExtractor =
                        InnerTubeExtractor(
                            configParser =
                                YtConfigParserImpl(
                                    innerTube.httpClient,
                                    benchmarkInnerTube,
                                    get<RemotePlayerConfigStore>(),
                                    MoralistInnerTubeLogger,
                                    cipherService = cipherService,
                                ),
                            cipherService = cipherService,
                            innerTube = benchmarkInnerTube,
                            fallbackStrategy = ContentAwareFallbackStrategy(benchmarkHealthTracker),
                            tokenProvider = tokenProvider,
                            clientHealthMonitor = benchmarkHealthTracker,
                            logger = MoralistInnerTubeLogger,
                        )
                    PlaybackBenchmarkEnvironment(
                        httpClient = benchmarkInnerTube.httpClient,
                        youtubeRepository = YouTubeRepository(benchmarkInnerTube),
                        // The runner calls this off the main thread; ExoPlayer must be created and configured on main.
                        playerFactory = { runBlocking(Dispatchers.Main) { ExoPlaybackBenchmarkPlayer(androidContext()) } },
                        resolverFor = {
                            DefaultStreamResolver(
                                extractor = benchmarkExtractor,
                                innerTube = benchmarkInnerTube,
                                tokenProvider = tokenProvider,
                                clientHealthTracker = benchmarkHealthTracker,
                                playerConfigRefresh = { cipherService.refreshAfterStreamRejection() },
                                musicVideoResolver = musicVideoResolver,
                            )
                        },
                    )
                },
            )
        }

        includes(viewModelsModule)
    }
