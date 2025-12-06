package com.quickbite.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("QuickBitePrefs", Context.MODE_PRIVATE)

    fun saveUserData(userId: Int, userName: String, email: String) {
        prefs.edit().apply {
            putInt("user_id", userId)
            putString("user_name", userName)
            putString("user_email", email)
            putBoolean("is_logged_in", true)
            apply()
        }
    }

    fun getUserId(): Int = prefs.getInt("user_id", -1)
    fun getUserName(): String = prefs.getString("user_name", "") ?: ""
    fun getSavedEmail(): String = prefs.getString("user_email", "") ?: ""
    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)

    fun setGuestMode(isGuest: Boolean) {
        prefs.edit().putBoolean("is_guest", isGuest).apply()
    }

    fun isGuestMode(): Boolean = prefs.getBoolean("is_guest", false)

    fun clearUserData() {
        prefs.edit().clear().apply()
    }
}