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
                    _reports.value = list
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
        imageUrl: String? = null
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
                imageUrl = imageUrl
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

    fun resetState() {
        _uiState.value = ReportUiState.Idle
    }
}
