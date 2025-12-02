package ru.itis.android.inception25.model

data class NotificationSettingsModel(
    val title: String = "",
    val content: String = "",
    val isExpandable: Boolean = false,
    val importance: NotificationImportance = NotificationImportance.MEDIUM,
    val shouldOpenActivity: Boolean = false,
    val hasReplyAction: Boolean = false
)

enum class NotificationImportance(val importance: Int) {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    MAX(4)
}

data class UserMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
