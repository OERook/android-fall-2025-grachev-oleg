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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.itis.android.inception25.R

@Composable
fun NotificationEditScreen(
    activeNotificationIds: Set<Int>,
    onUpdateNotification: (Int, String) -> Boolean,
    onCancelAllNotifications: () -> Boolean
) {
    val notificationId = remember { mutableStateOf("") }
    val newText = remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.notification_edit_title))
            Spacer(modifier = Modifier.height(16.dp))

            Text(stringResource(R.string.active_notification_ids, activeNotificationIds.joinToString()))
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = notificationId.value,
                onValueChange = { notificationId.value = it },
                label = { Text(stringResource(R.string.notification_id_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = newText.value,
                onValueChange = { newText.value = it },
                label = { Text(stringResource(R.string.notification_new_text_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        val id = notificationId.value.toIntOrNull()
                        if (id != null && newText.value.isNotEmpty()) {
                            onUpdateNotification(id, newText.value)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.notification_update_button))
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        onCancelAllNotifications()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.notification_clear_all_button))
                }
            }
        }
    }
}