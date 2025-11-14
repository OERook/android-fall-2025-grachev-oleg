package ru.itis.android.inception25.navScreens.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.itis.android.inception25.R
import ru.itis.android.inception25.model.NotificationImportance
import ru.itis.android.inception25.model.NotificationSettingsModel

@Composable
fun NotificationSettingsScreen(
    onNotificationCreated: (NotificationSettingsModel) -> Unit
) {
    val title = remember { mutableStateOf("") }
    val content = remember { mutableStateOf("") }
    val isExpandable = remember { mutableStateOf(false) }
    val importance = remember { mutableStateOf(NotificationImportance.MEDIUM) }
    val shouldOpenActivity = remember { mutableStateOf(false) }
    val hasReplyAction = remember { mutableStateOf(false) }
    val showImportanceMenu = remember { mutableStateOf(false) }
    val titleError = remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(stringResource(R.string.notification_creation_title), fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title.value,
                onValueChange = {
                    title.value = it
                    titleError.value = false
                },
                label = { Text(stringResource(R.string.notification_title_label)) },
                modifier = Modifier.fillMaxWidth(),
                isError = titleError.value
            )
            if (titleError.value) {
                Text(stringResource(R.string.notification_title_error), color = Color.Red, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = content.value,
                onValueChange = { content.value = it },
                label = { Text(stringResource(R.string.notification_content_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                maxLines = 4
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.notification_expandable_label))
                Spacer(modifier = Modifier.width(16.dp))
                Switch(
                    checked = isExpandable.value,
                    onCheckedChange = { isExpandable.value = it },
                    enabled = content.value.isNotEmpty()
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.notification_priority_label))
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { showImportanceMenu.value = true }) {
                    Text(importance.value.name)
                }
                DropdownMenu(
                    expanded = showImportanceMenu.value,
                    onDismissRequest = { showImportanceMenu.value = false }
                ) {
                    NotificationImportance.values().forEach { imp ->
                        DropdownMenuItem(
                            text = { Text(imp.name) },
                            onClick = {
                                importance.value = imp
                                showImportanceMenu.value = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.notification_activity_open_label))
                Spacer(modifier = Modifier.width(16.dp))
                Switch(
                    checked = shouldOpenActivity.value,
                    onCheckedChange = { shouldOpenActivity.value = it }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.notification_reply_action_label))
                Spacer(modifier = Modifier.width(16.dp))
                Switch(
                    checked = hasReplyAction.value,
                    onCheckedChange = { hasReplyAction.value = it },
                    enabled = content.value.isNotEmpty()
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.value.isEmpty()) {
                        titleError.value = true
                    } else {
                        val settings = NotificationSettingsModel(
                            title = title.value,
                            content = content.value,
                            isExpandable = isExpandable.value && content.value.isNotEmpty(),
                            importance = importance.value,
                            shouldOpenActivity = shouldOpenActivity.value,
                            hasReplyAction = hasReplyAction.value && content.value.isNotEmpty()
                        )
                        onNotificationCreated(settings)
                        title.value = ""
                        content.value = ""
                        isExpandable.value = false
                        importance.value = NotificationImportance.MEDIUM
                        shouldOpenActivity.value = false
                        hasReplyAction.value = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.notification_create_button))
            }
        }
    }
}
