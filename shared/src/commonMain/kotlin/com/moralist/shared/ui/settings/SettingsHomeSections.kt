/*
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 */

package com.moralist.shared.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.moralist.shared.generated.resources.Res
import com.moralist.shared.generated.resources.settings_home_section_dynamic
import com.moralist.shared.generated.resources.settings_home_section_items
import com.moralist.shared.settings.Settings
import com.moralist.shared.ui.screens.HomeShelfBuilder
import com.moralist.shared.ui.screens.homeContentText
import com.moralist.shared.viewmodels.HomeViewModel.HomeUiState
import org.jetbrains.compose.resources.stringResource

@Composable
fun settingsHomeSections(state: HomeUiState): List<SettingsHomeSection> =
    HomeShelfBuilder.build(state, homeContentText(), 1000.dp, includeHidden = true).map { section ->
        SettingsHomeSection(
            id = section.key,
            preferenceKey = section.visibilityKey,
            title = section.title,
            description =
                if (section.items.isNotEmpty()) {
                    stringResource(Res.string.settings_home_section_items, section.items.size)
                } else {
                    stringResource(Res.string.settings_home_section_dynamic)
                },
            visible = Settings.shared.shouldShowHomeSection(section.visibilityKey, section.title),
        )
    }
