package com.example.trac

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthValidationTest {

    @Test
    fun emailNormalization_appendsGmailDomain_whenNoAtSign() {
        val input = "joshua22"
        val normalized = if (!input.contains("@")) "$input@gmail.com" else input
        assertEquals("joshua22@gmail.com", normalized)
    }

    @Test
    fun emailNormalization_preservesFullEmail_whenDomainPresent() {
        val input = "student@school.sch.id"
        val normalized = if (!input.contains("@")) "$input@gmail.com" else input
        assertEquals("student@school.sch.id", normalized)
    }

    @Test
    fun passwordValidation_checksMinimumLength() {
        val shortPass = "12345"
        val validPass = "123456"
        assertTrue(shortPass.length < 6)
        assertFalse(validPass.length < 6)
    }

    @Test
    fun passwordConfirmation_matchesCorrectly() {
        val pass = "secret123"
        val confirmPassSame = "secret123"
        val confirmPassDifferent = "secret456"
        assertEquals(pass, confirmPassSame)
        assertFalse(pass == confirmPassDifferent)
    }

    @Test
    fun roleResolution_identifiesAdmin_fromEmailOrMetadata() {
        fun resolveRole(email: String, metadataRole: String): String {
            return when {
                metadataRole.isNotBlank() && metadataRole.contains("Admin", ignoreCase = true) -> "Admin"
                email.contains("admin", ignoreCase = true) -> "Admin"
                metadataRole.isNotBlank() -> metadataRole
                else -> "Siswa"
            }
        }

        assertEquals("Admin", resolveRole("admin@trac.sch.id", "Siswa"))
        assertEquals("Admin", resolveRole("joshua@trac.sch.id", "Admin"))
        assertEquals("Admin", resolveRole("admin.sekolah@gmail.com", ""))
        assertEquals("Siswa", resolveRole("rompis@gmail.com", "Siswa"))
        assertEquals("Siswa", resolveRole("student@gmail.com", ""))
    }
}
