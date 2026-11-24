package com.moralist.shared.db

fun interface OfflineSongAvailability {
    suspend fun songIds(): Set<String>
}
