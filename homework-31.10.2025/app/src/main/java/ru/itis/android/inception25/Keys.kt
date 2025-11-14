package ru.itis.android.inception25

object Keys {
    const val CURRENT_TEXT_OUTER_KEY = "TEXT_KEY"
    const val SETTINGS_SCREEN_ARGS = "SETTINGS_SCREEN_ARGS"
    const val INTENT_KEY = "intent_sample_key"
    const val EXTRA_PAYLOAD_KEY = "intent_extra_payload"

    object ProfileScreenArg {
        const val FIRST_ARG1 = "firstArg"
        const val SECOND_ARG = "secondArg"
        const val COMPLEX_DATA = "complex_data"
        const val RESULT_ARG = "profile_result"
    }

    object Notifications {
        const val ACTION_REPLY = "ru.itis.android.inception25.REPLY_ACTION"
        const val KEY_TEXT_REPLY = "key_text_reply"
        const val EXTRA_REPLY_TEXT = "extra_reply_text"
        const val NOTIFICATION_ID = "notification_id"
        const val ACTION_NOTIFICATION_DISMISSED = "ru.itis.android.inception25.ACTION_NOTIFICATION_DISMISSED"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    }
}