package com.example.trac

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trac.data.ReportData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ReportListTracScreen(
    reportsList: List<ReportData> = emptyList(),
    currentUserId: String = "",
    currentUserName: String = "",
    isIndonesian: Boolean = false,
    isDarkMode: Boolean = false,
    onBackClick: () -> Unit = {},
    onHomeTabClick: () -> Unit = {},
    onCreateReportClick: () -> Unit = {},
    onReportItemClick: (ReportData) -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val isPreview = LocalInspectionMode.current

    // Dynamic Theme Colors
    val pageBg = if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF8FAFD)
    val cardBg = if (isDarkMode) Color(0xFF1E293B) else Color.White
    val textPrimary = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
    val borderCol = if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf("All") }
    var reportScope by rememberSaveable { mutableStateOf("ALL") }

    val filterOptions = if (isIndonesian) listOf("Semua", "Menunggu", "Proses", "Selesai") else listOf("All", "Pending", "In Progress", "Completed")

    // Real-Time Query & Scope Filtering
    val filteredReports = remember(reportsList, searchQuery, selectedFilter, reportScope, currentUserId, currentUserName) {
        reportsList.filter { report ->
            val matchesScope = if (reportScope == "MY_REPORTS") {
                (currentUserId.isNotBlank() && report.userId == currentUserId) ||
                (currentUserName.isNotBlank() && report.userName.equals(currentUserName, ignoreCase = true))
            } else true

            val matchesQuery = searchQuery.isBlank() ||
                    report.title.contains(searchQuery, ignoreCase = true) ||
                    report.location.contains(searchQuery, ignoreCase = true) ||
                    report.category.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Pending", "Menunggu" -> report.status.equals("Pending", ignoreCase = true)
                "In Progress", "Proses" -> report.status.equals("In Progress", ignoreCase = true)
                "Completed", "Selesai" -> report.status.equals("Completed", ignoreCase = true)
                else -> true
            }

            matchesScope && matchesQuery && matchesFilter
        }.sortedWith(
            compareByDescending<ReportData> { it.createdAt ?: "" }
                .thenByDescending { it.id ?: "" }
        )
    }

    // Entrance animation states
    val headerAlpha = remember { Animatable(if (isPreview) 1f else 0f) }
    val headerOffsetY = remember { Animatable(if (isPreview) 0f else -30f) }

    val contentAlpha = remember { Animatable(if (isPreview) 1f else 0f) }
    val contentOffsetY = remember { Animatable(if (isPreview) 0f else 40f) }

    LaunchedEffect(isPreview) {
        if (!isPreview) {
            launch {
                headerAlpha.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
            }
            launch {
                headerOffsetY.animateTo(
                    0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }

            delay(100)
            launch {
                contentAlpha.animateTo(1f, animationSpec = tween(450))
            }
            launch {
                contentOffsetY.animateTo(
                    0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(pageBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 76.dp)
                .padding(horizontal = 22.dp, vertical = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // 1. Top Header Bar: Back Circle Arrow + Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = headerAlpha.value
                        translationY = headerOffsetY.value.dp.toPx()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .clickable { onBackClick() }
                        .shadow(
                            elevation = 6.dp,
                            shape = CircleShape,
                            spotColor = if (isDarkMode) Color(0x30000000) else Color(0x1A000000)
                        ),
                    shape = CircleShape,
                    color = cardBg
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ChevronLeftIcon(color = textPrimary)
                    }
                }

                Spacer(modifier = Modifier.width(28.dp))

                Text(
                    text = if (isIndonesian) "Daftar Laporan" else "Report List",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textPrimary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Animated Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = contentAlpha.value
                        translationY = contentOffsetY.value.dp.toPx()
                    }
            ) {
                // 2. Search Input Bar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(18.dp),
                            spotColor = Color(0x0D000000)
                        ),
                    shape = RoundedCornerShape(18.dp),
                    color = cardBg,
                    border = BorderStroke(1.dp, borderCol)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SearchIcon(color = textSecondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = if (isIndonesian) "Cari laporan fasilitas..." else "Search facility reports...",
                                    fontSize = 14.sp,
                                    color = textSecondary,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = textPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2.5 Scope Toggle: Semua Laporan vs Laporan Saya
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(4.dp)
                ) {
                    val isAll = reportScope == "ALL"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(11.dp))
                            .background(if (isAll) Color(0xFF2563EB) else Color.Transparent)
                            .clickable { reportScope = "ALL" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isIndonesian) "Semua Laporan" else "All Reports",
                            fontSize = 12.5.sp,
                            fontWeight = if (isAll) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = if (isAll) Color.White else textSecondary
                        )
                    }

                    val isMine = reportScope == "MY_REPORTS"
                    val myCount = reportsList.count {
                        (currentUserId.isNotBlank() && it.userId == currentUserId) ||
                        (currentUserName.isNotBlank() && it.userName.equals(currentUserName, ignoreCase = true))
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(11.dp))
                            .background(if (isMine) Color(0xFF2563EB) else Color.Transparent)
                            .clickable { reportScope = "MY_REPORTS" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isIndonesian) "Laporan Saya ($myCount)" else "My Reports ($myCount)",
                            fontSize = 12.5.sp,
                            fontWeight = if (isMine) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = if (isMine) Color.White else textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Filter Category Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(filterOptions) { filter ->
                        val isSelected = filter == selectedFilter || (selectedFilter == "All" && filter == "Semua")
                        val chipBg = if (isSelected) Color(0xFF2563EB) else cardBg
                        val chipTextColor = if (isSelected) Color.White else textSecondary

                        Surface(
                            modifier = Modifier
                                .clickable { selectedFilter = filter }
                                .shadow(
                                    elevation = if (isSelected) 6.dp else 2.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    spotColor = if (isSelected) Color(0x352563EB) else Color(0x0A000000)
                                ),
                            shape = RoundedCornerShape(20.dp),
                            color = chipBg,
                            border = if (isSelected) null else BorderStroke(1.dp, borderCol)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = filter,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = chipTextColor
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 4. Recycled Reports List Items using LazyColumn
                if (filteredReports.isEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(18.dp),
                                spotColor = Color(0x0D000000)
                            ),
                        shape = RoundedCornerShape(18.dp),
                        color = cardBg,
                        border = BorderStroke(1.dp, borderCol)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 36.dp, horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isIndonesian) "Belum ada laporan yang sesuai." else "No matching reports found.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = textSecondary
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 88.dp)
                    ) {
                        items(
                            items = filteredReports,
                            key = { it.id ?: (it.title + it.location + it.createdAt) }
                        ) { report ->
                            val (statusBg, statusColor, iconType) = when (report.status.lowercase()) {
                                "completed" -> Triple(Color(0xFFD1FAE5), Color(0xFF059669), ReportIconType.SUCCESS)
                                "in progress" -> Triple(Color(0xFFFEF3C7), Color(0xFFD97706), ReportIconType.WARNING)
                                else -> Triple(Color(0xFFF1F5F9), Color(0xFF64748B), ReportIconType.INFO)
                            }

                            val statusDisplay = when {
                                report.status.equals("In Progress", ignoreCase = true) -> if (isIndonesian) "Proses" else "In Progress"
                                report.status.equals("Completed", ignoreCase = true) -> if (isIndonesian) "Selesai" else "Completed"
                                else -> if (isIndonesian) "Menunggu" else "Pending"
                            }

                            ReportCardItem(
                                title = report.title,
                                location = report.location,
                                timeAgo = report.createdAt?.take(10) ?: "Baru saja",
                                priority = report.priority,
                                upvoteCount = report.upvoteCount,
                                statusText = statusDisplay,
                                statusBg = statusBg,
                                statusColor = statusColor,
                                iconType = iconType,
                                isIndonesian = isIndonesian,
                                cardBg = cardBg,
                                borderCol = borderCol,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary,
                                onClick = { onReportItemClick(report) }
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class ReportIconType {
    WARNING,
    INFO,
    SUCCESS
}

@Composable
private fun ReportCardItem(
    title: String,
    location: String,
    timeAgo: String,
    priority: String,
    upvoteCount: Int,
    statusText: String,
    statusBg: Color,
    statusColor: Color,
    iconType: ReportIconType,
    isIndonesian: Boolean,
    cardBg: Color,
    borderCol: Color,
    textPrimary: Color,
    textSecondary: Color,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = Color(0x0D000000),
                ambientColor = Color(0x05000000)
            ),
        shape = RoundedCornerShape(18.dp),
        color = cardBg,
        border = BorderStroke(1.dp, borderCol)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                val iconBg = when (iconType) {
                    ReportIconType.WARNING -> Color(0xFFFEF3C7)
                    ReportIconType.INFO -> Color(0xFFE0F2FE)
                    ReportIconType.SUCCESS -> Color(0xFFD1FAE5)
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(iconBg, shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    ReportStatusIcon(type = iconType)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (priority.equals("Darurat", ignoreCase = true) || priority.equals("High", ignoreCase = true)) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFEE2E2)
                            ) {
                                Text(
                                    text = if (isIndonesian) "DARURAT" else "EMERGENCY",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFDC2626),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        } else if (priority.equals("Rendah", ignoreCase = true) || priority.equals("Low", ignoreCase = true)) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFDCFCE7)
                            ) {
                                Text(
                                    text = if (isIndonesian) "RENDAH" else "LOW",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF16A34A),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$location • $timeAgo",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (upvoteCount > 0) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "👍 $upvoteCount",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Status Badge Pill
            Box(
                modifier = Modifier
                    .background(statusBg, shape = RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = statusText,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
        }
    }
}

@Composable
private fun ReportStatusIcon(type: ReportIconType) {
    Canvas(modifier = Modifier.size(20.dp)) {
        val w = size.width
        val h = size.height

        when (type) {
            ReportIconType.WARNING -> {
                val color = Color(0xFFD97706)
                drawCircle(color = color, radius = w * 0.42f, style = Stroke(width = 2.dp.toPx()))
                drawLine(
                    color = color,
                    start = Offset(w * 0.5f, h * 0.28f),
                    end = Offset(w * 0.5f, h * 0.58f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawCircle(color = color, radius = 1.2.dp.toPx(), center = Offset(w * 0.5f, h * 0.72f))
            }
            ReportIconType.INFO -> {
                val color = Color(0xFF0284C7)
                drawCircle(color = color, radius = w * 0.42f, style = Stroke(width = 2.dp.toPx()))
                drawCircle(color = color, radius = 1.2.dp.toPx(), center = Offset(w * 0.5f, h * 0.3f))
                drawLine(
                    color = color,
                    start = Offset(w * 0.5f, h * 0.44f),
                    end = Offset(w * 0.5f, h * 0.72f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            ReportIconType.SUCCESS -> {
                val color = Color(0xFF059669)
                drawCircle(color = color, radius = w * 0.42f, style = Stroke(width = 2.dp.toPx()))
                val checkPath = Path().apply {
                    moveTo(w * 0.32f, h * 0.52f)
                    lineTo(w * 0.46f, h * 0.66f)
                    lineTo(w * 0.68f, h * 0.38f)
                }
                drawPath(
                    path = checkPath,
                    color = color,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}

@Composable
private fun SearchIcon(color: Color) {
    Canvas(modifier = Modifier.size(18.dp)) {
        val w = size.width
        val h = size.height

        drawCircle(
            color = color,
            radius = w * 0.32f,
            center = Offset(w * 0.4f, h * 0.4f),
            style = Stroke(width = 2.dp.toPx())
        )
        drawLine(
            color = color,
            start = Offset(w * 0.62f, h * 0.62f),
            end = Offset(w * 0.88f, h * 0.88f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ChevronLeftIcon(color: Color) {
    Canvas(modifier = Modifier.size(18.dp)) {
        val w = size.width
        val h = size.height

        val path = Path().apply {
            moveTo(w * 0.65f, h * 0.2f)
            lineTo(w * 0.35f, h * 0.5f)
            lineTo(w * 0.65f, h * 0.8f)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 2.2.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReportListTracScreenPreview() {
    ReportListTracScreen()
}
