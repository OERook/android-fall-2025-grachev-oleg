package ru.itis.android.homework_24112025

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.itis.android.homework_24112025.presentation.screens.CoroutinesScreen
import ru.itis.android.homework_24112025.ui.theme.CoroutinesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoroutinesTheme {
                CoroutinesScreen(viewModel = viewModel())
            }
        }
    }
}