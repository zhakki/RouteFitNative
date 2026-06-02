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
import com.example.routefitnative.ui.theme.RouteFitAccent
import com.example.routefitnative.ui.theme.RouteFitBackground
import com.example.routefitnative.ui.theme.RouteFitOnAccent
import com.example.routefitnative.ui.theme.RouteFitOutline
import com.example.routefitnative.ui.theme.RouteFitSurface
import com.example.routefitnative.ui.theme.RouteFitSurfaceVariant
import com.example.routefitnative.ui.theme.RouteFitTextPrimary
import com.example.routefitnative.ui.theme.RouteFitTextSecondary

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
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
                modifier = Modifier.padding(top = 14.dp)
            )

            SettingsSectionTitle(
                text = "MARSRUUDI SEADED",
                modifier = Modifier.padding(top = 26.dp)
            )
            RouteSettingsCard(
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
                modifier = Modifier.padding(top = 14.dp)
            )

            Button(
                onClick = {},
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
private fun GoalsCard(modifier: Modifier = Modifier) {
    RouteFitSettingsCard(modifier = modifier) {
        Text(
            text = "Päevane sammueesmärk",
            color = RouteFitTextPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "24 000",
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
            value = 24000f,
            onValueChange = {},
            valueRange = 5_000f..40_000f,
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
            text = "Nädala eesmärk arvutatakse automaatselt: 168 000 sammu",
            modifier = Modifier.padding(top = 8.dp),
            color = RouteFitTextSecondary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun RouteSettingsCard(modifier: Modifier = Modifier) {
    RouteFitSettingsCard(modifier = modifier) {
        Text(
            text = "Vahemaaühik",
            color = RouteFitTextPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold
        )
        UnitSegmentedControl(
            modifier = Modifier.padding(top = 14.dp)
        )

        SettingsDivider()
        SettingSwitchRow(title = "Salvesta marsruudid", checked = true)
        SettingsDivider()
        SettingSwitchRow(title = "Kasuta asukohta", checked = true)
    }
}

@Composable
private fun AppSettingsCard(modifier: Modifier = Modifier) {
    RouteFitSettingsCard(modifier = modifier) {
        SettingSwitchRow(title = "Teavitused", checked = true)
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
private fun AccountCard(modifier: Modifier = Modifier) {
    RouteFitSettingsCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
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
private fun UnitSegmentedControl(modifier: Modifier = Modifier) {
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
            selected = true,
            modifier = Modifier.weight(1f)
        )
        SegmentOption(
            text = "mi",
            selected = false,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SegmentOption(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(if (selected) RouteFitAccent else Color.Transparent),
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
private fun SettingSwitchRow(title: String, checked: Boolean) {
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
            onCheckedChange = {},
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
