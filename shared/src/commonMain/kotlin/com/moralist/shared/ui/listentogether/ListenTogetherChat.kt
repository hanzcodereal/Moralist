package com.moralist.shared.ui.listentogether

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moralist.shared.generated.resources.Res
import com.moralist.shared.generated.resources.listen_together_chat
import com.moralist.shared.generated.resources.listen_together_chat_empty
import com.moralist.shared.generated.resources.listen_together_chat_hint
import com.moralist.shared.generated.resources.listen_together_chat_send
import com.moralist.shared.listentogether.ListenTogetherChatMessage
import com.moralist.shared.listentogether.ListenTogetherManager
import org.jetbrains.compose.resources.stringResource

private const val MAX_DRAFT_LENGTH = 400

/** Obrolan room Dengar Bersama. Riwayat tersimpan di server sampai room ditutup. */
@Composable
internal fun ListenTogetherChatCard(manager: ListenTogetherManager) {
    val supported by manager.chatSupported.collectAsState()
    if (!supported) return
    val messages by manager.chatMessages.collectAsState()
    val currentUserId by manager.userId.collectAsState()
    var draft by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(Res.string.listen_together_chat),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (messages.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.listen_together_chat_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(messages, key = { it.id }) { message ->
                            ChatBubble(message = message, mine = message.userId == currentUserId)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = draft,
                        onValueChange = { draft = it.take(MAX_DRAFT_LENGTH) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(stringResource(Res.string.listen_together_chat_hint)) },
                        maxLines = 3,
                        shape = RoundedCornerShape(20.dp),
                    )
                    Button(
                        onClick = { if (manager.sendChat(draft)) draft = "" },
                        enabled = draft.isNotBlank(),
                    ) {
                        Text(stringResource(Res.string.listen_together_chat_send))
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ListenTogetherChatMessage,
    mine: Boolean,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (mine) Alignment.End else Alignment.Start,
    ) {
        if (!mine) {
            Text(
                text = message.username,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp, bottom = 2.dp),
            )
        }
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = if (mine) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = if (mine) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.widthIn(max = 280.dp).padding(horizontal = 14.dp, vertical = 8.dp),
            )
        }
    }
}
