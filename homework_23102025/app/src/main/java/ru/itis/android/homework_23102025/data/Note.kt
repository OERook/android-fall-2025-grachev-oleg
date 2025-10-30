package ru.itis.android.homework_23102025.data

data class Note(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val content: String
)