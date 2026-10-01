package com.example.trac.data

import kotlinx.serialization.Serializable

@Serializable
data class StaffMember(
    val id: String,
    val name: String,
    val role: String,
    val phone: String,
    val activeTasks: Int = 0,
    val isAvailable: Boolean = true
)
