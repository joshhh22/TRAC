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
        return runCatching {
            auth.signInWith(Email) {
                this.email = email
                this.password = pass
            }
            // Save session preferences ("cookies")
            val currentUser = auth.currentUserOrNull()
            val fullName = currentUser?.userMetadata?.get("full_name")?.toString()?.replace("\"", "") ?: ""
            val userClass = currentUser?.userMetadata?.get("user_class")?.toString()?.replace("\"", "") ?: "XI RPL"
            val profileImage = currentUser?.userMetadata?.get("profile_image")?.toString()?.replace("\"", "") ?: ""
            val userId = currentUser?.id ?: ""
            val userEmail = currentUser?.email ?: email

            sessionPrefs.saveSession(
                email = userEmail,
                fullName = fullName,
                userId = userId,
                userClass = userClass,
                profileImage = profileImage
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
                        put("role", "Siswa")
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

    fun getLoggedInUserEmail(): String = sessionPrefs.getEmail()

    fun getLoggedInUserName(): String = sessionPrefs.getFullName()

    fun getLoggedInUserClass(): String = sessionPrefs.getUserClass()

    fun getLoggedInUserProfileImage(): String = sessionPrefs.getProfileImage()

    fun updateProfileLocal(fullName: String, userClass: String, profileImage: String? = null) {
        sessionPrefs.updateUserProfile(fullName, userClass, profileImage)
    }

    fun getCurrentUser() = auth.currentUserOrNull()
}
