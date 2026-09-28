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
}
