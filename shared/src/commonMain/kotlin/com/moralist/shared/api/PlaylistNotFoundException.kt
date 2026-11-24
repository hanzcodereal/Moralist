package com.moralist.shared.api

class PlaylistNotFoundException(
    val playlistId: String,
) : Exception()
