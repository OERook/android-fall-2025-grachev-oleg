package ru.itis.android.homework_16122025.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "MusicBoxSession",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USERNAME = "username"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveSession(userId: Int, email: String, username: String) {
        with(prefs.edit()) {
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USERNAME, username)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun clearSession(){
        with(prefs.edit()){
            clear()
            apply()
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun hasValidSession(): Boolean {
        return isLoggedIn() && getUserId() != -1
    }

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)

    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)

    fun getSessionData(): Triple<Int, String?, String?> {
        return Triple(getUserId(), getUserEmail(), getUsername())
    }
}