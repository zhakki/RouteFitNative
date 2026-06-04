package com.example.routefitnative.ui.screens.settings

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.routefitnative.data.AuthRepository
import com.example.routefitnative.data.UserRepository
import com.example.routefitnative.model.UserSettings
import com.example.routefitnative.ui.theme.RouteFitAccent
import com.example.routefitnative.ui.theme.RouteFitBackground
import com.example.routefitnative.ui.theme.RouteFitOnAccent
import com.example.routefitnative.ui.theme.RouteFitOutline
import com.example.routefitnative.ui.theme.RouteFitSurface
import com.example.routefitnative.ui.theme.RouteFitSurfaceVariant
import com.example.routefitnative.ui.theme.RouteFitTextPrimary
import com.example.routefitnative.ui.theme.RouteFitTextSecondary
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val authRepository = remember { AuthRepository() }
    val userRepository = remember { UserRepository() }
    val coroutineScope = rememberCoroutineScope()

    var settings by remember { mutableStateOf(UserSettings()) }
    var errorMessage by remember { mutableStateOf("") }

    fun saveSettings(updatedSettings: UserSettings) {
        val uid = authRepository.currentUser?.uid ?: return

        settings = updatedSettings

        coroutineScope.launch {
            try {
                userRepository.updateUserSettings(uid, updatedSettings)
                errorMessage = ""
            } catch (e: Exception) {
                errorMessage = e.message ?: "Seadete salvestamine ebaõnnestus."
            }
        }
    }

    LaunchedEffect(Unit) {
        val uid = authRepository.currentUser?.uid

        if (uid == null) {
            errorMessage = "Kasutaja pole sisse logitud."
        } else {
            try {
                settings = userRepository.getUserSettings(uid)
                errorMessage = ""
            } catch (e: Exception) {
                errorMessage = e.message ?: "Seadete laadimine ebaõnnestus."
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(RouteFitBackground)
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
                .padding(top = 30.dp, bottom = 32.dp)
        ) {
            SettingsTopBar(onBackClick = onBackClick)

            SettingsSectionTitle(
                text = "EESMÄRGID",
                modifier = Modifier.padding(top = 30.dp)
            )
            GoalsCard(
                dailyStepGoal = settings.dailyStepGoal,
                onDailyStepGoalChange = { newGoal ->
                    settings = settings.copy(dailyStepGoal = newGoal)
                },
                onDailyStepGoalChangeFinished = {
                    saveSettings(settings)
                },
                modifier = Modifier.padding(top = 14.dp)
            )

            SettingsSectionTitle(
                text = "MARSRUUDI SEADED",
                modifier = Modifier.padding(top = 26.dp)
            )
            RouteSettingsCard(
                distanceUnit = settings.distanceUnit,
                saveRoutes = settings.saveRoutes,
                allowLocation = settings.allowLocation,
                onDistanceUnitChange = { unit ->
                    saveSettings(settings.copy(distanceUnit = unit))
                },
                onSaveRoutesChange = { checked ->
                    saveSettings(settings.copy(saveRoutes = checked))
                },
                onAllowLocationChange = { checked ->
                    saveSettings(settings.copy(allowLocation = checked))
                },
                modifier = Modifier.padding(top = 14.dp)
            )

            SettingsSectionTitle(
                text = "RAKENDUSE SEADED",
                modifier = Modifier.padding(top = 26.dp)
            )
            AppSettingsCard(
                modifier = Modifier.padding(top = 14.dp)
            )

            SettingsSectionTitle(
                text = "KONTO",
                modifier = Modifier.padding(top = 26.dp)
            )
            AccountCard(
                modifier = Modifier.padding(top = 14.dp),
                onEditProfileClick = onEditProfileClick
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

            Button(
                onClick = {
                    authRepository.logout()
                    onLogoutClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp)
                    .height(64.dp),
                shape = RoundedCornerShape(34.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RouteFitAccent,
                    contentColor = RouteFitOnAccent
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "LOGI VÄLJA",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun SettingsTopBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(46.dp)
                .clickable(onClick = onBackClick),
            shape = CircleShape,
            color = RouteFitSurfaceVariant.copy(alpha = 0.82f),
            border = BorderStroke(1.dp, RouteFitOutline)
        ) {
            Box(contentAlignment = Alignment.Center) {
                BackArrowIcon(
                    modifier = Modifier.size(26.dp),
                    color = RouteFitAccent
                )
            }
        }

        Text(
            text = "SEADED",
            modifier = Modifier.align(Alignment.Center),
            color = RouteFitTextPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun GoalsCard(
    dailyStepGoal: Int,
    onDailyStepGoalChange: (Int) -> Unit,
    onDailyStepGoalChangeFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val safeGoal = dailyStepGoal.coerceIn(5_000, 40_000)
    val weeklyGoal = safeGoal * 7

    RouteFitSettingsCard(modifier = modifier) {
        Text(
            text = "Päevane sammueesmärk",
            color = RouteFitTextPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = formatSteps(safeGoal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            color = RouteFitAccent,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 48.sp,
                lineHeight = 54.sp
            ),
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Slider(
            value = safeGoal.toFloat(),
            onValueChange = { value ->
                onDailyStepGoalChange(roundToNearestThousand(value))
            },
            onValueChangeFinished = onDailyStepGoalChangeFinished,
            valueRange = 5_000f..40_000f,
            steps = 34,
            modifier = Modifier.padding(top = 12.dp),
            colors = SliderDefaults.colors(
                thumbColor = RouteFitAccent,
                activeTrackColor = RouteFitAccent,
                inactiveTrackColor = RouteFitSurfaceVariant,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            )
        )

        Text(
            text = "Nädala eesmärk arvutatakse automaatselt: ${formatSteps(weeklyGoal)} sammu",
            modifier = Modifier.padding(top = 8.dp),
            color = RouteFitTextSecondary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun RouteSettingsCard(
    distanceUnit: String,
    saveRoutes: Boolean,
    allowLocation: Boolean,
    onDistanceUnitChange: (String) -> Unit,
    onSaveRoutesChange: (Boolean) -> Unit,
    onAllowLocationChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    RouteFitSettingsCard(modifier = modifier) {
        Text(
            text = "Vahemaaühik",
            color = RouteFitTextPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold
        )
        UnitSegmentedControl(
            selectedUnit = distanceUnit,
            onUnitChange = onDistanceUnitChange,
            modifier = Modifier.padding(top = 14.dp)
        )

        SettingsDivider()
        SettingSwitchRow(
            title = "Salvesta marsruudid",
            checked = saveRoutes,
            onCheckedChange = onSaveRoutesChange
        )
        SettingsDivider()
        SettingSwitchRow(
            title = "Kasuta asukohta",
            checked = allowLocation,
            onCheckedChange = onAllowLocationChange
        )
    }
}

@Composable
private fun AppSettingsCard(modifier: Modifier = Modifier) {
    var notificationsEnabled by remember { mutableStateOf(true) }

    RouteFitSettingsCard(modifier = modifier) {
        SettingSwitchRow(
            title = "Teavitused",
            checked = notificationsEnabled,
            onCheckedChange = { notificationsEnabled = it }
        )
        SettingsDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Keele valik",
                    color = RouteFitTextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Keel: Eesti",
                    modifier = Modifier.padding(top = 5.dp),
                    color = RouteFitTextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            LanguageIcon(
                modifier = Modifier.size(30.dp),
                color = RouteFitAccent
            )
        }
    }
}

@Composable
private fun AccountCard(
    modifier: Modifier = Modifier,
    onEditProfileClick: () -> Unit = {}
) {
    RouteFitSettingsCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onEditProfileClick)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Muuda profiili",
                modifier = Modifier.weight(1f),
                color = RouteFitTextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold
            )
            ArrowRightIcon(
                modifier = Modifier.size(28.dp),
                color = RouteFitTextSecondary
            )
        }
    }
}

@Composable
private fun SettingsSectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        color = RouteFitAccent,
        style = MaterialTheme.typography.labelMedium
    )
}

@Composable
private fun UnitSegmentedControl(
    selectedUnit: String,
    onUnitChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(RouteFitSurfaceVariant.copy(alpha = 0.72f))
            .border(BorderStroke(1.dp, RouteFitOutline), RoundedCornerShape(28.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SegmentOption(
            text = "km",
            selected = selectedUnit == "km",
            onClick = { onUnitChange("km") },
            modifier = Modifier.weight(1f)
        )
        SegmentOption(
            text = "mi",
            selected = selectedUnit == "mi",
            onClick = { onUnitChange("mi") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SegmentOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(if (selected) RouteFitAccent else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) RouteFitOnAccent else RouteFitTextSecondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = RouteFitTextPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = RouteFitOnAccent,
                checkedTrackColor = RouteFitAccent,
                checkedBorderColor = RouteFitAccent,
                uncheckedThumbColor = RouteFitTextSecondary,
                uncheckedTrackColor = RouteFitSurfaceVariant,
                uncheckedBorderColor = RouteFitOutline
            )
        )
    }
}

@Composable
private fun SettingsDivider() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
            .height(1.dp)
            .background(RouteFitOutline.copy(alpha = 0.7f))
    )
}

@Composable
private fun RouteFitSettingsCard(
    modifier: Modifier = Modifier,
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
            modifier = Modifier.padding(22.dp),
            content = content
        )
    }
}

@Composable
private fun BackArrowIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.8.dp.toPx()
        drawLine(
            color = color,
            start = Offset(size.width * 0.68f, size.height * 0.2f),
            end = Offset(size.width * 0.32f, size.height * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.32f, size.height * 0.5f),
            end = Offset(size.width * 0.68f, size.height * 0.8f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ArrowRightIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.8.dp.toPx()
        drawLine(
            color = color,
            start = Offset(size.width * 0.32f, size.height * 0.2f),
            end = Offset(size.width * 0.68f, size.height * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.68f, size.height * 0.5f),
            end = Offset(size.width * 0.32f, size.height * 0.8f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun LanguageIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        drawCircle(color = color, radius = size.minDimension * 0.42f, style = stroke)
        drawLine(
            color = color,
            start = Offset(size.width * 0.12f, center.y),
            end = Offset(size.width * 0.88f, center.y),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(center.x, size.height * 0.08f),
            end = Offset(center.x, size.height * 0.92f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawArc(
            color = color,
            startAngle = -75f,
            sweepAngle = 150f,
            useCenter = false,
            topLeft = Offset(size.width * 0.28f, size.height * 0.08f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.44f, size.height * 0.84f),
            style = stroke
        )
        drawArc(
            color = color,
            startAngle = 105f,
            sweepAngle = 150f,
            useCenter = false,
            topLeft = Offset(size.width * 0.28f, size.height * 0.08f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.44f, size.height * 0.84f),
            style = stroke
        )
    }
}

private fun roundToNearestThousand(value: Float): Int {
    return ((value / 1000f).roundToInt() * 1000).coerceIn(5_000, 40_000)
}

private fun formatSteps(value: Int): String {
    return "%,d".format(value).replace(",", " ")
}