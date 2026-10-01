package com.example.trac.data

import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReportRepository {
    private val postgrest = SupabaseClientManager.client.postgrest

    suspend fun getReports(): Result<List<ReportData>> = withContext(Dispatchers.IO) {
        runCatching {
            postgrest["reports"]
                .select()
                .decodeList<ReportData>()
        }
    }

    suspend fun createReport(report: ReportData): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            postgrest["reports"].insert(report)
            Unit
        }
    }

    suspend fun updateReportStatus(
        reportId: String,
        newStatus: String,
        completionImageUrl: String? = null,
        completionNotes: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            postgrest["reports"].update({
                set("status", newStatus)
                if (completionImageUrl != null) {
                    set("completion_image_url", completionImageUrl)
                }
                if (completionNotes != null) {
                    set("completion_notes", completionNotes)
                }
            }) {
                filter {
                    eq("id", reportId)
                }
            }
            Unit
        }
    }

    suspend fun upvoteReport(reportId: String, newCount: Int): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            postgrest["reports"].update({
                set("upvote_count", newCount)
            }) {
                filter {
                    eq("id", reportId)
                }
            }
            Unit
        }
    }
}
