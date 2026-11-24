/**
 * Hanz Dev Official (C) 2026
 * Lisensi: Hanz Dev Official
 */

package com.moralist.shared.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.moralist.shared.generated.resources.Res
import com.moralist.shared.generated.resources.sponsor_developer
import com.moralist.shared.generated.resources.support_prompt_message
import com.moralist.shared.generated.resources.support_prompt_never_show
import com.moralist.shared.generated.resources.support_prompt_title
import com.moralist.shared.settings.Settings
import com.moralist.shared.ui.components.AlertDialog
import com.moralist.shared.ui.components.PopupVisibility
import org.jetbrains.compose.resources.stringResource
import kotlin.random.Random

private val SupportPromptLaunchRange = 8..20

internal object SupportPromptSchedule {
    private var dueThisLaunch: Boolean? = null

    fun claimLaunch(settings: Settings = Settings.shared): Boolean = dueThisLaunch ?: isDue(settings).also { dueThisLaunch = it }

    internal fun isDue(
        settings: Settings,
        random: Random = Random.Default,
    ): Boolean {
        if (settings.supportPromptDismissed) return false
        if (settings.supportPromptLaunchThreshold !in SupportPromptLaunchRange) {
            settings.supportPromptLaunchThreshold = SupportPromptLaunchRange.random(random)
        }
        val launches = settings.supportPromptLaunches + 1
        if (launches < settings.supportPromptLaunchThreshold) {
            settings.supportPromptLaunches = launches
            return false
        }
        settings.supportPromptLaunches = 0
        settings.supportPromptLaunchThreshold = SupportPromptLaunchRange.random(random)
        return true
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SupportPrompt() {
    var visible by remember { mutableStateOf(SupportPromptSchedule.claimLaunch()) }
    val uriHandler = LocalUriHandler.current
    val developers =
        remember {
            AboutContributors.all.filter { it.sponsorUrl != null }
        }
    PopupVisibility(visible = visible) {
        AlertDialog(
            onDismissRequest = { visible = false },
            title = { Text(stringResource(Res.string.support_prompt_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text(stringResource(Res.string.support_prompt_message))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        val sponsorLabel = stringResource(Res.string.sponsor_developer)
                        developers.forEach { developer ->
                            val shape = contributorShape(developer)
                            Column(
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .clip(MaterialTheme.shapes.medium)
                                        .clickable(onClickLabel = sponsorLabel, role = Role.Button) {
                                            runCatching { uriHandler.openUri(checkNotNull(developer.sponsorUrl)) }
                                        }.padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    text = developer.name,
                                    style = MaterialTheme.typography.labelLarge,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        Settings.shared.supportPromptDismissed = true
                        visible = false
                    },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(Res.string.support_prompt_never_show))
                }
            },
        )
    }
}
