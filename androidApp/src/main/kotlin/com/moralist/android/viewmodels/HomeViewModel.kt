package com.moralist.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moralist.shared.db.MusicDatabase
import com.moralist.shared.viewmodels.HomeViewModel as SharedHomeViewModel

class HomeViewModel(
    database: MusicDatabase,
    likedSongsName: String,
) : ViewModel() {
    val shared = SharedHomeViewModel(database, viewModelScope, likedSongsName = likedSongsName)
}
