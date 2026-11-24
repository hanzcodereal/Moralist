package com.moralist.android

import android.app.Activity
import com.moralist.android.utils.isAndroidTv
import com.moralist.shared.settings.Settings

internal fun Activity.exitApp() {
    if (isAndroidTv() && Settings.shared.stopMusicOnTaskClear) {
        // TV has no recents gesture; a confirmed root Back exit removes the task instead.
        finishAndRemoveTask()
    } else {
        finish()
    }
}
