package com.example.trac

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.graphics.PathMeasure
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

@Composable
fun SplashTracScreen(
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    val isPreview = LocalInspectionMode.current

    // Animated states for opening sequence
    val logoScale = remember { Animatable(if (isPreview) 1f else 0.2f) }
    val logoAlpha = remember { Animatable(if (isPreview) 1f else 0f) }
    val logoDrawProgress = remember { Animatable(if (isPreview) 1f else 0f) }

    val titleAlpha = remember { Animatable(if (isPreview) 1f else 0f) }
    val titleOffsetY = remember { Animatable(if (isPreview) 0f else -25f) }

    val subtitleAlpha = remember { Animatable(if (isPreview) 1f else 0f) }

    val cardAlpha = remember { Animatable(if (isPreview) 1f else 0f) }
    val cardOffsetY = remember { Animatable(if (isPreview) 0f else 60f) }

    val descAlpha = remember { Animatable(if (isPreview) 1f else 0f) }

    val loginBtnAlpha = remember { Animatable(if (isPreview) 1f else 0f) }
    val loginBtnOffsetY = remember { Animatable(if (isPreview) 0f else 40f) }

    val regBtnAlpha = remember { Animatable(if (isPreview) 1f else 0f) }
    val regBtnOffsetY = remember { Animatable(if (isPreview) 0f else 40f) }

    val footerAlpha = remember { Animatable(if (isPreview) 1f else 0f) }

    // Execute Opening Sequence
    LaunchedEffect(isPreview) {
        if (!isPreview) {
            // 1. Logo Elastic Bounce & Path Draw
            launch {
                logoAlpha.animateTo(1f, animationSpec = tween(400))
            }
            launch {
                logoScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            launch {
                logoDrawProgress.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
            }

            delay(200)
            // 2. Title & Subtitle Entrance
            launch {
                titleAlpha.animateTo(1f, animationSpec = tween(400))
            }
            launch {
                titleOffsetY.animateTo(
                    0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }

            delay(100)
            launch {
                subtitleAlpha.animateTo(1f, animationSpec = tween(400))
            }

            delay(150)
            // 3. School Card Slide Up
            launch {
                cardAlpha.animateTo(1f, animationSpec = tween(500))
            }
            launch {
                cardOffsetY.animateTo(
                    0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }

            delay(150)
            // 4. Description Text
            launch {
                descAlpha.animateTo(1f, animationSpec = tween(400))
            }

            delay(120)
            // 5. Login Button Entrance
            launch {
                loginBtnAlpha.animateTo(1f, animationSpec = tween(400))
            }
            launch {
                loginBtnOffsetY.animateTo(
                    0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }

            delay(100)
            // 6. Register Button Entrance
            launch {
                regBtnAlpha.animateTo(1f, animationSpec = tween(400))
            }
            launch {
                regBtnOffsetY.animateTo(
                    0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }

            delay(100)
            // 7. Footer Slogan
            launch {
                footerAlpha.animateTo(1f, animationSpec = tween(400))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFD))
    ) {
        // Soft white ambient background circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = 180.dp.toPx(),
                center = Offset(-size.width * 0.05f, size.height * 0.18f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = 170.dp.toPx(),
                center = Offset(size.width * 1.05f, size.height * 0.16f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Header: Animated Logo Icon, App Title & Subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Box(
                    modifier = Modifier.graphicsLayer {
                        scaleX = logoScale.value
                        scaleY = logoScale.value
                        alpha = logoAlpha.value
                    }
                ) {
                    ShieldIconAnimated(drawProgress = logoDrawProgress.value)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "TRAC",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A),
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.graphicsLayer {
                        alpha = titleAlpha.value
                        translationY = titleOffsetY.value.dp.toPx()
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Tradevis Track and Care",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B),
                    modifier = Modifier.graphicsLayer {
                        alpha = subtitleAlpha.value
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Middle Section: School Illustration Card & Description
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.graphicsLayer {
                        alpha = cardAlpha.value
                        translationY = cardOffsetY.value.dp.toPx()
                    }
                ) {
                    SchoolIllustrationCard()
                }

                Spacer(modifier = Modifier.height(26.dp))

                Text(
                    text = "Report school facility issues for a better, safer, and more comfortable environment.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF334155),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .graphicsLayer {
                            alpha = descAlpha.value
                        }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Bottom Section: Action Buttons & Footer Slogan
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Login Primary Pill Button
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .graphicsLayer {
                            alpha = loginBtnAlpha.value
                            translationY = loginBtnOffsetY.value.dp.toPx()
                        }
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
                        text = "Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Register Secondary Pill Button
                Button(
                    onClick = onRegisterClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .graphicsLayer {
                            alpha = regBtnAlpha.value
                            translationY = regBtnOffsetY.value.dp.toPx()
                        }
                        .shadow(
                            elevation = 6.dp,
                            shape = CircleShape,
                            spotColor = Color(0x15000000),
                            ambientColor = Color(0x0A000000)
                        ),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF2563EB)
                    )
                ) {
                    Text(
                        text = "Register",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "A Better School, Together",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.graphicsLayer {
                        alpha = footerAlpha.value
                    }
                )
            }
        }
    }
}

@Composable
fun ShieldIconAnimated(
    drawProgress: Float,
    modifier: Modifier = Modifier
) {
    // Pulse Aura Ring Infinite Animation
    val infiniteTransition = rememberInfiniteTransition(label = "AuraRingTransition")
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AuraScale"
    )
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AuraAlpha"
    )

    Box(
        modifier = modifier.size(76.dp),
        contentAlignment = Alignment.Center
    ) {
        // Expanding Soft Glowing Aura Behind Logo
        Box(
            modifier = Modifier
                .size(68.dp)
                .graphicsLayer {
                    scaleX = auraScale
                    scaleY = auraScale
                    alpha = auraAlpha
                }
                .background(
                    color = Color(0xFF2563EB),
                    shape = RoundedCornerShape(24.dp)
                )
        )

        // Main Royal Blue Logo Squircle Container
        Box(
            modifier = Modifier
                .size(68.dp)
                .shadow(
                    elevation = 14.dp,
                    shape = RoundedCornerShape(22.dp),
                    spotColor = Color(0x502563EB),
                    ambientColor = Color(0x252563EB)
                )
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2563EB),
                            Color(0xFF1D4ED8)
                        )
                    ),
                    shape = RoundedCornerShape(22.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(34.dp)) {
                val w = size.width
                val h = size.height

                // Full Shield Path
                val fullShieldPath = Path().apply {
                    moveTo(w * 0.5f, h * 0.10f)
                    cubicTo(w * 0.70f, h * 0.10f, w * 0.86f, h * 0.14f, w * 0.86f, h * 0.26f)
                    cubicTo(w * 0.86f, h * 0.58f, w * 0.68f, h * 0.82f, w * 0.5f, h * 0.90f)
                    cubicTo(w * 0.32f, h * 0.82f, w * 0.14f, h * 0.58f, w * 0.14f, h * 0.26f)
                    cubicTo(w * 0.14f, h * 0.14f, w * 0.30f, h * 0.10f, w * 0.5f, h * 0.10f)
                    close()
                }

                // Full Checkmark Path
                val fullCheckPath = Path().apply {
                    moveTo(w * 0.37f, h * 0.48f)
                    lineTo(w * 0.47f, h * 0.58f)
                    lineTo(w * 0.63f, h * 0.39f)
                }

                // Animate Path Line Draw Progress using PathMeasure
                val shieldMeasure = PathMeasure()
                shieldMeasure.setPath(fullShieldPath, false)
                val animatedShieldPath = Path()
                shieldMeasure.getSegment(0f, shieldMeasure.length * drawProgress, animatedShieldPath, true)

                drawPath(
                    path = animatedShieldPath,
                    color = Color.White,
                    style = Stroke(
                        width = 2.4.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                if (drawProgress > 0.4f) {
                    val checkProgress = ((drawProgress - 0.4f) / 0.6f).coerceIn(0f, 1f)
                    val checkMeasure = PathMeasure()
                    checkMeasure.setPath(fullCheckPath, false)
                    val animatedCheckPath = Path()
                    checkMeasure.getSegment(0f, checkMeasure.length * checkProgress, animatedCheckPath, true)

                    drawPath(
                        path = animatedCheckPath,
                        color = Color.White,
                        style = Stroke(
                            width = 2.4.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SchoolIllustrationCard() {
    // Infinite ambient animations for clouds floating and sun breathing
    val infiniteTransition = rememberInfiniteTransition(label = "IllustrationAnimations")

    val cloudOffsetX by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CloudFloating"
    )

    val sunPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SunPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = Color(0x202563EB),
                ambientColor = Color(0x0E000000)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE2EDFF),
                        Color(0xFFD6E6FF)
                    )
                )
            )
    ) {
        // Sky, Sun, Clouds & Green Ground Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Sun (Bright golden yellow circle in upper right with subtle pulse)
            drawCircle(
                color = Color(0xFFFACC15),
                radius = 28.dp.toPx() * sunPulseScale,
                center = Offset(width * 0.78f, height * 0.26f)
            )

            // 2. White Floating Clouds
            val cloudColor = Color.White.copy(alpha = 0.92f)
            val shiftX = cloudOffsetX.dp.toPx()

            // Left fluffy cloud group (shifts gently)
            drawCircle(cloudColor, 18.dp.toPx(), Offset(width * 0.25f + shiftX, height * 0.28f))
            drawCircle(cloudColor, 25.dp.toPx(), Offset(width * 0.35f + shiftX, height * 0.24f))
            drawCircle(cloudColor, 20.dp.toPx(), Offset(width * 0.46f + shiftX, height * 0.28f))
            drawCircle(cloudColor, 15.dp.toPx(), Offset(width * 0.16f + shiftX, height * 0.30f))

            // Cloud partially overlapping sun (shifts slightly inverse)
            drawCircle(Color.White.copy(alpha = 0.85f), 18.dp.toPx(), Offset(width * 0.65f - shiftX * 0.5f, height * 0.31f))

            // Yellow accent dot near clouds
            drawCircle(Color(0xFFFACC15), 3.5.dp.toPx(), Offset(width * 0.31f + shiftX, height * 0.39f))

            // 3. Ground (Mint green curved hill shape at bottom)
            val groundY = height * 0.68f
            drawRoundRect(
                color = Color(0xFF86EFAC),
                topLeft = Offset(0f, groundY),
                size = Size(width, height - groundY),
                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
            )
        }

        // Center Illustration: School Building, Roof Flag, and Side Trees
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                // Roof Flag
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(20.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Flag pole
                        drawLine(
                            color = Color(0xFF94A3B8),
                            start = Offset(size.width * 0.45f, 0f),
                            end = Offset(size.width * 0.45f, size.height),
                            strokeWidth = 2.dp.toPx()
                        )
                        // Yellow flag
                        val flagPath = Path().apply {
                            moveTo(size.width * 0.45f, 0f)
                            lineTo(size.width * 0.95f, size.height * 0.28f)
                            lineTo(size.width * 0.45f, size.height * 0.56f)
                            close()
                        }
                        drawPath(flagPath, color = Color(0xFFFACC15))
                    }
                }

                // Building Structure & Flanking Trees
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Left Tree
                    TreeItem()

                    Spacer(modifier = Modifier.width(16.dp))

                    // White Building Body
                    Surface(
                        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier.width(138.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // "SCHOOL" Banner
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF2563EB))
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "SCHOOL",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 3 Windows
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                            ) {
                                WindowBox()
                                WindowBox()
                                WindowBox()
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Door
                            Box(
                                modifier = Modifier
                                    .width(36.dp)
                                    .height(30.dp)
                                    .background(
                                        color = Color(0xFF2563EB),
                                        shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Right Tree
                    TreeItem()
                }
            }
        }
    }
}

@Composable
private fun WindowBox() {
    Box(
        modifier = Modifier
            .size(16.dp)
            .background(Color(0xFFBFDBFE), shape = RoundedCornerShape(2.dp))
    )
}

@Composable
private fun TreeItem() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Round green tree crown
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(Color(0xFF4ADE80), shape = CircleShape)
        )
        // Tree trunk
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(10.dp)
                .background(Color(0xFF94A3B8))
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashTracScreenPreview() {
    SplashTracScreen()
}
