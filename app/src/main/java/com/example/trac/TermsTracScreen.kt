package com.example.trac

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class TermItemData(
    val number: String,
    val title: String,
    val description: String
)

@Composable
fun TermsTracScreen(
    onBackClick: () -> Unit = {},
    onAcceptClick: () -> Unit = {}
) {
    val isPreview = LocalInspectionMode.current

    val termsList = remember {
        listOf(
            TermItemData(
                number = "01",
                title = "Acceptance of Terms",
                description = "By registering an account or accessing the TRAC platform, you agree to be bound by these Terms and Conditions and our Privacy Policy. If you do not agree, please do not use the service."
            ),
            TermItemData(
                number = "02",
                title = "Account Responsibility",
                description = "You are responsible for safeguarding your login credentials (student/staff email or ID). Any reporting action performed under your account is considered your personal submission."
            ),
            TermItemData(
                number = "03",
                title = "Appropriate Reporting Guidelines",
                description = "Reports must focus solely on genuine school facility and infrastructure issues (e.g. broken AC, leaks, safety hazards). Submitting fake reports, spam, harassment, or offensive imagery is strictly prohibited."
            ),
            TermItemData(
                number = "04",
                title = "Privacy & Data Usage",
                description = "Your report details, images, and user identifier will be processed by school administrators to address facility issues. We do not distribute your personal data to external third parties."
            ),
            TermItemData(
                number = "05",
                title = "Platform Service Availability",
                description = "TRAC is provided on an 'as-is' basis to support the campus environment. While we aim for constant availability, we do not guarantee uninterrupted access and reserve the right to perform maintenance."
            ),
            TermItemData(
                number = "06",
                title = "Prohibited Misuse",
                description = "You agree not to reverse-engineer TRAC, attempt unauthorized administrative access, or exploit the report submission channel to overload the school administration team."
            ),
            TermItemData(
                number = "07",
                title = "Amendments to Terms",
                description = "We may update these terms occasionally to reflect changes in school policy or software features. Your continued use of TRAC after updates signifies your acceptance of the revised terms."
            )
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
            .background(Color(0xFFF8FAFD))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // 1. Top Header Bar: Back Circle Button + Title "Terms of Service" & Date
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
                            spotColor = Color(0x1A000000),
                            ambientColor = Color(0x0A000000)
                        ),
                    shape = CircleShape,
                    color = Color.White
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ChevronLeftIcon()
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Terms of Service",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Last updated: October 2026",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Animated Main Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = contentAlpha.value
                        translationY = contentOffsetY.value.dp.toPx()
                    }
            ) {
                // Welcome Intro Text
                Text(
                    text = "Welcome to TRAC. Please read these terms carefully before using our school facility issue reporting platform.",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF475569),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 2. Numbered Terms Cards List (01 to 07)
                termsList.forEach { term ->
                    TermCardItem(
                        number = term.number,
                        title = term.title,
                        description = term.description
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // 3. Contact Admin Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(18.dp),
                            spotColor = Color(0x0D000000),
                            ambientColor = Color(0x05000000)
                        ),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                PhoneIcon()
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = "School Administrator",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "+62 812 3456 7890",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "If you have questions about these terms or need to report an application bug, contact the school admin via WhatsApp.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF64748B),
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // 4. Primary "I Accept the Terms" Pill Button
                Button(
                    onClick = onAcceptClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            spotColor = Color(0x402563EB),
                            ambientColor = Color(0x202563EB)
                        ),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "I Accept the Terms",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Footer Slogan
                Text(
                    text = "A Better School, Together",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun TermCardItem(
    number: String,
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = Color(0x0D000000),
                ambientColor = Color(0x05000000)
            ),
        shape = RoundedCornerShape(18.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Number Pill Badge
                Box(
                    modifier = Modifier
                        .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2563EB)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF475569),
                lineHeight = 19.sp
            )
        }
    }
}

@Composable
private fun PhoneIcon() {
    Canvas(modifier = Modifier.size(18.dp)) {
        val w = size.width
        val h = size.height
        val color = Color(0xFF2563EB)

        val phonePath = Path().apply {
            moveTo(w * 0.2f, h * 0.15f)
            quadraticTo(w * 0.5f, h * 0.1f, w * 0.8f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.35f)
            lineTo(w * 0.65f, h * 0.45f)
            lineTo(w * 0.55f, h * 0.35f)
            quadraticTo(w * 0.35f, h * 0.35f, w * 0.35f, h * 0.55f)
            lineTo(w * 0.45f, h * 0.65f)
            lineTo(w * 0.35f, h * 0.85f)
            close()
        }
        drawPath(
            path = phonePath,
            color = color,
            style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
private fun ChevronLeftIcon() {
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
            color = Color(0xFF0F172A),
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
fun TermsTracScreenPreview() {
    TermsTracScreen()
}
