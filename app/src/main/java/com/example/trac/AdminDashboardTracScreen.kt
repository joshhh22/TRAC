package com.example.trac

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trac.data.ReportData
import com.example.trac.data.SessionPreferences
import com.example.trac.util.ImageUtils

enum class AdminSubTab {
    DASHBOARD,
    REPORTS,
    REPORT_DETAIL,
    ASSIGN_STAFF,
    USERS_PELAPOR,
    CATEGORIES_LOCATIONS,
    ADMIN_NOTIF
}

data class StaffMember(
    val id: String,
    val name: String,
    val role: String,
    val phone: String,
    val activeTasks: Int,
    val isAvailable: Boolean
)

data class StudentReporter(
    val id: String,
    val name: String,
    val className: String,
    val email: String,
    val totalReports: Int,
    val resolvedReports: Int
)

data class FacilityLocation(
    val floorName: String,
    val floorDesc: String,
    val roomCount: Int,
    val activeReportsCount: Int,
    val rooms: List<String>
)

data class AdminNotifItem(
    val id: String,
    val title: String,
    val desc: String,
    val timeAgo: String,
    val isUrgent: Boolean,
    var isRead: Boolean = false,
    val reportId: String? = null
)

@Composable
fun AdminDashboardTracScreen(
    adminName: String = "Admin TRAC",
    adminEmail: String = "rompisjosh@gmail.com",
    isSuperAdmin: Boolean = true,
    adminEmails: Set<String> = setOf("rompisjosh@gmail.com"),
    isIndonesian: Boolean = true,
    isDarkMode: Boolean = false,
    reportsList: List<ReportData> = emptyList(),
    selectedReportInitial: ReportData? = null,
    onBackToUserModeClick: () -> Unit = {},
    onUpdateReportStatus: (reportId: String, newStatus: String) -> Unit = { _, _ -> },
    onToggleUserAdminRole: (email: String, shouldBeAdmin: Boolean) -> Unit = { _, _ -> }
) {
    // Dynamic Theme Tokens matching standard TRAC visual system
    val pageBg = if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF8FAFD)
    val cardBg = if (isDarkMode) Color(0xFF1E293B) else Color.White
    val textPrimary = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
    val borderCol = if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)

    var currentTab by rememberSaveable { mutableStateOf(AdminSubTab.DASHBOARD) }
    var selectedReport by remember {
        mutableStateOf(selectedReportInitial ?: reportsList.firstOrNull())
    }

    if (selectedReport == null && reportsList.isNotEmpty()) {
        selectedReport = reportsList.first()
    }

    // Mock initial staff directory
    var staffList by remember {
        mutableStateOf(
            listOf(
                StaffMember("STF-01", "Pak Joko Widodo", "Teknisi Kelistrikan & Lampu", "0812-3456-7890", 3, true),
                StaffMember("STF-02", "Pak Bambang Pamungkas", "Teknisi AC & Pendingin Ruangan", "0813-8877-6655", 2, true),
                StaffMember("STF-03", "Ibu Siti Khadijah", "Koordinator Fasilitas & Sanitasi", "0819-2233-4455", 1, true),
                StaffMember("STF-04", "Mas Fajar Pratama", "Teknisi IT, Lab & Jaringan", "0857-1122-3344", 4, false),
                StaffMember("STF-05", "Pak Rudi Hartono", "Staff Sarpras & Perabot Sipil", "0821-9988-7766", 0, true)
            )
        )
    }

    // Student Reporters List
    val studentReporters = remember {
        listOf(
            StudentReporter("USR-101", "rompis", "XI RPL", "rompisjosh@gmail.com", 8, 6),
            StudentReporter("USR-102", "Rayya Al-Fatih", "XI RPL", "rayya@gmail.com", 4, 3),
            StudentReporter("USR-103", "Joshua Benjamin", "XI RPL", "joshua@gmail.com", 5, 5),
            StudentReporter("USR-104", "Kevin Sanjaya", "XI TKJ", "kevin@gmail.com", 3, 2),
            StudentReporter("USR-105", "Nadya Putri", "XII RPL", "nadya@gmail.com", 2, 1),
            StudentReporter("USR-106", "Dimas Anggara", "X RPL 1", "dimas@gmail.com", 4, 2)
        )
    }

    // Master Locations per 4 floors
    val facilityLocations = remember {
        listOf(
            FacilityLocation("Lantai 1", "Lobi Utama & Kantor Administrasi", 8, 2, listOf("Lobi Utama", "Ruang Tata Usaha", "Lab Komputer 1", "Lab Komputer 2", "Toilet Barat", "Ruang UKS", "Kantin")),
            FacilityLocation("Lantai 2", "Ruang Guru & Kelas Teori", 10, 3, listOf("Ruang Guru", "Perpustakaan Digital", "Kelas X RPL 1", "Kelas X RPL 2", "Kelas X TKJ", "Toilet Lantai 2")),
            FacilityLocation("Lantai 3", "Kelas Kejuruan & Audio Visual", 9, 4, listOf("Kelas XI RPL", "Kelas XI TKJ", "Lab Pemrograman Mobile", "Ruang Audio Visual", "Musholla")),
            FacilityLocation("Lantai 4", "Kelas Akhir & Pusat Jaringan IT", 7, 1, listOf("Kelas XII RPL", "Kelas XII TKJ", "Ruang Server Pusat", "Studio Multimedia", "Rooftop Garden"))
        )
    }

    // Admin Notifications
    var adminNotifications by remember {
        mutableStateOf(
            listOf(
                AdminNotifItem("NOTIF-1", "Laporan Masuk: Stopkontak Korslet", "Lab Komputer 1 - Perlu teknisi listrik darurat.", "10 menit lalu", true, false),
                AdminNotifItem("NOTIF-2", "Laporan Baru: AC Berisik & Bocor", "Kelas XI RPL - Suhu kelas panas mengganggu KBM.", "35 menit lalu", false, false),
                AdminNotifItem("NOTIF-3", "Status Diperbarui oleh Teknisi", "Pak Joko telah menyelesaikan perbaikan proyektor Lab 2.", "2 jam lalu", false, true),
                AdminNotifItem("NOTIF-4", "Laporan Selesai Diverifikasi", "Wastafel Toilet Lantai 2 telah normal kembali.", "1 hari lalu", false, true)
            )
        )
    }

    // Metrics counters
    val totalCount = reportsList.size
    val pendingCount = reportsList.count { it.status.equals("Pending", ignoreCase = true) }
    val inProgressCount = reportsList.count { it.status.equals("In Progress", ignoreCase = true) }
    val completedCount = reportsList.count { it.status.equals("Completed", ignoreCase = true) }
    val completionRate = if (totalCount > 0) (completedCount * 100) / totalCount else 100

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = pageBg
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Admin Hub Bar
            AdminTopBar(
                adminName = adminName,
                isSuperAdmin = isSuperAdmin,
                isIndonesian = isIndonesian,
                isDarkMode = isDarkMode,
                cardBg = cardBg,
                borderCol = borderCol,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                onBackToUserMode = onBackToUserModeClick
            )

            // 2. Horizontal Sub-Tab Navigation Bar (7 Tabs)
            AdminSubTabRow(
                currentTab = currentTab,
                isIndonesian = isIndonesian,
                isDarkMode = isDarkMode,
                onTabSelect = { currentTab = it }
            )

            // 3. Tab Content View with smooth animated transition
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    (slideInHorizontally { it / 3 } + fadeIn(tween(220)))
                        .togetherWith(slideOutHorizontally { -it / 3 } + fadeOut(tween(180)))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = "AdminSubTabTransition"
            ) { tab ->
                when (tab) {
                    AdminSubTab.DASHBOARD -> {
                        AdminDashboardView(
                            isIndonesian = isIndonesian,
                            isDarkMode = isDarkMode,
                            cardBg = cardBg,
                            borderCol = borderCol,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            totalCount = totalCount,
                            pendingCount = pendingCount,
                            inProgressCount = inProgressCount,
                            completedCount = completedCount,
                            completionRate = completionRate,
                            staffCount = staffList.size,
                            reportsList = reportsList,
                            onNavigateToTab = { currentTab = it },
                            onOpenReportDetail = { report ->
                                selectedReport = report
                                currentTab = AdminSubTab.REPORT_DETAIL
                            }
                        )
                    }

                    AdminSubTab.REPORTS -> {
                        AdminReportsListView(
                            reportsList = reportsList,
                            isIndonesian = isIndonesian,
                            isDarkMode = isDarkMode,
                            cardBg = cardBg,
                            borderCol = borderCol,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            onUpdateStatus = { reportId, newStatus ->
                                onUpdateReportStatus(reportId, newStatus)
                            },
                            onOpenDetail = { report ->
                                selectedReport = report
                                currentTab = AdminSubTab.REPORT_DETAIL
                            }
                        )
                    }

                    AdminSubTab.REPORT_DETAIL -> {
                        AdminReportDetailView(
                            report = selectedReport,
                            staffList = staffList,
                            isIndonesian = isIndonesian,
                            isDarkMode = isDarkMode,
                            cardBg = cardBg,
                            borderCol = borderCol,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            onUpdateStatus = { reportId, newStatus ->
                                onUpdateReportStatus(reportId, newStatus)
                                selectedReport = selectedReport?.copy(status = newStatus)
                            },
                            onBackToReports = { currentTab = AdminSubTab.REPORTS }
                        )
                    }

                    AdminSubTab.ASSIGN_STAFF -> {
                        AdminAssignStaffView(
                            staffList = staffList,
                            reportsList = reportsList,
                            isIndonesian = isIndonesian,
                            isDarkMode = isDarkMode,
                            cardBg = cardBg,
                            borderCol = borderCol,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            onAssignStaff = { staffId, reportId ->
                                staffList = staffList.map {
                                    if (it.id == staffId) it.copy(activeTasks = it.activeTasks + 1) else it
                                }
                            }
                        )
                    }

                    AdminSubTab.USERS_PELAPOR -> {
                        AdminUsersPelaporView(
                            students = studentReporters,
                            isSuperAdmin = isSuperAdmin,
                            adminEmails = adminEmails,
                            isIndonesian = isIndonesian,
                            isDarkMode = isDarkMode,
                            cardBg = cardBg,
                            borderCol = borderCol,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            onToggleAdmin = { email, makeAdmin ->
                                onToggleUserAdminRole(email, makeAdmin)
                            }
                        )
                    }

                    AdminSubTab.CATEGORIES_LOCATIONS -> {
                        AdminCategoriesLocationsView(
                            locations = facilityLocations,
                            reportsList = reportsList,
                            isIndonesian = isIndonesian,
                            isDarkMode = isDarkMode,
                            cardBg = cardBg,
                            borderCol = borderCol,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                    }

                    AdminSubTab.ADMIN_NOTIF -> {
                        AdminNotifView(
                            notifications = adminNotifications,
                            isIndonesian = isIndonesian,
                            isDarkMode = isDarkMode,
                            cardBg = cardBg,
                            borderCol = borderCol,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            onMarkAllRead = {
                                adminNotifications = adminNotifications.map { it.copy(isRead = true) }
                            },
                            onNotificationClick = { notif ->
                                val matchedReport = reportsList.find { it.id == notif.reportId }
                                if (matchedReport != null) {
                                    selectedReport = matchedReport
                                    currentTab = AdminSubTab.REPORT_DETAIL
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 1. TOP BAR
// -------------------------------------------------------------
@Composable
private fun AdminTopBar(
    adminName: String,
    isSuperAdmin: Boolean,
    isIndonesian: Boolean,
    isDarkMode: Boolean,
    cardBg: Color,
    borderCol: Color,
    textPrimary: Color,
    textSecondary: Color,
    onBackToUserMode: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, spotColor = Color(0x15000000)),
        color = cardBg,
        border = BorderStroke(1.dp, borderCol)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // TRAC Brand Shield Icon Badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AdminShieldCrownIcon(color = Color.White, size = 22.dp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isIndonesian) "Panel Admin & Pengurus" else "Admin & Facility Hub",
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF2563EB)
                        ) {
                            Text(
                                text = if (isSuperAdmin) "ADMIN UTAMA" else "ADMIN",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (isIndonesian) "TRAC Fasilitas Sekolah • $adminName" else "School Facility Management • $adminName",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = textSecondary
                    )
                }
            }

            // Button: Back to Reporter Mode ("Mode Pelapor")
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onBackToUserMode() },
                shape = RoundedCornerShape(12.dp),
                color = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEFF6FF),
                border = BorderStroke(1.dp, Color(0xFF2563EB).copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "←",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isIndonesian) "Mode Pelapor" else "Reporter Mode",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. HORIZONTAL SUB-TAB SELECTOR (7 TABS)
// -------------------------------------------------------------
@Composable
private fun AdminSubTabRow(
    currentTab: AdminSubTab,
    isIndonesian: Boolean,
    isDarkMode: Boolean,
    onTabSelect: (AdminSubTab) -> Unit
) {
    val tabs = listOf(
        AdminSubTab.DASHBOARD to (if (isIndonesian) "Dashboard" else "Dashboard"),
        AdminSubTab.REPORTS to (if (isIndonesian) "Laporan" else "Reports"),
        AdminSubTab.REPORT_DETAIL to (if (isIndonesian) "Report Detail" else "Report Detail"),
        AdminSubTab.ASSIGN_STAFF to (if (isIndonesian) "Assign Staff" else "Assign Staff"),
        AdminSubTab.USERS_PELAPOR to (if (isIndonesian) "Users Pelapor" else "Student Users"),
        AdminSubTab.CATEGORIES_LOCATIONS to (if (isIndonesian) "Categories-locations" else "Categories-locations"),
        AdminSubTab.ADMIN_NOTIF to (if (isIndonesian) "Admin Notif" else "Admin Notif")
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDarkMode) Color(0xFF131D2F) else Color(0xFFF1F5F9)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEach { (tab, label) ->
                val isSelected = currentTab == tab
                val bg = if (isSelected) Color(0xFF2563EB) else if (isDarkMode) Color(0xFF1E293B) else Color.White
                val textColor = if (isSelected) Color.White else if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569)
                val border = if (isSelected) Color(0xFF1D4ED8) else if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onTabSelect(tab) },
                    shape = RoundedCornerShape(10.dp),
                    color = bg,
                    border = BorderStroke(1.dp, border)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AdminTabIcon(tab = tab, color = textColor, size = 15.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            color = textColor
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. TAB 1: DASHBOARD
// -------------------------------------------------------------
@Composable
private fun AdminDashboardView(
    isIndonesian: Boolean,
    isDarkMode: Boolean,
    cardBg: Color,
    borderCol: Color,
    textPrimary: Color,
    textSecondary: Color,
    totalCount: Int,
    pendingCount: Int,
    inProgressCount: Int,
    completedCount: Int,
    completionRate: Int,
    staffCount: Int,
    reportsList: List<ReportData>,
    onNavigateToTab: (AdminSubTab) -> Unit,
    onOpenReportDetail: (ReportData) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Welcome Banner in TRAC Blue
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEFF6FF),
            border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF2563EB).copy(alpha = 0.4f) else Color(0xFFBFDBFE))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = if (isIndonesian) "Ringkasan Operasional Fasilitas" else "Facility Operational Overview",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDarkMode) Color(0xFF93C5FD) else Color(0xFF1E40AF)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isIndonesian)
                        "Pantau seluruh laporan sarana prasarana sekolah, tugaskan teknisi, dan ubah status pengerjaan secara real-time."
                    else
                        "Monitor school facility reports, assign technical staff, and update resolution progress in real time.",
                    fontSize = 12.sp,
                    color = if (isDarkMode) Color(0xFF60A5FA) else Color(0xFF2563EB),
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Metrics Grid
        Text(
            text = if (isIndonesian) "Statistik Utama" else "Key Metrics",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            MetricCard(
                title = if (isIndonesian) "Total Laporan" else "Total Reports",
                value = "$totalCount",
                subtitle = if (isIndonesian) "Laporan tercatat" else "Recorded",
                cardColor = cardBg,
                accentColor = Color(0xFF2563EB),
                borderColor = borderCol,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(10.dp))

            MetricCard(
                title = if (isIndonesian) "Menunggu (Pending)" else "Pending",
                value = "$pendingCount",
                subtitle = if (isIndonesian) "Perlu ditindak" else "Action needed",
                cardColor = cardBg,
                accentColor = Color(0xFFD97706),
                borderColor = borderCol,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            MetricCard(
                title = if (isIndonesian) "Sedang Diproses" else "In Progress",
                value = "$inProgressCount",
                subtitle = if (isIndonesian) "Oleh teknisi" else "Under repair",
                cardColor = cardBg,
                accentColor = Color(0xFF3B82F6),
                borderColor = borderCol,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(10.dp))

            MetricCard(
                title = if (isIndonesian) "Selesai (Resolved)" else "Resolved",
                value = "$completedCount",
                subtitle = "$completionRate% " + if (isIndonesian) "terselesaikan" else "resolved",
                cardColor = cardBg,
                accentColor = Color(0xFF10B981),
                borderColor = borderCol,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Actions Row
        Text(
            text = if (isIndonesian) "Aksi Cepat Admin" else "Quick Actions",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdminActionButton(
                label = if (isIndonesian) "Kelola Laporan" else "Manage Reports",
                color = Color(0xFF2563EB),
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToTab(AdminSubTab.REPORTS) }
            )

            AdminActionButton(
                label = if (isIndonesian) "Tugaskan Staf" else "Assign Staff",
                color = Color(0xFF0284C7),
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToTab(AdminSubTab.ASSIGN_STAFF) }
            )

            AdminActionButton(
                label = if (isIndonesian) "Data Lokasi" else "Locations",
                color = Color(0xFF10B981),
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToTab(AdminSubTab.CATEGORIES_LOCATIONS) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Incoming Reports Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isIndonesian) "Laporan Masuk Terbaru" else "Recent Incoming Reports",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )

            Text(
                text = if (isIndonesian) "Lihat Semua →" else "View All →",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2563EB),
                modifier = Modifier.clickable { onNavigateToTab(AdminSubTab.REPORTS) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (reportsList.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = cardBg,
                border = BorderStroke(1.dp, borderCol)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isIndonesian) "Belum ada laporan masuk dari pelapor." else "No reports submitted yet.",
                        fontSize = 13.sp,
                        color = textSecondary
                    )
                }
            }
        } else {
            reportsList.take(4).forEach { report ->
                AdminCompactReportCard(
                    report = report,
                    cardBg = cardBg,
                    borderCol = borderCol,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    onClick = { onOpenReportDetail(report) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// -------------------------------------------------------------
// 4. TAB 2: LAPORAN (REPORTS LIST WITH STATUS CONTROL)
// -------------------------------------------------------------
@Composable
private fun AdminReportsListView(
    reportsList: List<ReportData>,
    isIndonesian: Boolean,
    isDarkMode: Boolean,
    cardBg: Color,
    borderCol: Color,
    textPrimary: Color,
    textSecondary: Color,
    onUpdateStatus: (reportId: String, newStatus: String) -> Unit,
    onOpenDetail: (ReportData) -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf("All") }

    val filteredList = remember(reportsList, searchQuery, selectedFilter) {
        reportsList.filter { report ->
            val matchesQuery = searchQuery.isBlank() ||
                    report.title.contains(searchQuery, ignoreCase = true) ||
                    report.location.contains(searchQuery, ignoreCase = true) ||
                    (report.userName ?: "").contains(searchQuery, ignoreCase = true) ||
                    report.category.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Pending" -> report.status.equals("Pending", ignoreCase = true)
                "In Progress" -> report.status.equals("In Progress", ignoreCase = true)
                "Completed" -> report.status.equals("Completed", ignoreCase = true)
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Input Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = cardBg,
            border = BorderStroke(1.dp, borderCol)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchVectorIcon(color = textSecondary, size = 18.dp)
                Spacer(modifier = Modifier.width(10.dp))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    textStyle = TextStyle(
                        fontSize = 13.5.sp,
                        color = textPrimary,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = if (isIndonesian) "Cari judul, lokasi, atau pelapor..." else "Search title, room, or reporter...",
                                fontSize = 13.5.sp,
                                color = textSecondary
                            )
                        }
                        innerTextField()
                    }
                )
                if (searchQuery.isNotEmpty()) {
                    Text(
                        text = "✕",
                        fontSize = 14.sp,
                        color = textSecondary,
                        modifier = Modifier.clickable { searchQuery = "" }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Status Filter Chips
        val filters = listOf(
            "All" to (if (isIndonesian) "Semua (${reportsList.size})" else "All (${reportsList.size})"),
            "Pending" to (if (isIndonesian) "Menunggu (${reportsList.count { it.status.equals("Pending", ignoreCase = true) }})" else "Pending (${reportsList.count { it.status.equals("Pending", ignoreCase = true) }})"),
            "In Progress" to (if (isIndonesian) "Diproses (${reportsList.count { it.status.equals("In Progress", ignoreCase = true) }})" else "In Progress (${reportsList.count { it.status.equals("In Progress", ignoreCase = true) }})"),
            "Completed" to (if (isIndonesian) "Selesai (${reportsList.count { it.status.equals("Completed", ignoreCase = true) }})" else "Resolved (${reportsList.count { it.status.equals("Completed", ignoreCase = true) }})")
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { (key, label) ->
                val isSelected = selectedFilter == key
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { selectedFilter = key },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) Color(0xFF2563EB) else if (isDarkMode) Color(0xFF1E293B) else Color.White,
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF1D4ED8) else borderCol)
                ) {
                    Text(
                        text = label,
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else textSecondary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Lazy Reports List
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isIndonesian) "Tidak ada laporan yang sesuai dengan filter." else "No reports match the filter.",
                    fontSize = 13.sp,
                    color = textSecondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredList, key = { it.id ?: it.title }) { report ->
                    AdminReportManageCard(
                        report = report,
                        isIndonesian = isIndonesian,
                        isDarkMode = isDarkMode,
                        cardBg = cardBg,
                        borderCol = borderCol,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        onUpdateStatus = { newStatus ->
                            report.id?.let { onUpdateStatus(it, newStatus) }
                        },
                        onOpenDetail = { onOpenDetail(report) }
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. TAB 3: REPORT DETAIL (ADMIN VIEW)
// -------------------------------------------------------------
@Composable
private fun AdminReportDetailView(
    report: ReportData?,
    staffList: List<StaffMember>,
    isIndonesian: Boolean,
    isDarkMode: Boolean,
    cardBg: Color,
    borderCol: Color,
    textPrimary: Color,
    textSecondary: Color,
    onUpdateStatus: (reportId: String, newStatus: String) -> Unit,
    onBackToReports: () -> Unit
) {
    if (report == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isIndonesian) "Pilih laporan dari tab 'Laporan' untuk melihat detail admin." else "Select a report from 'Reports' tab to view details.",
                    fontSize = 13.5.sp,
                    color = textSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onBackToReports) {
                    Text(if (isIndonesian) "Buka Daftar Laporan" else "Open Reports List")
                }
            }
        }
        return
    }

    var currentStatus by remember(report.status) { mutableStateOf(report.status) }
    var selectedStaffName by remember { mutableStateOf(staffList.firstOrNull()?.name ?: "Tim Fasilitas") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Back Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onBackToReports() },
                shape = RoundedCornerShape(10.dp),
                color = cardBg,
                border = BorderStroke(1.dp, borderCol)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "←", fontSize = 13.sp, color = textPrimary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isIndonesian) "Kembali" else "Back",
                        fontSize = 12.sp,
                        color = textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = if (isIndonesian) "Kelola Laporan ID #${report.id?.takeLast(5) ?: "TRAC"}" else "Manage Report #${report.id?.takeLast(5) ?: "TRAC"}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status Changer Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = cardBg,
            border = BorderStroke(1.dp, borderCol)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isIndonesian) "Ubah Status Pengerjaan (Admin Control):" else "Change Progress Status (Admin Control):",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = textSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val statuses = listOf("Pending", "In Progress", "Completed")
                    statuses.forEach { st ->
                        val isCurrent = currentStatus.equals(st, ignoreCase = true)
                        val btnBg = when (st) {
                            "Pending" -> if (isCurrent) Color(0xFFD97706) else Color(0xFFFEF3C7)
                            "In Progress" -> if (isCurrent) Color(0xFF2563EB) else Color(0xFFEFF6FF)
                            else -> if (isCurrent) Color(0xFF10B981) else Color(0xFFDCFCE7)
                        }
                        val btnText = if (isCurrent) Color.White else when (st) {
                            "Pending" -> Color(0xFFB45309)
                            "In Progress" -> Color(0xFF1D4ED8)
                            else -> Color(0xFF15803D)
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    currentStatus = st
                                    report.id?.let { onUpdateStatus(it, st) }
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = btnBg
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (st) {
                                        "Pending" -> if (isIndonesian) "Menunggu" else "Pending"
                                        "In Progress" -> if (isIndonesian) "Diproses" else "In Progress"
                                        else -> if (isIndonesian) "Selesai" else "Resolved"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = btnText
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Report Information Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = cardBg,
            border = BorderStroke(1.dp, borderCol)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = report.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryPill(category = report.category)
                    Spacer(modifier = Modifier.width(8.dp))
                    LocationPill(location = report.location)
                }

                Spacer(modifier = Modifier.height(14.dp))

                HorizontalDivider(color = borderCol)

                Spacer(modifier = Modifier.height(14.dp))

                DetailItemRow(
                    label = if (isIndonesian) "Nama Pelapor" else "Reporter",
                    value = report.userName ?: "Pelapor TRAC",
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )

                DetailItemRow(
                    label = if (isIndonesian) "Waktu Laporan" else "Reported At",
                    value = report.createdAt ?: "Hari ini",
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )

                DetailItemRow(
                    label = if (isIndonesian) "Petugas Ditugaskan" else "Assigned Staff",
                    value = selectedStaffName,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isIndonesian) "Deskripsi Kerusakan:" else "Issue Description:",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = textSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = report.description,
                    fontSize = 13.5.sp,
                    color = textPrimary,
                    lineHeight = 20.sp
                )

                // Render Image if available
                if (!report.imageUrl.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (isIndonesian) "Foto Bukti Fasilitas:" else "Attached Photo Evidence:",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val imageBitmap = remember(report.imageUrl) {
                        ImageUtils.base64ToBitmap(report.imageUrl)
                    }
                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap.asImageBitmap(),
                            contentDescription = "Report Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Staff Assignment Quick Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = cardBg,
            border = BorderStroke(1.dp, borderCol)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isIndonesian) "Tugaskan Teknisi Spesialis:" else "Assign Specialist Technician:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                staffList.forEach { staff ->
                    val isAssigned = selectedStaffName == staff.name
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedStaffName = staff.name }
                            .padding(vertical = 8.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = staff.name,
                                fontSize = 13.sp,
                                fontWeight = if (isAssigned) FontWeight.Bold else FontWeight.Medium,
                                color = if (isAssigned) Color(0xFF2563EB) else textPrimary
                            )
                            Text(
                                text = staff.role,
                                fontSize = 11.sp,
                                color = textSecondary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isAssigned) Color(0xFF2563EB) else if (isDarkMode) Color(0xFF334155) else Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = if (isAssigned) "Ditugaskan ✓" else "Pilih",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAssigned) Color.White else textSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 6. TAB 4: ASSIGN STAFF
// -------------------------------------------------------------
@Composable
private fun AdminAssignStaffView(
    staffList: List<StaffMember>,
    reportsList: List<ReportData>,
    isIndonesian: Boolean,
    isDarkMode: Boolean,
    cardBg: Color,
    borderCol: Color,
    textPrimary: Color,
    textSecondary: Color,
    onAssignStaff: (staffId: String, reportId: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = if (isIndonesian) "Manajemen Staf & Teknisi Fasilitas" else "Facility Staff & Technician Management",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (isIndonesian)
                "Daftar teknisi yang bertanggung jawab menangani pemeliharaan sarana prasarana sekolah."
            else
                "List of technicians responsible for maintaining school facilities and equipment.",
            fontSize = 12.sp,
            color = textSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        staffList.forEach { staff ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(14.dp),
                color = cardBg,
                border = BorderStroke(1.dp, borderCol)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    color = if (staff.isAvailable) Color(0xFFEFF6FF) else Color(0xFFFEF3C7),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            StaffWrenchIcon(
                                color = if (staff.isAvailable) Color(0xFF2563EB) else Color(0xFFD97706),
                                size = 22.dp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = staff.name,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = staff.role,
                                fontSize = 11.5.sp,
                                color = textSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (staff.isAvailable) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                                ) {
                                    Text(
                                        text = if (staff.isAvailable) (if (isIndonesian) "Siap Bertugas" else "Available") else (if (isIndonesian) "Sedang Bertugas" else "Busy"),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (staff.isAvailable) Color(0xFF15803D) else Color(0xFFB45309),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${staff.activeTasks} " + if (isIndonesian) "tugas aktif" else "tasks",
                                    fontSize = 11.sp,
                                    color = textSecondary
                                )
                            }
                        }
                    }

                    // Contact / Phone action
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF2563EB)
                    ) {
                        Text(
                            text = "WhatsApp",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 7. TAB 5: USERS PELAPOR (STUDENT REPORTERS & ADMIN MANAGER)
// -------------------------------------------------------------
@Composable
private fun AdminUsersPelaporView(
    students: List<StudentReporter>,
    isSuperAdmin: Boolean,
    adminEmails: Set<String>,
    isIndonesian: Boolean,
    isDarkMode: Boolean,
    cardBg: Color,
    borderCol: Color,
    textPrimary: Color,
    textSecondary: Color,
    onToggleAdmin: (email: String, shouldBeAdmin: Boolean) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    val filteredStudents = remember(students, query) {
        if (query.isBlank()) students
        else students.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.className.contains(query, ignoreCase = true) ||
                    it.email.contains(query, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (isIndonesian) "Direktori Pengguna & Hak Akses Admin" else "Users Directory & Admin Permissions",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (isIndonesian)
                "Daftar akun terdaftar. Admin Utama (rompisjosh@gmail.com) memiliki wewenang mengangkat atau mencabut hak admin."
            else
                "List of registered users. Super Admin (rompisjosh@gmail.com) has authority to grant or revoke admin privileges.",
            fontSize = 12.sp,
            color = textSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Super Admin Authority Notice Banner
        if (isSuperAdmin) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEFF6FF),
                border = BorderStroke(1.dp, Color(0xFF2563EB).copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF2563EB), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AdminShieldCrownIcon(color = Color.White, size = 16.dp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = if (isIndonesian) "Wewenang Admin Utama Aktif" else "Super Admin Authority Active",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB)
                        )
                        Text(
                            text = if (isIndonesian)
                                "Anda dapat menekan tombol '+ Jadikan Admin' atau 'Cabut Admin' pada tiap pengguna."
                            else
                                "You can tap '+ Grant Admin' or 'Revoke Admin' for any user.",
                            fontSize = 11.sp,
                            color = textSecondary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Search Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = cardBg,
            border = BorderStroke(1.dp, borderCol)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchVectorIcon(color = textSecondary, size = 18.dp)
                Spacer(modifier = Modifier.width(10.dp))
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    textStyle = TextStyle(fontSize = 13.5.sp, color = textPrimary),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        if (query.isEmpty()) {
                            Text(
                                text = if (isIndonesian) "Cari pelapor atau email..." else "Search reporter or email...",
                                fontSize = 13.5.sp,
                                color = textSecondary
                            )
                        }
                        inner()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredStudents, key = { it.id }) { student ->
                val cleanEmail = student.email.trim().lowercase()
                val isUserSuperAdmin = cleanEmail == SessionPreferences.SUPER_ADMIN_EMAIL
                val isUserAdmin = isUserSuperAdmin || adminEmails.contains(cleanEmail)

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = cardBg,
                    border = BorderStroke(1.dp, if (isUserAdmin) Color(0xFF2563EB).copy(alpha = 0.4f) else borderCol)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(
                                            if (isUserAdmin) Color(0xFFEFF6FF) else Color(0xFFF1F5F9),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = student.name.take(2).uppercase(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUserAdmin) Color(0xFF2563EB) else textSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = student.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = when {
                                                isUserSuperAdmin -> Color(0xFF2563EB)
                                                isUserAdmin -> Color(0xFF3B82F6)
                                                else -> Color(0xFF64748B)
                                            }
                                        ) {
                                            Text(
                                                text = when {
                                                    isUserSuperAdmin -> "ADMIN UTAMA"
                                                    isUserAdmin -> "ADMIN"
                                                    else -> "PELAPOR"
                                                },
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${student.className} • ${student.email}",
                                        fontSize = 11.5.sp,
                                        color = textSecondary
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Text(
                                        text = "${student.totalReports} " + if (isIndonesian) "Laporan" else "Reports",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        // Super Admin Promotion / Demotion Action Buttons
                        if (isSuperAdmin && !isUserSuperAdmin) {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = borderCol)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (isUserAdmin) {
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onToggleAdmin(student.email, false) },
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFFEF2F2),
                                        border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                                    ) {
                                        Text(
                                            text = if (isIndonesian) "Cabut Hak Admin" else "Revoke Admin",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFEF4444),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                } else {
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onToggleAdmin(student.email, true) },
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF2563EB)
                                    ) {
                                        Text(
                                            text = if (isIndonesian) "+ Jadikan Admin" else "+ Grant Admin",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 8. TAB 6: CATEGORIES & LOCATIONS
// -------------------------------------------------------------
@Composable
private fun AdminCategoriesLocationsView(
    locations: List<FacilityLocation>,
    reportsList: List<ReportData>,
    isIndonesian: Boolean,
    isDarkMode: Boolean,
    cardBg: Color,
    borderCol: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = if (isIndonesian) "Master Data Kategori & 4 Lantai Gedung" else "Facility Categories & 4 Building Floors",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (isIndonesian)
                "Struktur pembagian denah gedung sekolah dan sebaran kategori kerusakan fasilitas."
            else
                "School building layout breakdown and facility damage category distribution.",
            fontSize = 12.sp,
            color = textSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Categories Overview
        Text(
            text = if (isIndonesian) "Kategori Sarana & Prasarana" else "Facility Categories",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        val categories = listOf(
            "Kelistrikan" to Color(0xFFF59E0B),
            "AC & Elektronik" to Color(0xFF3B82F6),
            "Sanitasi & Air" to Color(0xFF06B6D4),
            "Perabot & Sipil" to Color(0xFF8B5CF6),
            "Jaringan & IT" to Color(0xFF10B981)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { (catName, catColor) ->
                val count = reportsList.count { it.category.contains(catName.substringBefore(" "), ignoreCase = true) }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = cardBg,
                    border = BorderStroke(1.dp, borderCol)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(catColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = catName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = "$count " + if (isIndonesian) "laporan" else "reports",
                            fontSize = 11.sp,
                            color = textSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Floor Breakdowns (4 Floors)
        Text(
            text = if (isIndonesian) "Sebaran Lokasi Per Lantai Gedung" else "Locations Breakdown By Floor",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        locations.forEach { loc ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(14.dp),
                color = cardBg,
                border = BorderStroke(1.dp, borderCol)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                LocationBuildingIcon(color = Color(0xFF2563EB), size = 20.dp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = loc.floorName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                                Text(
                                    text = loc.floorDesc,
                                    fontSize = 11.5.sp,
                                    color = textSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFEFF6FF)
                        ) {
                            Text(
                                text = "${loc.roomCount} " + if (isIndonesian) "Ruangan" else "Rooms",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isIndonesian) "Daftar Ruangan Terdaftar:" else "Registered Rooms:",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        loc.rooms.forEach { room ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isDarkMode) Color(0xFF334155) else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = room,
                                    fontSize = 11.sp,
                                    color = textPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 9. TAB 7: ADMIN NOTIFICATIONS
// -------------------------------------------------------------
@Composable
private fun AdminNotifView(
    notifications: List<AdminNotifItem>,
    isIndonesian: Boolean,
    isDarkMode: Boolean,
    cardBg: Color,
    borderCol: Color,
    textPrimary: Color,
    textSecondary: Color,
    onMarkAllRead: () -> Unit,
    onNotificationClick: (AdminNotifItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = if (isIndonesian) "Pusat Peringatan & Notifikasi Admin" else "Admin Alert & Notification Center",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Text(
                    text = if (isIndonesian) "Pemberitahuan darurat dan status pengerjaan fasilitas." else "Urgent reports and repair status alerts.",
                    fontSize = 12.sp,
                    color = textSecondary
                )
            }

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onMarkAllRead() },
                shape = RoundedCornerShape(8.dp),
                color = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEFF6FF)
            ) {
                Text(
                    text = if (isIndonesian) "Tandai Dibaca" else "Mark All Read",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2563EB),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(notifications, key = { it.id }) { notif ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onNotificationClick(notif) },
                    shape = RoundedCornerShape(14.dp),
                    color = if (!notif.isRead) (if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEFF6FF)) else cardBg,
                    border = BorderStroke(1.dp, if (!notif.isRead) Color(0xFF2563EB).copy(alpha = 0.4f) else borderCol)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    color = if (notif.isUrgent) Color(0xFFFEE2E2) else Color(0xFFEFF6FF),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            AdminBellIcon(
                                color = if (notif.isUrgent) Color(0xFFEF4444) else Color(0xFF2563EB),
                                size = 20.dp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = notif.title,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = notif.timeAgo,
                                    fontSize = 11.sp,
                                    color = textSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = notif.desc,
                                fontSize = 12.sp,
                                color = textSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// HELPER COMPOSABLES & CARDS
// -------------------------------------------------------------
@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    cardColor: Color,
    accentColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = cardColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
private fun AdminActionButton(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun AdminCompactReportCard(
    report: ReportData,
    cardBg: Color,
    borderCol: Color,
    textPrimary: Color,
    textSecondary: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = cardBg,
        border = BorderStroke(1.dp, borderCol)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = report.title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${report.location} • ${report.userName ?: "Pelapor"}",
                    fontSize = 11.5.sp,
                    color = textSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            AdminStatusBadge(status = report.status)
        }
    }
}

@Composable
private fun AdminReportManageCard(
    report: ReportData,
    isIndonesian: Boolean,
    isDarkMode: Boolean,
    cardBg: Color,
    borderCol: Color,
    textPrimary: Color,
    textSecondary: Color,
    onUpdateStatus: (String) -> Unit,
    onOpenDetail: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = cardBg,
        border = BorderStroke(1.dp, borderCol)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = report.title,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                AdminStatusBadge(status = report.status)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${report.location} • Ditulis oleh ${report.userName ?: "Pelapor"}",
                fontSize = 12.sp,
                color = textSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = report.description,
                fontSize = 12.5.sp,
                color = textPrimary,
                maxLines = 2,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Row: Quick Status Updater and Detail Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Quick status change pills
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (!report.status.equals("In Progress", ignoreCase = true)) {
                        StatusActionButton(
                            label = if (isIndonesian) "→ Diproses" else "→ In Progress",
                            color = Color(0xFF2563EB),
                            onClick = { onUpdateStatus("In Progress") }
                        )
                    }

                    if (!report.status.equals("Completed", ignoreCase = true)) {
                        StatusActionButton(
                            label = if (isIndonesian) "✓ Selesai" else "✓ Resolved",
                            color = Color(0xFF16A34A),
                            onClick = { onUpdateStatus("Completed") }
                        )
                    }

                    if (report.status.equals("Completed", ignoreCase = true) || report.status.equals("In Progress", ignoreCase = true)) {
                        StatusActionButton(
                            label = if (isIndonesian) "↺ Pending" else "↺ Pending",
                            color = Color(0xFFD97706),
                            onClick = { onUpdateStatus("Pending") }
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onOpenDetail() },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDarkMode) Color(0xFF334155) else Color(0xFFEFF6FF)
                ) {
                    Text(
                        text = if (isIndonesian) "Detail →" else "Detail →",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusActionButton(
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun AdminStatusBadge(status: String) {
    val (bg, textColor, label) = when {
        status.equals("Pending", ignoreCase = true) -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "Pending")
        status.equals("In Progress", ignoreCase = true) -> Triple(Color(0xFFEFF6FF), Color(0xFF1D4ED8), "Diproses")
        else -> Triple(Color(0xFFDCFCE7), Color(0xFF15803D), "Selesai")
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bg
    ) {
        Text(
            text = label,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun CategoryPill(category: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFFF1F5F9)
    ) {
        Text(
            text = category,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF475569),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun LocationPill(location: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFFEFF6FF)
    ) {
        Text(
            text = "📍 $location",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2563EB),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun DetailItemRow(
    label: String,
    value: String,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.5.sp, color = textSecondary)
        Text(text = value, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = textPrimary)
    }
}

// -------------------------------------------------------------
// CANVAS DRAWN ICONS MATCHING TRAC DESIGN SYSTEM
// -------------------------------------------------------------
@Composable
private fun AdminTabIcon(tab: AdminSubTab, color: Color, size: androidx.compose.ui.unit.Dp = 16.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        when (tab) {
            AdminSubTab.DASHBOARD -> {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(w * 0.1f, h * 0.1f),
                    size = Size(w * 0.35f, h * 0.35f),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
                drawRoundRect(
                    color = color,
                    topLeft = Offset(w * 0.55f, h * 0.1f),
                    size = Size(w * 0.35f, h * 0.35f),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
                drawRoundRect(
                    color = color,
                    topLeft = Offset(w * 0.1f, h * 0.55f),
                    size = Size(w * 0.35f, h * 0.35f),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
                drawRoundRect(
                    color = color,
                    topLeft = Offset(w * 0.55f, h * 0.55f),
                    size = Size(w * 0.35f, h * 0.35f),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
            }
            AdminSubTab.REPORTS -> {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(w * 0.2f, h * 0.15f),
                    size = Size(w * 0.6f, h * 0.7f),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                    style = Stroke(width = 1.5.dp.toPx())
                )
                drawLine(color = color, start = Offset(w * 0.35f, h * 0.35f), end = Offset(w * 0.65f, h * 0.35f), strokeWidth = 1.5.dp.toPx())
                drawLine(color = color, start = Offset(w * 0.35f, h * 0.5f), end = Offset(w * 0.65f, h * 0.5f), strokeWidth = 1.5.dp.toPx())
                drawLine(color = color, start = Offset(w * 0.35f, h * 0.65f), end = Offset(w * 0.55f, h * 0.65f), strokeWidth = 1.5.dp.toPx())
            }
            AdminSubTab.REPORT_DETAIL -> {
                drawCircle(color = color, radius = w * 0.4f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = 1.5.dp.toPx()))
                drawLine(color = color, start = Offset(w * 0.5f, h * 0.3f), end = Offset(w * 0.5f, h * 0.55f), strokeWidth = 1.8.dp.toPx(), cap = StrokeCap.Round)
                drawCircle(color = color, radius = 1.2.dp.toPx(), center = Offset(w * 0.5f, h * 0.7f))
            }
            AdminSubTab.ASSIGN_STAFF -> {
                val path = Path().apply {
                    moveTo(w * 0.25f, h * 0.75f)
                    lineTo(w * 0.65f, h * 0.35f)
                }
                drawPath(path, color = color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
                drawCircle(color = color, radius = 2.5.dp.toPx(), center = Offset(w * 0.75f, h * 0.25f), style = Stroke(width = 1.5.dp.toPx()))
            }
            AdminSubTab.USERS_PELAPOR -> {
                drawCircle(color = color, radius = 2.5.dp.toPx(), center = Offset(w * 0.35f, h * 0.35f), style = Stroke(width = 1.5.dp.toPx()))
                drawCircle(color = color, radius = 2.5.dp.toPx(), center = Offset(w * 0.65f, h * 0.35f), style = Stroke(width = 1.5.dp.toPx()))
                drawLine(color = color, start = Offset(w * 0.2f, h * 0.75f), end = Offset(w * 0.5f, h * 0.75f), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
                drawLine(color = color, start = Offset(w * 0.5f, h * 0.75f), end = Offset(w * 0.8f, h * 0.75f), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
            }
            AdminSubTab.CATEGORIES_LOCATIONS -> {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(w * 0.25f, h * 0.25f),
                    size = Size(w * 0.5f, h * 0.65f),
                    style = Stroke(width = 1.5.dp.toPx())
                )
                drawLine(color = color, start = Offset(w * 0.4f, h * 0.4f), end = Offset(w * 0.4f, h * 0.5f), strokeWidth = 1.5.dp.toPx())
                drawLine(color = color, start = Offset(w * 0.6f, h * 0.4f), end = Offset(w * 0.6f, h * 0.5f), strokeWidth = 1.5.dp.toPx())
            }
            AdminSubTab.ADMIN_NOTIF -> {
                drawCircle(color = color, radius = 1.5.dp.toPx(), center = Offset(w * 0.5f, h * 0.2f))
                val bell = Path().apply {
                    moveTo(w * 0.3f, h * 0.7f)
                    lineTo(w * 0.7f, h * 0.7f)
                    lineTo(w * 0.65f, h * 0.35f)
                    lineTo(w * 0.35f, h * 0.35f)
                    close()
                }
                drawPath(bell, color = color, style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawLine(color = color, start = Offset(w * 0.45f, h * 0.82f), end = Offset(w * 0.55f, h * 0.82f), strokeWidth = 1.5.dp.toPx())
            }
        }
    }
}

@Composable
private fun AdminShieldCrownIcon(color: Color, size: androidx.compose.ui.unit.Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        val shieldPath = Path().apply {
            moveTo(w * 0.5f, h * 0.10f)
            lineTo(w * 0.88f, h * 0.26f)
            lineTo(w * 0.88f, h * 0.58f)
            cubicTo(w * 0.88f, h * 0.82f, w * 0.5f, h * 0.94f, w * 0.5f, h * 0.94f)
            cubicTo(w * 0.5f, h * 0.94f, w * 0.12f, h * 0.82f, w * 0.12f, h * 0.58f)
            lineTo(w * 0.12f, h * 0.26f)
            close()
        }
        drawPath(shieldPath, color = color, style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(color = color, radius = 2.5.dp.toPx(), center = Offset(w * 0.5f, h * 0.52f))
    }
}

@Composable
private fun StaffWrenchIcon(color: Color, size: androidx.compose.ui.unit.Dp = 20.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        val path = Path().apply {
            moveTo(w * 0.2f, h * 0.8f)
            lineTo(w * 0.65f, h * 0.35f)
        }
        drawPath(path, color = color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
        drawCircle(color = color, radius = 3.dp.toPx(), center = Offset(w * 0.72f, h * 0.28f), style = Stroke(width = 2.dp.toPx()))
    }
}

@Composable
private fun LocationBuildingIcon(color: Color, size: androidx.compose.ui.unit.Dp = 20.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.25f, h * 0.2f),
            size = Size(w * 0.5f, h * 0.7f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
            style = Stroke(width = 1.8.dp.toPx())
        )
        drawLine(color = color, start = Offset(w * 0.4f, h * 0.38f), end = Offset(w * 0.4f, h * 0.48f), strokeWidth = 1.8.dp.toPx(), cap = StrokeCap.Round)
        drawLine(color = color, start = Offset(w * 0.6f, h * 0.38f), end = Offset(w * 0.6f, h * 0.48f), strokeWidth = 1.8.dp.toPx(), cap = StrokeCap.Round)
        drawLine(color = color, start = Offset(w * 0.4f, h * 0.6f), end = Offset(w * 0.4f, h * 0.7f), strokeWidth = 1.8.dp.toPx(), cap = StrokeCap.Round)
        drawLine(color = color, start = Offset(w * 0.6f, h * 0.6f), end = Offset(w * 0.6f, h * 0.7f), strokeWidth = 1.8.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
private fun AdminBellIcon(color: Color, size: androidx.compose.ui.unit.Dp = 20.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        drawCircle(color = color, radius = 1.5.dp.toPx(), center = Offset(w * 0.5f, h * 0.18f))
        val bell = Path().apply {
            moveTo(w * 0.28f, h * 0.72f)
            lineTo(w * 0.72f, h * 0.72f)
            lineTo(w * 0.65f, h * 0.34f)
            lineTo(w * 0.35f, h * 0.34f)
            close()
        }
        drawPath(bell, color = color, style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(color = color, start = Offset(w * 0.44f, h * 0.84f), end = Offset(w * 0.56f, h * 0.84f), strokeWidth = 1.8.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
private fun SearchVectorIcon(color: Color, size: androidx.compose.ui.unit.Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        drawCircle(color = color, radius = 5.dp.toPx(), center = Offset(w * 0.42f, h * 0.42f), style = Stroke(width = 1.8.dp.toPx()))
        drawLine(color = color, start = Offset(w * 0.65f, h * 0.65f), end = Offset(w * 0.88f, h * 0.88f), strokeWidth = 1.8.dp.toPx(), cap = StrokeCap.Round)
    }
}
