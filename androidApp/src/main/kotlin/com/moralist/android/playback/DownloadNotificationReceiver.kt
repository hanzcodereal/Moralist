/**
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 */

package com.moralist.android.playback

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.moralist.android.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class DownloadNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                (context.applicationContext as App).startup.await()
                val manager = GlobalContext.get().get<SongDownloadManager>()
                val action =
                    when (intent.action) {
                        SongDownloadManager.ACTION_PAUSE_ALL -> manager.pauseAll()
                        SongDownloadManager.ACTION_RESUME_ALL -> manager.resumeAll()
                        SongDownloadManager.ACTION_CANCEL_ALL -> manager.cancelAll()
                        else -> return@launch
                    }
                action.join()
                manager.flushQueue()
                if (intent.action == SongDownloadManager.ACTION_CANCEL_ALL) DownloadService.stop(context)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
