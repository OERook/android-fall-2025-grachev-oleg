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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.itis.android.inception25.R
import ru.itis.android.inception25.model.UserMessage

@Composable
fun UserMessagesScreen(
    messages: List<UserMessage>,
    onMessageAdded: (UserMessage) -> Unit
) {
    val inputText = remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.messages_screen_title), fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(messages) { message ->
                    Text(
                        text = message.text,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText.value,
                    onValueChange = { inputText.value = it },
                    label = { Text(stringResource(R.string.message_input_label)) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (inputText.value.isNotEmpty()) {
                            val newMessage = UserMessage(text = inputText.value)
                            onMessageAdded(newMessage)
                            inputText.value = ""
                        }
                    }
                ) {
                    Text(stringResource(R.string.message_send_button))
                }
            }
        }
    }
}