package com.quickbite.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "QuickBitePrefs"
        private const val KEY_USER_ID = "userId"
        private const val KEY_USER_UID = "userUid" // Firebase UID
        private const val KEY_USER_NAME = "userName"
        private const val KEY_EMAIL = "email"
        private const val KEY_IS_GUEST = "isGuest"
        private const val KEY_SELECTED_BRANCH_ID = "selected_branch_id"
    }

    // Save user data with both Int ID and String UID
    fun saveUserData(userId: Int, userName: String?, email: String?) {
        sharedPreferences.edit()
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_USER_NAME, userName)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    // Save Firebase UID separately
    fun saveFirebaseUid(uid: String) {
        sharedPreferences.edit()
            .putString(KEY_USER_UID, uid)
            .apply()
    }

    // Get Integer user ID (for legacy/database operations)
    fun getUserId(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, -1)
    }

    // Get Firebase UID string
    fun getFirebaseUid(): String? {
        return sharedPreferences.getString(KEY_USER_UID, null)
    }

    // Get user name
    fun getUserName(): String {
        return sharedPreferences.getString(KEY_USER_NAME, "Guest User") ?: "Guest User"
    }

    // Get saved email
    fun getSavedEmail(): String {
        return sharedPreferences.getString(KEY_EMAIL, "") ?: ""
    }

    // Guest mode functions
    fun setGuestMode(isGuest: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_GUEST, isGuest).apply()
    }

    fun isGuestMode(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_GUEST, false)
    }

    // Branch selection
    fun saveSelectedBranch(branchId: Int) {
        sharedPreferences.edit().putInt(KEY_SELECTED_BRANCH_ID, branchId).apply()
    }

    fun getSelectedBranchId(): Int {
        return sharedPreferences.getInt(KEY_SELECTED_BRANCH_ID, -1)
    }

    // Clear all user data
    fun clearUserData() {
        sharedPreferences.edit()
            .remove(KEY_USER_ID)
            .remove(KEY_USER_UID)
            .remove(KEY_USER_NAME)
            .remove(KEY_EMAIL)
            .remove(KEY_IS_GUEST)
            .remove(KEY_SELECTED_BRANCH_ID)
            .apply()
    }

    // Clear everything
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }

    // Check if user is logged in
    fun isLoggedIn(): Boolean {
        return getFirebaseUid() != null || getUserId() != -1
    }
}