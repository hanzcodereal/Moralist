/*
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.moralist.shared.ui.equalizer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.moralist.shared.generated.resources.Res
import com.moralist.shared.generated.resources.close
import com.moralist.shared.generated.resources.equalizer
import com.moralist.shared.ui.components.BasicAlertDialog
import com.moralist.shared.ui.icons.MoralistIcons
import org.jetbrains.compose.resources.stringResource

@Composable
fun MoraEqDialog(
    onDismiss: () -> Unit,
    rememberProfilePicker: (@Composable (onPicked: (fileName: String, content: String) -> Unit) -> () -> Unit)? = null,
    isAudioProcessingAvailable: Boolean = true,
    initialAutoEqSearch: Boolean = false,
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(16.dp).widthIn(max = 560.dp).fillMaxWidth(),
    ) {
        Surface(
            shape = AlertDialogDefaults.shape,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.heightIn(max = 800.dp).fillMaxHeight(0.9f),
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        stringResource(Res.string.equalizer),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = onDismiss, shapes = IconButtonDefaults.shapes()) {
                        Icon(MoralistIcons.Default.Close, stringResource(Res.string.close))
                    }
                }
                Box(Modifier.weight(1f)) {
                    MoraEqScreen(
                        rememberProfilePicker = rememberProfilePicker,
                        isAudioProcessingAvailable = isAudioProcessingAvailable,
                        initialAutoEqSearch = initialAutoEqSearch,
                    )
                }
            }
        }
    }
}
