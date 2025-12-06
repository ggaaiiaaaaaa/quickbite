package com.quickbite.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "QuickBitePrefs"
        private const val KEY_USER_ID = "userId"
        private const val KEY_USER_NAME = "userName"
        private const val KEY_EMAIL = "email"
        private const val KEY_IS_GUEST = "isGuest"
        private const val KEY_SELECTED_BRANCH_ID = "selected_branch_id" // Key for branch ID
    }

    fun saveUserData(userId: Int, userName: String?, email: String?) {
        sharedPreferences.edit()
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_USER_NAME, userName)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun getUserId(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, -1)
    }

    fun getSavedEmail(): String {
        return sharedPreferences.getString(KEY_EMAIL, "") ?: ""
    }

    fun setGuestMode(isGuest: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_GUEST, isGuest).apply()
    }

    fun isGuestMode(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_GUEST, false)
    }

    // New function to save the selected branch ID
    fun saveSelectedBranch(branchId: Int) {
        sharedPreferences.edit().putInt(KEY_SELECTED_BRANCH_ID, branchId).apply()
    }

    // Optional: Function to retrieve the branch ID if needed later
    fun getSelectedBranchId(): Int {
        return sharedPreferences.getInt(KEY_SELECTED_BRANCH_ID, -1)
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}
