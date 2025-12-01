package ru.itis.android.homework_24112025.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2180A8),
    secondary = Color(0xFF5E5240),
    tertiary = Color(0xFF32B8C6),
    background = Color(0xFFFCFCF9),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF5F5F5),
    error = Color(0xFFC01530),
    onPrimary = Color(0xFFFCFCF9),
    onSecondary = Color(0xFFFFFFFF),
    onTertiary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF134252),
    onSurface = Color(0xFF134252),
    onError = Color(0xFFFFFFFF)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF117DDB),
    secondary = Color(0xFFD4AF85),
    tertiary = Color(0xFF6AC8D1),
    background = Color(0xFF1F2121),
    surface = Color(0xFF262828),
    surfaceVariant = Color(0xFF3A3C3C),
    error = Color(0xFFFF5459),
    onPrimary = Color(0xFF134252),
    onSecondary = Color(0xFF1F2121),
    onTertiary = Color(0xFF1F2121),
    onBackground = Color(0xFFF5F5F5),
    onSurface = Color(0xFFF5F5F5),
    onError = Color(0xFF1F2121)
)

@Composable
fun CoroutinesTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}