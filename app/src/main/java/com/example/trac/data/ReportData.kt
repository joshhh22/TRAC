package com.example.trac.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportData(
    @SerialName("id") val id: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("user_name") val userName: String? = null,
    @SerialName("title") val title: String,
    @SerialName("location") val location: String,
    @SerialName("category") val category: String,
    @SerialName("description") val description: String,
    @SerialName("status") val status: String = "Pending",
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("priority") val priority: String = "Sedang",
    @SerialName("completion_image_url") val completionImageUrl: String? = null,
    @SerialName("completion_notes") val completionNotes: String? = null,
    @SerialName("upvote_count") val upvoteCount: Int = 0
)

