package ru.itis.android.homework_23102025.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.itis.android.homework_23102025.R
import ru.itis.android.homework_23102025.data.AppTheme
import ru.itis.android.homework_23102025.data.ColorScheme
import ru.itis.android.homework_23102025.data.Note
import ru.itis.android.homework_23102025.viewmodel.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    email: String,
    notesViewModel: NotesViewModel,
    onAddNote: () -> Unit
) {
    val notes = notesViewModel.notes
    val themeColors = notesViewModel.getThemeColors()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeColors.background)
    ) {
        var expanded by remember { mutableStateOf(false) }
        val themes = listOf(
            AppTheme.RED to stringResource(R.string.theme_red),
            AppTheme.BLUE to stringResource(R.string.theme_blue),
            AppTheme.GREEN to stringResource(R.string.theme_green)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            TextButton(
                onClick = { expanded = true },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = themeColors.primary
                )
            ) {
                Text(stringResource(R.string.select_theme))
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                themes.forEach { (theme, name) ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            notesViewModel.updateTheme(theme)
                            expanded = false
                        }
                    )
                }
            }
        }

        Text(
            text = stringResource(R.string.welcome_message, email),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp),
            color = themeColors.primary
        )

        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            ) {
                Text(
                    text = "No notes yet",
                    color = themeColors.secondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(
                    items = notes,
                    key = { note -> note.id }
                ) { note ->
                    NoteItem(
                        note = note,
                        themeColors = themeColors,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(0.1f))

        Button(
            onClick = onAddNote,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = themeColors.primary
            )
        ) {
            Text(
                stringResource(R.string.add_note_button)
            )
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    themeColors: ColorScheme,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = themeColors.secondary.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                color = themeColors.primary
            )
            if (note.content.isNotEmpty()) {
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = themeColors.secondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}