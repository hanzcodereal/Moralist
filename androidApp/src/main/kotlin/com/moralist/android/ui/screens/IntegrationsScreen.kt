/*
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 */

package com.moralist.android.ui.screens

import androidx.compose.runtime.Composable
import com.moralist.android.ui.screens.settings.IntegrationsScreen as SettingsIntegrationsScreen

@Composable
fun IntegrationsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToListenTogether: () -> Unit,
) {
    SettingsIntegrationsScreen(onNavigateBack = onNavigateBack, onNavigateToListenTogether = onNavigateToListenTogether)
}
