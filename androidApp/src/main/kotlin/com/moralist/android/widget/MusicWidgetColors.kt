package com.moralist.android.widget

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import com.moralist.shared.settings.ThemePaletteStyle
import com.moralist.shared.ui.theme.themeColorScheme

internal fun musicWidgetColorScheme(
    artworkSeed: Color?,
    isDark: Boolean,
    systemScheme: () -> ColorScheme,
): ColorScheme = artworkSeed?.let { themeColorScheme(it, isDark, ThemePaletteStyle.TONAL_SPOT) } ?: systemScheme()
