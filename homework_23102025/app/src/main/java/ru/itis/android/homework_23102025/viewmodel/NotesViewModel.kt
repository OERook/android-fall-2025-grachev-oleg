// viewmodel/NotesViewModel.kt
package ru.itis.android.homework_23102025.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ru.itis.android.homework_23102025.data.AppTheme
import ru.itis.android.homework_23102025.data.ColorScheme
import ru.itis.android.homework_23102025.data.Note
import androidx.compose.ui.graphics.Color

class NotesViewModel : ViewModel() {
    var notes by mutableStateOf(emptyList<Note>())
        private set

    var selectedTheme by mutableStateOf(AppTheme.BLUE)
        private set

    fun addNote(note: Note) {
        notes = notes + note
    }

    fun updateTheme(theme: AppTheme) {
        selectedTheme = theme
    }

    fun getThemeColors(): ColorScheme {
        return when (selectedTheme) {
            AppTheme.RED -> ColorScheme(
                primary = Color(0xFFD32F2F),
                secondary = Color(0xFFEF5350),
                background = Color(0xFFFFEBEE)
            )
            AppTheme.BLUE -> ColorScheme(
                primary = Color(0xFF1976D2),
                secondary = Color(0xFF42A5F5),
                background = Color(0xFFE3F2FD)
            )
            AppTheme.GREEN -> ColorScheme(
                primary = Color(0xFF388E3C),
                secondary = Color(0xFF66BB6A),
                background = Color(0xFFE8F5E8)
            )
        }
    }
}