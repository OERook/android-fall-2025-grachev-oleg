package ru.itis.android.homework_16122025

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.delay
import ru.itis.android.homework_16122025.navigation.*
import ru.itis.android.homework_16122025.screens.AddSongScreen
import ru.itis.android.homework_16122025.screens.LoginScreen
import ru.itis.android.homework_16122025.screens.MusicListScreen
import ru.itis.android.homework_16122025.screens.ProfileScreen
import ru.itis.android.homework_16122025.screens.RecoveryScreen
import ru.itis.android.homework_16122025.screens.RegisterScreen
import ru.itis.android.homework_16122025.screens.SortBottomSheet

@Composable
fun MusicBoxApp() {
    val navController = rememberNavController()

    val sessionManager = ru.itis.android.homework_16122025.di.ServiceLocator.getSessionManager()
    val repository = ru.itis.android.homework_16122025.di.ServiceLocator.getRepository()

    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(500)
        isLoading.value = false
    }

    val startDestination = if (sessionManager.isLoggedIn() && sessionManager.getUserId() != -1) {
        MusicListScreenObject.route
    } else {
        LoginScreenObject.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(LoginScreenObject.route) {
            LoginScreen(
                navController = navController,
                sessionManager = sessionManager,
                repository = repository
            )
        }

        composable(RegisterScreenObject.route) {
            RegisterScreen(
                navController = navController,
                sessionManager = sessionManager,
                repository = repository
            )
        }

        composable(MusicListScreenObject.route) {
            MusicListScreen(
                navController = navController,
                sessionManager = sessionManager,
                repository = repository
            )
        }

        composable(AddSongScreenObject.route) {
            AddSongScreen(
                navController = navController,
                sessionManager = sessionManager,
                repository = repository
            )
        }

        composable(
            route = ProfileScreenObject.route,
            arguments = listOf(
                navArgument("username") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType },
                navArgument("songsCount") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val songsCount = backStackEntry.arguments?.getInt("songsCount") ?: 0
            ProfileScreen(
                navController = navController,
                username = username,
                email = email,
                songsCount = songsCount,
                sessionManager = sessionManager,
                repository = repository
            )
        }

        composable(
            route = "recovery/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            RecoveryScreen(
                navController = navController,
                userId = userId,
                sessionManager = sessionManager,
                repository = repository
            )
        }

        composable(
            route = "sort_bottom_sheet/{sortType}",
            arguments = listOf(
                navArgument("sortType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sortTypeString = backStackEntry.arguments?.getString("sortType") ?: "NEWEST"
            val currentSortType = try {
                ru.itis.android.homework_16122025.data.UserRepository.SortType.valueOf(sortTypeString)
            } catch (e: IllegalArgumentException) {
                ru.itis.android.homework_16122025.data.UserRepository.SortType.NEWEST
            }
            SortBottomSheet(
                navController = navController,
                currentSortType = currentSortType
            )
        }
    }
}