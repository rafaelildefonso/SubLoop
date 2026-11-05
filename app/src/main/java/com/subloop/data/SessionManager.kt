package com.subloop.data

import android.content.Context
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val preferences = context.getSharedPreferences("subloop_session", Context.MODE_PRIVATE)

    val currentUserId: Long?
        get() = preferences.getLong(KEY_USER_ID, NO_USER).takeUnless { it == NO_USER }

    fun save(userId: Long) {
        preferences.edit { putLong(KEY_USER_ID, userId) }
    }

    fun clear() {
        preferences.edit { remove(KEY_USER_ID) }
    }

    private companion object {
        const val KEY_USER_ID = "current_user_id"
        const val NO_USER = -1L
    }
}
