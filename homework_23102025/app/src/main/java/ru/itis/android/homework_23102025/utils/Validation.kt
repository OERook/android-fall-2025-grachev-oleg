package ru.itis.android.homework_23102025.utils

object Validation {
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})".toRegex()
        return email.matches(emailRegex)
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length >= 8
    }
}