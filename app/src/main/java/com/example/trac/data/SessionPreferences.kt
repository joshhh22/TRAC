package com.example.trac.data

import android.content.Context
import android.content.SharedPreferences

class SessionPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("trac_user_session", Context.MODE_PRIVATE)

    fun saveSession(
        email: String,
        fullName: String,
        userId: String,
        userClass: String = "XI RPL",
        profileImage: String = "",
        role: String = "Siswa"
    ) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_EMAIL, email)
            .putString(KEY_FULL_NAME, fullName)
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_CLASS, userClass)
            .putString(KEY_PROFILE_IMAGE, profileImage)
            .putString(KEY_USER_ROLE, role)
            .apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""

    fun getFullName(): String {
        val name = prefs.getString(KEY_FULL_NAME, "") ?: ""
        return if (name.isNotBlank()) {
            name
        } else {
            getEmail().substringBefore("@").replaceFirstChar { it.uppercase() }
        }
    }

    fun getUserClass(): String {
        val cls = prefs.getString(KEY_USER_CLASS, "") ?: ""
        return if (cls.isNotBlank()) cls else "XI RPL"
    }

    fun getRole(): String {
        val email = getEmail().trim().lowercase()
        if (getAdminEmails().contains(email)) return "Admin"
        val role = prefs.getString(KEY_USER_ROLE, "") ?: ""
        return if (role.isNotBlank()) role else "Siswa"
    }

    fun saveRole(role: String) {
        prefs.edit().putString(KEY_USER_ROLE, role).apply()
    }

    fun getAdminEmails(): Set<String> {
        val defaultAdmins = setOf(SUPER_ADMIN_EMAIL)
        val stored = prefs.getStringSet(KEY_ADMIN_EMAILS, emptySet()) ?: emptySet()
        return defaultAdmins + stored.map { it.lowercase() }
    }

    fun addAdminEmail(email: String) {
        val clean = email.trim().lowercase()
        if (clean.isBlank()) return
        val current = (prefs.getStringSet(KEY_ADMIN_EMAILS, emptySet()) ?: emptySet()).toMutableSet()
        current.add(clean)
        prefs.edit().putStringSet(KEY_ADMIN_EMAILS, current).apply()
    }

    fun removeAdminEmail(email: String) {
        val clean = email.trim().lowercase()
        if (clean == SUPER_ADMIN_EMAIL) return // Super Admin cannot be removed
        val current = (prefs.getStringSet(KEY_ADMIN_EMAILS, emptySet()) ?: emptySet()).toMutableSet()
        current.remove(clean)
        prefs.edit().putStringSet(KEY_ADMIN_EMAILS, current).apply()
    }

    fun getProfileImage(): String = prefs.getString(KEY_PROFILE_IMAGE, "") ?: ""

    fun updateUserProfile(fullName: String, userClass: String, profileImage: String? = null) {
        val editor = prefs.edit()
            .putString(KEY_FULL_NAME, fullName)
            .putString(KEY_USER_CLASS, userClass)

        if (!profileImage.isNullOrBlank()) {
            editor.putString(KEY_PROFILE_IMAGE, profileImage)
        }
        editor.apply()
    }

    fun isIndonesian(): Boolean = prefs.getBoolean(KEY_IS_INDONESIAN, false)

    fun saveLanguage(isIndonesian: Boolean) {
        prefs.edit().putBoolean(KEY_IS_INDONESIAN, isIndonesian).apply()
    }

    fun isDarkMode(): Boolean = prefs.getBoolean(KEY_IS_DARK_MODE, false)

    fun saveDarkMode(isDark: Boolean) {
        prefs.edit().putBoolean(KEY_IS_DARK_MODE, isDark).apply()
    }

    fun getUserId(): String = prefs.getString(KEY_USER_ID, "") ?: ""

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        const val SUPER_ADMIN_EMAIL = "rompisjosh@gmail.com"
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
        private const val KEY_EMAIL = "key_email"
        private const val KEY_FULL_NAME = "key_full_name"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_USER_CLASS = "key_user_class"
        private const val KEY_USER_ROLE = "key_user_role"
        private const val KEY_ADMIN_EMAILS = "key_admin_emails"
        private const val KEY_PROFILE_IMAGE = "key_profile_image"
        private const val KEY_IS_INDONESIAN = "key_is_indonesian"
        private const val KEY_IS_DARK_MODE = "key_is_dark_mode"
    }
}
