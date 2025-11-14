package ru.itis.android.inception25.utils

import android.content.Context
import androidx.annotation.StringRes

class ResManager(
    private val ctx: Context
) {

    fun getString(@StringRes stringRes: Int): String {
        return ctx.getString(stringRes)
    }

    fun getStringPattern(@StringRes stringRes: Int, vararg args: Any): String {
        return ctx.getString(stringRes, args)
    }
}