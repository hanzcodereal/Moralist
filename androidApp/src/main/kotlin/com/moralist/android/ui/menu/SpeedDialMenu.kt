package com.moralist.android.ui.menu

import androidx.compose.runtime.Composable
import com.moralist.shared.db.MusicDatabase
import com.moralist.shared.model.YTItem
import com.moralist.shared.ui.menu.SpeedDialToggle
import org.koin.compose.koinInject
import com.moralist.shared.ui.menu.rememberSpeedDialToggle as rememberSharedSpeedDialToggle

@Composable
internal fun rememberSpeedDialToggle(item: YTItem): SpeedDialToggle = rememberSharedSpeedDialToggle(item, koinInject<MusicDatabase>())
