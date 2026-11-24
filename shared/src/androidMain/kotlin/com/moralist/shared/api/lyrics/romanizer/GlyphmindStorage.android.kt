/*
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 */

package com.moralist.shared.api.lyrics.romanizer

import com.moralist.shared.youtube.playback.AndroidContextProvider
import java.io.File

internal actual fun glyphmindStorageDirectory(): String =
    File(AndroidContextProvider.applicationContext.noBackupFilesDir, "glyphmind").absolutePath

internal actual fun removeLegacyJapaneseDictionary() {
    File(AndroidContextProvider.applicationContext.noBackupFilesDir, "japanese-dictionary").deleteRecursively()
}
