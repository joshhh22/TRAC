package com.example.trac.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InAppBanner(
    message: String?,
    isError: Boolean = true,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !message.isNullOrBlank(),
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        if (!message.isNullOrBlank()) {
            val bgColor = if (isError) Color(0xFFFEF2F2) else Color(0xFFF0FDF4)
            val borderColor = if (isError) Color(0xFFFCA5A5) else Color(0xFF86EFAC)
            val contentColor = if (isError) Color(0xFF991B1B) else Color(0xFF166534)
            val iconColor = if (isError) Color(0xFFEF4444) else Color(0xFF22C55E)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(14.dp),
                        spotColor = if (isError) Color(0x20EF4444) else Color(0x2022C55E)
                    )
                    .background(color = bgColor, shape = RoundedCornerShape(14.dp))
                    .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Banner Icon
                Canvas(modifier = Modifier.size(22.dp)) {
                    val w = size.width
                    val h = size.height

                    if (isError) {
                        // Warning Exclamation Circle
                        drawCircle(color = iconColor, radius = w * 0.45f)
                        drawLine(
                            color = Color.White,
                            start = Offset(w * 0.5f, h * 0.28f),
                            end = Offset(w * 0.5f, h * 0.58f),
                            strokeWidth = 2.2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 1.6.dp.toPx(),
                            center = Offset(w * 0.5f, h * 0.72f)
                        )
                    } else {
                        // Success Check Circle
                        drawCircle(color = iconColor, radius = w * 0.45f)
                        val checkPath = Path().apply {
                            moveTo(w * 0.32f, h * 0.50f)
                            lineTo(w * 0.45f, h * 0.63f)
                            lineTo(w * 0.68f, h * 0.38f)
                        }
                        drawPath(
                            path = checkPath,
                            color = Color.White,
                            style = Stroke(
                                width = 2.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = message,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor,
                    lineHeight = 18.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
