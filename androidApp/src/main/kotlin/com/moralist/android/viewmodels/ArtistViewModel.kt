package com.moralist.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moralist.shared.api.artwork.AnimatedArtworkService
import com.moralist.shared.db.MusicDatabase
import kotlinx.coroutines.CoroutineScope
import com.moralist.shared.viewmodels.ArtistViewModel as SharedArtistViewModel

class ArtistViewModel(
    artistId: String? = null,
    animatedArtworkService: AnimatedArtworkService,
    database: MusicDatabase,
    applicationScope: CoroutineScope,
) : ViewModel() {
    val shared = SharedArtistViewModel(artistId, viewModelScope, animatedArtworkService, database, applicationScope)
}
