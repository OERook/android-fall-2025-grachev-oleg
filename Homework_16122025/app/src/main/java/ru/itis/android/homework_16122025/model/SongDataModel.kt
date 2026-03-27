package ru.itis.android.homework_16122025.model

data class SongDataModel(
    val id: Int = 0,
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val duration: Int = 0,
    val genre: String = "",
    val releaseYear: Int = 0,
    val rating: Float = 0f,
    val coverImage: String = ""
)
