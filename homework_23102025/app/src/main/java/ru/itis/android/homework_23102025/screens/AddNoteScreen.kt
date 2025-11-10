// screens/AddNoteScreen.kt
package ru.itis.android.homework_23102025.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import ru.itis.android.homework_23102025.R
import ru.itis.android.homework_23102025.viewmodel.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    notesViewModel: NotesViewModel,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf<String?>(null) }

    val themeColors = notesViewModel.getThemeColors()
    val focusManager = LocalFocusManager.current

    val errorEmptyTitle = stringResource(R.string.error_title_empty)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Note") },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = themeColors.primary
                )
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(themeColors.background)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = null
                    },
                    label = { Text(stringResource(R.string.note_title_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = titleError != null,
                    supportingText = {
                        if (titleError != null) {
                            Text(text = titleError!!)
                        }
                    },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onNext = {
                            focusManager.clearFocus()
                        }
                    )
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text(stringResource(R.string.note_content_hint)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 16.dp),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    )
                )

                Button(
                    onClick = {
                        if (title.isBlank()) {
                            titleError = errorEmptyTitle
                        } else {
                            notesViewModel.addNote(
                                ru.itis.android.homework_23102025.data.Note(
                                    title = title,
                                    content = content
                                )
                            )
                            onBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeColors.primary
                    )
                ) {
                    Text(stringResource(R.string.save_note_button))
                }
            }
        }
    )
}