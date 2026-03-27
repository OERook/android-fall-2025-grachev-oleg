package ru.itis.android.homework_16122025.navigation

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data object LoginScreenObject {
    const val route = "login"
}

@Serializable
data object RegisterScreenObject {
    const val route = "register"
}

@Serializable
data object MusicListScreenObject {
    const val route = "music_list"
}

@Serializable
data object AddSongScreenObject {
    const val route = "add_song"
}

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class ProfileScreenObject(
    val username: String = "",
    val email: String = "",
    val songsCount: Int = 0
) {
    companion object {
        const val route = "profile/{username}/{email}/{songsCount}"
        const val routeBase = "profile"
    }

    fun createRoute(): String = "profile/$username/$email/$songsCount"
}

@Serializable
data object SortBottomSheetObject {
    const val route = "sort_bottom_sheet"
}