// navigation/NavGraph.kt
package ru.itis.android.homework_23102025.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.itis.android.homework_23102025.screens.AddNoteScreen
import ru.itis.android.homework_23102025.screens.LoginScreen
import ru.itis.android.homework_23102025.screens.NotesScreen
import ru.itis.android.homework_23102025.viewmodel.NotesViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val notesViewModel: NotesViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen { email ->
                navController.navigate(Screen.Notes.createRoute(email))
            }
        }

        composable(
            route = Screen.Notes.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            NotesScreen(
                email = email,
                notesViewModel = notesViewModel
            ) {
                navController.navigate(Screen.AddNote.route)
            }
        }

        composable(Screen.AddNote.route) {
            AddNoteScreen(
                notesViewModel = notesViewModel
            ) {
                navController.popBackStack()
            }
        }
    }
}