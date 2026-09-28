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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trac.components.InAppBanner
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterTracScreen(
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onSignUpClick: (fullName: String, email: String, userClass: String, pass: String, confirmPass: String) -> Unit = { _, _, _, _, _ -> },
    onLoginClick: () -> Unit = {},
    onTermsClick: () -> Unit = {}
) {
    val isPreview = LocalInspectionMode.current

    var fullName by rememberSaveable { mutableStateOf("") }
    var emailAddress by rememberSaveable { mutableStateOf("") }
    var userClass by rememberSaveable { mutableStateOf("XI RPL") }
    var customClass by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    var showClassPickerSheet by remember { mutableStateOf(false) }
    var classSearchQuery by remember { mutableStateOf("") }
    val classSheetState = rememberModalBottomSheetState()

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var isAgreeTerms by rememberSaveable { mutableStateOf(true) }

    // Real 28 Class List
    val realClassList = listOf(
        "X AKL 1", "X AKL 2", "X AKL 3", "X BD", "X Manlog", "X MP", "X RPL", "X ULW",
        "XI AK 1", "XI AK 2", "XI AK 3", "XI BD", "XI BR 1", "XI BR 2", "XI Manlog", "XI MP", "XI RPL", "XI ULW",
        "XII AK 1", "XII AK 2", "XII AK 3", "XII BD", "XII BR 1", "XII BR 2", "XII MP 1", "XII MP 2", "XII RPL", "XII ULW",
        "Lainnya (Kustom)"
    )

    val finalClass = if (userClass == "Lainnya (Kustom)") customClass else userClass

    // Real-time Field Validations
    val isEmailValid = emailAddress.isEmpty() || (emailAddress.contains("@") && emailAddress.contains("."))
    val isPasswordValid = password.isEmpty() || password.length >= 6
    val isConfirmPasswordValid = confirmPassword.isEmpty() || confirmPassword == password

    // Staggered entrance animation states
    val headerAlpha = remember { Animatable(if (isPreview) 1f else 0f) }
    val headerOffsetY = remember { Animatable(if (isPreview) 0f else -35f) }

    val formAlpha = remember { Animatable(if (isPreview) 1f else 0f) }
    val formOffsetY = remember { Animatable(if (isPreview) 0f else 40f) }

    val buttonAlpha = remember { Animatable(if (isPreview) 1f else 0f) }
    val buttonOffsetY = remember { Animatable(if (isPreview) 0f else 40f) }

    val footerAlpha = remember { Animatable(if (isPreview) 1f else 0f) }

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
                formAlpha.animateTo(1f, animationSpec = tween(450))
            }
            launch {
                formOffsetY.animateTo(
                    0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }

            delay(120)
            launch {
                buttonAlpha.animateTo(1f, animationSpec = tween(400))
            }
            launch {
                buttonOffsetY.animateTo(
                    0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }

            delay(100)
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
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = 180.dp.toPx(),
                center = Offset(-size.width * 0.05f, size.height * 0.15f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = 170.dp.toPx(),
                center = Offset(size.width * 1.05f, size.height * 0.12f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = headerAlpha.value
                            translationY = headerOffsetY.value.dp.toPx()
                        }
                ) {
                    ShieldIconSmall()

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "CREATE ACCOUNT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB),
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Join us!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Create an account to start reporting school facility issues.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B),
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                InAppBanner(
                    message = errorMessage,
                    isError = true
                )

                // Form Fields Group
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = formAlpha.value
                            translationY = formOffsetY.value.dp.toPx()
                        }
                ) {
                    // Field 1: Full Name
                    Text(
                        text = "Full Name",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    TracTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = "Enter your full name..."
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Field 2: Class / Grade (Opens Searchable Sheet)
                    Text(
                        text = "Class / Grade",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = Color(0x0D000000)
                            )
                            .clickable { showClassPickerSheet = true },
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFEFF3F8))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (userClass.isNotBlank()) userClass else "Select your class...",
                                fontSize = 14.sp,
                                color = if (userClass.isNotBlank()) Color(0xFF0F172A) else Color(0xFF94A3B8),
                                fontWeight = if (userClass.isNotBlank()) FontWeight.Medium else FontWeight.Normal
                            )

                            ChevronDownIcon()
                        }
                    }

                    if (userClass == "Lainnya (Kustom)") {
                        Spacer(modifier = Modifier.height(8.dp))
                        TracTextField(
                            value = customClass,
                            onValueChange = { customClass = it },
                            placeholder = "Tuliskan nama kelas..."
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Field 3: Email
                    Text(
                        text = "Email Address",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    TracTextField(
                        value = emailAddress,
                        onValueChange = { emailAddress = it },
                        placeholder = "e.g. budi@school.sch.id..."
                    )

                    if (!isEmailValid) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Format email tidak valid (butuh @ dan domain)",
                            fontSize = 11.sp,
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Field 4: Password
                    Text(
                        text = "Password",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    TracTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "••••••••",
                        isPassword = true,
                        isPasswordVisible = isPasswordVisible,
                        onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible }
                    )

                    if (!isPasswordValid) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Password minimal harus 6 karakter",
                            fontSize = 11.sp,
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Field 5: Confirm Password
                    Text(
                        text = "Confirm Password",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    TracTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = "••••••••",
                        isPassword = true,
                        isPasswordVisible = isConfirmPasswordVisible,
                        onTogglePasswordVisibility = { isConfirmPasswordVisible = !isConfirmPasswordVisible }
                    )

                    if (!isConfirmPasswordValid) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Konfirmasi password tidak cocok",
                            fontSize = 11.sp,
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Clean Inline Checkbox Row: I agree to the Terms & Conditions
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isAgreeTerms = !isAgreeTerms }
                    ) {
                        Checkbox(
                            checked = isAgreeTerms,
                            onCheckedChange = { isAgreeTerms = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF2563EB),
                                uncheckedColor = Color(0xFF94A3B8)
                            )
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "I agree to ",
                                fontSize = 12.5.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Terms & Conditions",
                                fontSize = 12.5.sp,
                                color = Color(0xFF2563EB),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onTermsClick() }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Primary Sign Up Button
                val isFormValid = isAgreeTerms && isEmailValid && isPasswordValid && isConfirmPasswordValid &&
                        fullName.isNotBlank() && finalClass.isNotBlank() && emailAddress.isNotBlank() && password.isNotBlank()

                Button(
                    onClick = {
                        if (isFormValid) {
                            onSignUpClick(fullName, emailAddress, finalClass, password, confirmPassword)
                        }
                    },
                    enabled = isFormValid && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .graphicsLayer {
                            alpha = buttonAlpha.value
                            translationY = buttonOffsetY.value.dp.toPx()
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
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF93C5FD),
                        disabledContentColor = Color.White.copy(alpha = 0.8f)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = "Sign Up Now",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .graphicsLayer {
                        alpha = footerAlpha.value
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFE2E8F0),
                        thickness = 1.dp
                    )
                    Text(
                        text = "atau",
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFE2E8F0),
                        thickness = 1.dp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account? ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "Log In",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB),
                        modifier = Modifier.clickable { onLoginClick() }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Searchable Class Picker Modal Bottom Sheet
        if (showClassPickerSheet) {
            ModalBottomSheet(
                onDismissRequest = { showClassPickerSheet = false },
                sheetState = classSheetState,
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(440.dp)
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Pilih Kelas / Tingkat",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF8FAFD),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SearchIconSmall(color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                if (classSearchQuery.isEmpty()) {
                                    Text(
                                        text = "Cari nama kelas... (contoh: RPL, AKL)",
                                        fontSize = 13.5.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                                BasicTextField(
                                    value = classSearchQuery,
                                    onValueChange = { classSearchQuery = it },
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF0F172A)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val filteredClasses = realClassList.filter {
                        it.contains(classSearchQuery, ignoreCase = true)
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredClasses) { cls ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        userClass = cls
                                        showClassPickerSheet = false
                                        classSearchQuery = ""
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = if (cls == userClass) Color(0xFFEFF6FF) else Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = cls,
                                        fontSize = 14.5.sp,
                                        fontWeight = if (cls == userClass) FontWeight.Bold else FontWeight.Medium,
                                        color = if (cls == userClass) Color(0xFF2563EB) else Color(0xFF0F172A)
                                    )

                                    if (cls == userClass) {
                                        Text(
                                            text = "✓",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2563EB)
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

@Composable
private fun SearchIconSmall(color: Color) {
    Canvas(modifier = Modifier.size(16.dp)) {
        val w = size.width
        val h = size.height

        drawCircle(
            color = color,
            radius = w * 0.32f,
            center = Offset(w * 0.4f, h * 0.4f),
            style = Stroke(width = 1.8.dp.toPx())
        )
        drawLine(
            color = color,
            start = Offset(w * 0.62f, h * 0.62f),
            end = Offset(w * 0.88f, h * 0.88f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ChevronDownIcon() {
    Canvas(modifier = Modifier.size(16.dp)) {
        val w = size.width
        val h = size.height

        val path = Path().apply {
            moveTo(w * 0.25f, h * 0.35f)
            lineTo(w * 0.5f, h * 0.65f)
            lineTo(w * 0.75f, h * 0.35f)
        }
        drawPath(
            path = path,
            color = Color(0xFF64748B),
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterTracScreenPreview() {
    RegisterTracScreen()
}
