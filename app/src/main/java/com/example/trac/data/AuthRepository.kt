package com.example.trac.data

import android.content.Context
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository(context: Context) {
    private val auth = SupabaseClientManager.client.auth
    private val sessionPrefs = SessionPreferences(context)

    suspend fun signUp(email: String, pass: String, fullName: String, userClass: String): Result<Unit> {
        return runCatching {
            auth.signUpWith(Email) {
                this.email = email
                this.password = pass
                this.data = buildJsonObject {
                    put("full_name", fullName)
                    put("role", "Siswa")
                    put("user_class", userClass)
                }
            }
        }
    }

    suspend fun signIn(email: String, pass: String): Result<Unit> {
        val cleanEmail = email.trim()
        val cleanPass = pass.trim()
        val isSuperAdminCreds = cleanEmail.equals(SessionPreferences.SUPER_ADMIN_EMAIL, ignoreCase = true) && cleanPass == "joshua"

        return runCatching {
            val remoteAuthResult = runCatching {
                auth.signInWith(Email) {
                    this.email = cleanEmail
                    this.password = cleanPass
                }
            }

            if (remoteAuthResult.isFailure) {
                if (isSuperAdminCreds) {
                    // Preconfigured Super Admin account fallback
                    sessionPrefs.saveSession(
                        email = SessionPreferences.SUPER_ADMIN_EMAIL,
                        fullName = "Joshua Rompis",
                        userId = "super-admin-joshua",
                        userClass = "Pengurus Utama TRAC",
                        profileImage = "",
                        role = "Admin"
                    )
                    return@runCatching
                } else {
                    remoteAuthResult.getOrThrow()
                }
            }

            // Save session preferences ("cookies")
            val currentUser = auth.currentUserOrNull()
            val fullName = currentUser?.userMetadata?.get("full_name")?.toString()?.replace("\"", "") ?: ""
            val userClass = currentUser?.userMetadata?.get("user_class")?.toString()?.replace("\"", "") ?: "XI RPL"
            val profileImage = currentUser?.userMetadata?.get("profile_image")?.toString()?.replace("\"", "") ?: ""
            val userId = currentUser?.id ?: ""
            val userEmail = currentUser?.email ?: cleanEmail
            val metaRole = currentUser?.userMetadata?.get("role")?.toString()?.replace("\"", "") ?: ""

            val resolvedRole = when {
                userEmail.equals(SessionPreferences.SUPER_ADMIN_EMAIL, ignoreCase = true) -> "Admin"
                sessionPrefs.getAdminEmails().contains(userEmail.lowercase()) -> "Admin"
                metaRole.isNotBlank() && metaRole.contains("Admin", ignoreCase = true) -> "Admin"
                metaRole.isNotBlank() -> metaRole
                else -> "Siswa"
            }

            sessionPrefs.saveSession(
                email = userEmail,
                fullName = if (fullName.isBlank() && userEmail.equals(SessionPreferences.SUPER_ADMIN_EMAIL, ignoreCase = true)) "Joshua Rompis" else fullName,
                userId = userId,
                userClass = userClass,
                profileImage = profileImage,
                role = resolvedRole
            )
        }
    }

    suspend fun updateProfileSupabase(fullName: String, userClass: String, profileImage: String? = null): Result<Unit> {
        return runCatching {
            sessionPrefs.updateUserProfile(fullName, userClass, profileImage)
            runCatching {
                auth.updateUser {
                    this.data = buildJsonObject {
                        put("full_name", fullName)
                        put("role", sessionPrefs.getRole())
                        put("user_class", userClass)
                        if (!profileImage.isNullOrBlank()) {
                            put("profile_image", profileImage)
                        }
                    }
                }
            }
        }
    }

    suspend fun signOut(): Result<Unit> {
        return runCatching {
            sessionPrefs.clearSession()
            runCatching { auth.signOut() }
        }
    }

    fun isUserLoggedIn(): Boolean = sessionPrefs.isLoggedIn()

    fun getLoggedInUserId(): String = sessionPrefs.getUserId()

    fun getLoggedInUserEmail(): String = sessionPrefs.getEmail()

    fun getLoggedInUserName(): String = sessionPrefs.getFullName()

    fun getLoggedInUserClass(): String = sessionPrefs.getUserClass()

    fun getLoggedInUserRole(): String = sessionPrefs.getRole()

    fun isUserAdmin(): Boolean {
        val email = sessionPrefs.getEmail().trim().lowercase()
        if (email == SessionPreferences.SUPER_ADMIN_EMAIL) return true
        if (sessionPrefs.getAdminEmails().contains(email)) return true
        val role = sessionPrefs.getRole()
        return role.equals("Admin", ignoreCase = true)
    }

    fun isSuperAdmin(): Boolean {
        val email = sessionPrefs.getEmail().trim().lowercase()
        return email == SessionPreferences.SUPER_ADMIN_EMAIL
    }

    fun promoteUserToAdmin(email: String) {
        sessionPrefs.addAdminEmail(email)
    }

    fun demoteAdminToUser(email: String) {
        sessionPrefs.removeAdminEmail(email)
    }

    fun getAdminEmails(): Set<String> = sessionPrefs.getAdminEmails()

    fun setLoggedInUserRole(newRole: String) {
        sessionPrefs.saveRole(newRole)
    }

    fun getLoggedInUserProfileImage(): String = sessionPrefs.getProfileImage()

    fun updateProfileLocal(fullName: String, userClass: String, profileImage: String? = null) {
        sessionPrefs.updateUserProfile(fullName, userClass, profileImage)
    }

    fun getCurrentUser() = auth.currentUserOrNull()
}
