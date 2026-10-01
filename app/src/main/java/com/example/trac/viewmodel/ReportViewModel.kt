package com.example.trac.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trac.data.ReportData
import com.example.trac.data.ReportRepository
import com.example.trac.data.SessionPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ReportUiState {
    object Idle : ReportUiState
    object Loading : ReportUiState
    data class Success(val message: String) : ReportUiState
    data class Error(val message: String) : ReportUiState
}

class ReportViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = ReportRepository()
    private val sessionPrefs = SessionPreferences(application.applicationContext)

    private val _uiState = MutableStateFlow<ReportUiState>(ReportUiState.Idle)
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    private val _reports = MutableStateFlow<List<ReportData>>(emptyList())
    val reports: StateFlow<List<ReportData>> = _reports.asStateFlow()

    init {
        fetchReports()
    }

    fun fetchReports() {
        viewModelScope.launch {
            repository.getReports()
                .onSuccess { list ->
                    val statusOverrides = sessionPrefs.getStatusOverrides()
                    val notesOverrides = sessionPrefs.getCompletionNotesOverrides()
                    val imgOverrides = sessionPrefs.getCompletionImageOverrides()

                    _reports.value = list.map { r ->
                        val localStatus = statusOverrides[r.id]
                        val localNotes = notesOverrides[r.id]
                        val localImg = imgOverrides[r.id]

                        if (localStatus != null || localNotes != null || localImg != null) {
                            r.copy(
                                status = localStatus ?: r.status,
                                completionNotes = localNotes ?: r.completionNotes,
                                completionImageUrl = localImg ?: r.completionImageUrl
                            )
                        } else r
                    }.sortedWith(
                        compareByDescending<ReportData> { it.createdAt ?: "" }
                            .thenByDescending { it.id ?: "" }
                    )
                }
                .onFailure {
                    // Keep existing list
                }
        }
    }

    fun createReport(
        category: String,
        location: String,
        title: String,
        description: String,
        imageUrl: String? = null,
        priority: String = "Sedang"
    ) {
        if (title.isBlank() || description.isBlank() || category.isBlank() || location.isBlank()) {
            _uiState.value = ReportUiState.Error("Harap lengkapi seluruh formulir laporan.")
            return
        }

        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading

            val newReport = ReportData(
                userId = sessionPrefs.getUserId(),
                userName = sessionPrefs.getFullName(),
                title = title.trim(),
                location = location.trim(),
                category = category.trim(),
                description = description.trim(),
                status = "Pending",
                imageUrl = imageUrl,
                priority = priority.ifBlank { "Sedang" }
            )

            repository.createReport(newReport)
                .onSuccess {
                    _uiState.value = ReportUiState.Success("Laporan berhasil dibuat!")
                    fetchReports()
                }
                .onFailure { error ->
                    val msg = error.localizedMessage ?: "Gagal mengirim laporan. Pastikan tabel 'reports' sudah dibuat di Supabase."
                    _uiState.value = ReportUiState.Error(msg)
                }
        }
    }

    fun updateReportStatus(
        reportId: String,
        newStatus: String,
        completionImageUrl: String? = null,
        completionNotes: String? = null,
        onResult: (Boolean) -> Unit = {}
    ) {
        // 1. Immediately persist to local device storage so state never resets
        sessionPrefs.saveStatusOverride(reportId, newStatus)
        if (completionNotes != null) {
            sessionPrefs.saveCompletionNotesOverride(reportId, completionNotes)
        }
        if (completionImageUrl != null) {
            sessionPrefs.saveCompletionImageOverride(reportId, completionImageUrl)
        }

        // 2. Optimistic update in-memory StateFlow
        _reports.value = _reports.value.map { r ->
            if (r.id == reportId) {
                r.copy(
                    status = newStatus,
                    completionImageUrl = completionImageUrl ?: r.completionImageUrl,
                    completionNotes = completionNotes ?: r.completionNotes
                )
            } else r
        }

        // 3. Sync to Supabase in background
        viewModelScope.launch {
            val result = repository.updateReportStatus(reportId, newStatus, completionImageUrl, completionNotes)
            result.onSuccess {
                onResult(true)
            }.onFailure {
                onResult(false)
            }
        }
    }

    fun upvoteReport(reportId: String) {
        val target = _reports.value.find { it.id == reportId } ?: return
        val newCount = target.upvoteCount + 1

        // Optimistic update for instant UI feedback
        _reports.value = _reports.value.map { r ->
            if (r.id == reportId) r.copy(upvoteCount = newCount) else r
        }

        viewModelScope.launch {
            repository.upvoteReport(reportId, newCount)
        }
    }

    fun resetState() {
        _uiState.value = ReportUiState.Idle
    }
}
