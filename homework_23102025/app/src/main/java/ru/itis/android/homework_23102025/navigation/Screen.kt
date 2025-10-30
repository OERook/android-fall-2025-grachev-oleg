package ru.itis.android.homework_23102025.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Notes : Screen("notes/{email}") {
        fun createRoute(email: String) = "notes/$email"
    }
    object AddNote : Screen("add_note")
}