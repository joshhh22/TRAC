package com.example.trac.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trac.Screen

@Composable
fun TracBottomNavBar(
    currentScreen: Screen,
    onTabSelected: (Screen) -> Unit,
    onCreateReportClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    isIndonesian: Boolean = false,
    isDarkMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val barBg = if (isDarkMode) Color(0xFF1E293B) else Color.White

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .shadow(
                    elevation = 16.dp,
                    spotColor = Color(0x1F000000),
                    ambientColor = Color(0x0F000000)
                ),
            color = barBg
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    BottomNavItem(
                        label = if (isIndonesian) "Beranda" else "Home",
                        iconType = "Home",
                        isSelected = currentScreen == Screen.HOME,
                        onClick = { onTabSelected(Screen.HOME) }
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    BottomNavItem(
                        label = if (isIndonesian) "Laporan" else "Reports",
                        iconType = "Reports",
                        isSelected = currentScreen == Screen.REPORT_LIST,
                        onClick = { onTabSelected(Screen.REPORT_LIST) }
                    )
                }
                Box(modifier = Modifier.weight(1f)) // Exact 20% width center slot
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    BottomNavItem(
                        label = if (isIndonesian) "Notifikasi" else "Notifications",
                        iconType = "Notifications",
                        isSelected = currentScreen == Screen.NOTIFICATIONS,
                        onClick = { onTabSelected(Screen.NOTIFICATIONS) }
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    BottomNavItem(
                        label = if (isIndonesian) "Profil" else "Profile",
                        iconType = "Profile",
                        isSelected = false,
                        onClick = { onProfileClick() }
                    )
                }
            }
        }

        // Prominent Symmetrical Center Floating FAB (+)
        val isFabActive = currentScreen == Screen.CREATE_REPORT
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(bottom = 10.dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onCreateReportClick() }
                    .shadow(
                        elevation = if (isFabActive) 14.dp else 12.dp,
                        shape = CircleShape,
                        spotColor = Color(0x452563EB),
                        ambientColor = Color(0x202563EB)
                    ),
                shape = CircleShape,
                color = Color(0xFF2563EB)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    PlusIcon(color = Color.White, size = 22.dp)
                }
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    iconType: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF2563EB) else Color(0xFF94A3B8),
        animationSpec = tween(durationMillis = 200),
        label = "TabColorAnimation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Canvas(modifier = Modifier.size(20.dp)) {
            val w = size.width
            val h = size.height

            when (iconType) {
                "Home" -> {
                    val path = Path().apply {
                        moveTo(w * 0.15f, h * 0.9f)
                        lineTo(w * 0.15f, h * 0.45f)
                        lineTo(w * 0.5f, h * 0.12f)
                        lineTo(w * 0.85f, h * 0.45f)
                        lineTo(w * 0.85f, h * 0.9f)
                        close()
                    }
                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(
                            width = 2.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
                "Reports" -> {
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(w * 0.2f, h * 0.15f),
                        size = Size(w * 0.6f, h * 0.7f),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawLine(
                        color = color,
                        start = Offset(w * 0.35f, h * 0.4f),
                        end = Offset(w * 0.65f, h * 0.4f),
                        strokeWidth = 2.dp.toPx()
                    )
                    drawLine(
                        color = color,
                        start = Offset(w * 0.35f, h * 0.6f),
                        end = Offset(w * 0.65f, h * 0.6f),
                        strokeWidth = 2.dp.toPx()
                    )
                }
                "Notifications" -> {
                    val bellPath = Path().apply {
                        moveTo(w * 0.5f, h * 0.12f)
                        cubicTo(w * 0.3f, h * 0.12f, w * 0.2f, h * 0.3f, w * 0.2f, h * 0.58f)
                        lineTo(w * 0.12f, h * 0.72f)
                        lineTo(w * 0.88f, h * 0.72f)
                        lineTo(w * 0.8f, h * 0.58f)
                        cubicTo(w * 0.8f, h * 0.3f, w * 0.7f, h * 0.12f, w * 0.5f, h * 0.12f)
                        close()
                    }
                    drawPath(
                        path = bellPath,
                        color = color,
                        style = Stroke(
                            width = 2.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
                else -> { // Profile
                    drawCircle(
                        color = color,
                        radius = 4.dp.toPx(),
                        center = Offset(w * 0.5f, h * 0.32f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    val bodyPath = Path().apply {
                        moveTo(w * 0.2f, h * 0.85f)
                        cubicTo(w * 0.2f, h * 0.6f, w * 0.8f, h * 0.6f, w * 0.8f, h * 0.85f)
                    }
                    drawPath(
                        path = bodyPath,
                        color = color,
                        style = Stroke(
                            width = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = color,
            maxLines = 1,
            softWrap = false,
            letterSpacing = (-0.2).sp
        )
    }
}

@Composable
private fun PlusIcon(color: Color, size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        drawLine(
            color = color,
            start = Offset(w * 0.5f, h * 0.2f),
            end = Offset(w * 0.5f, h * 0.8f),
            strokeWidth = 2.4.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.2f, h * 0.5f),
            end = Offset(w * 0.8f, h * 0.5f),
            strokeWidth = 2.4.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
