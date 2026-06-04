package com.example.routefitnative.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.routefitnative.data.AuthRepository
import com.example.routefitnative.data.UserRepository
import com.example.routefitnative.model.UserProfile
import com.example.routefitnative.ui.components.BottomNavItem
import com.example.routefitnative.ui.components.BottomNavigationBar
import com.example.routefitnative.ui.theme.RouteFitAccent
import com.example.routefitnative.ui.theme.RouteFitBackground
import com.example.routefitnative.ui.theme.RouteFitOnAccent
import com.example.routefitnative.ui.theme.RouteFitOutline
import com.example.routefitnative.ui.theme.RouteFitSurface
import com.example.routefitnative.ui.theme.RouteFitSurfaceVariant
import com.example.routefitnative.ui.theme.RouteFitTextPrimary
import com.example.routefitnative.ui.theme.RouteFitTextSecondary

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onBottomNavItemClick: (BottomNavItem) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {}
) {
    val authRepository = remember { AuthRepository() }
    val userRepository = remember { UserRepository() }

    var profile by remember { mutableStateOf<UserProfile?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val currentUser = authRepository.currentUser

        if (currentUser == null) {
            errorMessage = "Kasutaja pole sisse logitud."
        } else {
            try {
                userRepository.ensureUserProfileExists(
                    uid = currentUser.uid,
                    email = currentUser.email ?: ""
                )

                profile = userRepository.getUserProfile(currentUser.uid)
                errorMessage = ""
            } catch (e: Exception) {
                errorMessage = e.message ?: "Profiili laadimine ebaõnnestus."
            }
        }
    }

    val currentEmail = profile?.email
        ?.takeIf { it.isNotBlank() }
        ?: authRepository.currentUser?.email
        ?: "kasutaja@email.ee"

    val displayName = profile?.fullName
        ?.takeIf { it.isNotBlank() }
        ?: "RouteFit kasutaja"

    val initials = profileInitials(displayName, currentEmail)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = RouteFitBackground,
        bottomBar = {
            BottomNavigationBar(
                selectedItem = BottomNavItem.Profile,
                onItemClick = onBottomNavItemClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(RouteFitBackground)
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                RouteFitAccent.copy(alpha = 0.12f),
                                RouteFitBackground
                            ),
                            radius = 980f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp)
                    .padding(top = 30.dp, bottom = 24.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "RouteFit",
                        modifier = Modifier.align(Alignment.Center),
                        color = RouteFitAccent,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 48.sp,
                            lineHeight = 54.sp
                        ),
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center
                    )

                    Surface(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(48.dp)
                            .clickable(onClick = onSettingsClick),
                        shape = CircleShape,
                        color = RouteFitSurfaceVariant.copy(alpha = 0.82f),
                        border = BorderStroke(1.dp, RouteFitOutline)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            GearIcon(
                                modifier = Modifier.size(26.dp),
                                color = RouteFitAccent
                            )
                        }
                    }
                }

                ProfileHeader(
                    modifier = Modifier.padding(top = 34.dp),
                    initials = initials,
                    fullName = displayName,
                    email = currentEmail,
                    onEditProfileClick = onEditProfileClick
                )

                SectionTitle(
                    text = "Isiklik info",
                    modifier = Modifier.padding(top = 28.dp)
                )

                InfoGrid(
                    items = listOf(
                        "Vanus" to formatAge(profile?.age ?: 0),
                        "Kaal" to formatDoubleValue(profile?.weightKg ?: 0.0, "kg"),
                        "Pikkus" to formatDoubleValue(profile?.heightCm ?: 0.0, "cm"),
                        "Sugu" to formatGender(profile?.gender.orEmpty())
                    ),
                    modifier = Modifier.padding(top = 14.dp)
                )

                if (errorMessage.isNotBlank()) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        color = RouteFitAccent,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    modifier: Modifier = Modifier,
    initials: String,
    fullName: String,
    email: String,
    onEditProfileClick: () -> Unit = {}
) {
    RouteFitProfileCard(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .size(128.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = CircleShape,
                        ambientColor = RouteFitAccent.copy(alpha = 0.12f),
                        spotColor = RouteFitAccent.copy(alpha = 0.18f)
                    ),
                shape = CircleShape,
                color = RouteFitSurfaceVariant,
                border = BorderStroke(3.dp, RouteFitAccent)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = initials,
                        color = RouteFitAccent,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 92.dp, bottom = 8.dp)
                    .size(42.dp),
                shape = CircleShape,
                color = RouteFitAccent,
                border = BorderStroke(2.dp, RouteFitOnAccent)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    EditIcon(
                        modifier = Modifier.size(22.dp),
                        color = RouteFitOnAccent
                    )
                }
            }
        }

        Text(
            text = fullName,
            modifier = Modifier.fillMaxWidth(),
            color = RouteFitTextPrimary,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 32.sp,
                lineHeight = 38.sp
            ),
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Text(
            text = "RouteFit kasutaja",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            color = RouteFitTextSecondary,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Button(
            onClick = onEditProfileClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp)
                .height(58.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RouteFitAccent,
                contentColor = RouteFitOnAccent
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = "Muuda profiili",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Text(
            text = email,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp),
            color = RouteFitAccent,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        color = RouteFitTextPrimary,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold
    )
}

@Composable
private fun InfoGrid(
    items: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        items.chunked(2).forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = if (rowIndex == 0) 0.dp else 14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                rowItems.forEach { item ->
                    InfoTile(
                        label = item.first,
                        value = item.second,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    RouteFitProfileCard(
        modifier = modifier,
        contentPadding = 18.dp
    ) {
        Text(
            text = label,
            color = RouteFitAccent,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1
        )
        Text(
            text = value,
            modifier = Modifier.padding(top = 10.dp),
            color = RouteFitTextPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
    }
}

@Composable
private fun RouteFitProfileCard(
    modifier: Modifier = Modifier,
    contentPadding: androidx.compose.ui.unit.Dp = 22.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = RouteFitAccent.copy(alpha = 0.08f),
                spotColor = RouteFitAccent.copy(alpha = 0.08f)
            )
            .border(
                BorderStroke(1.dp, RouteFitOutline),
                RoundedCornerShape(24.dp)
            ),
        color = RouteFitSurface.copy(alpha = 0.92f),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

@Composable
private fun GearIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.2.dp.toPx()
        val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        drawCircle(color = color, radius = size.minDimension * 0.21f, style = stroke)
        drawCircle(color = color, radius = size.minDimension * 0.38f, style = stroke)
        repeat(8) { index ->
            val angle = Math.toRadians((index * 45).toDouble())
            val startRadius = size.minDimension * 0.42f
            val endRadius = size.minDimension * 0.5f
            val start = Offset(
                x = center.x + kotlin.math.cos(angle).toFloat() * startRadius,
                y = center.y + kotlin.math.sin(angle).toFloat() * startRadius
            )
            val end = Offset(
                x = center.x + kotlin.math.cos(angle).toFloat() * endRadius,
                y = center.y + kotlin.math.sin(angle).toFloat() * endRadius
            )
            drawLine(
                color = color,
                start = start,
                end = end,
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun EditIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.5.dp.toPx()
        drawLine(
            color = color,
            start = Offset(size.width * 0.28f, size.height * 0.72f),
            end = Offset(size.width * 0.72f, size.height * 0.28f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.62f, size.height * 0.22f),
            end = Offset(size.width * 0.78f, size.height * 0.38f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.22f, size.height * 0.78f),
            end = Offset(size.width * 0.38f, size.height * 0.74f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

private fun profileInitials(fullName: String, email: String): String {
    val source = if (fullName.isNotBlank()) fullName else email.substringBefore("@")
    val parts = source
        .trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }

    return when {
        parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> "RF"
    }
}

private fun formatAge(age: Int): String {
    return if (age > 0) age.toString() else "—"
}

private fun formatDoubleValue(value: Double, unit: String): String {
    if (value <= 0.0) return "—"

    val numberText = if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        value.toString()
    }

    return "$numberText $unit"
}

private fun formatGender(gender: String): String {
    return when (gender.trim().lowercase()) {
        "female", "naine" -> "Naine"
        "male", "mees" -> "Mees"
        "" -> "—"
        else -> gender
    }
}