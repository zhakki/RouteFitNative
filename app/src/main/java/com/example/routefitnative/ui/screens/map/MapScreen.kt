package com.example.routefitnative.ui.screens.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun MapScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = RouteFitBackground,
        bottomBar = {
            BottomNavigationBar(selectedItem = BottomNavItem.Map)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(RouteFitBackground)
                .padding(innerPadding)
        ) {
            Text(
                text = "RouteFit",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 34.dp, bottom = 26.dp),
                color = RouteFitAccent,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 46.sp,
                    lineHeight = 52.sp
                ),
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                MapStatCard(
                    label = "Tempo",
                    value = "0'00",
                    unit = "/km",
                    modifier = Modifier.weight(1f)
                )
                MapStatCard(
                    label = "Vahemaa",
                    value = "0",
                    unit = "m",
                    modifier = Modifier.weight(1f)
                )
                MapStatCard(
                    label = "Aeg",
                    value = "00:00",
                    unit = "",
                    modifier = Modifier.weight(1f)
                )
            }

            MapPreview(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 18.dp)
            )
        }
    }
}

@Composable
private fun MapStatCard(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(116.dp),
        color = RouteFitSurface.copy(alpha = 0.94f),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, RouteFitOutline)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                color = RouteFitTextSecondary,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )
            Row(
                modifier = Modifier.padding(top = 12.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = value,
                    color = RouteFitAccent,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = if (value.length > 3) 34.sp else 40.sp,
                        lineHeight = 42.sp
                    ),
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
                if (unit.isNotEmpty()) {
                    Text(
                        text = " $unit",
                        modifier = Modifier.padding(bottom = 5.dp),
                        color = RouteFitTextPrimary,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun MapPreview(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(RouteFitBackground)
    ) {
        StylizedMapBackground(modifier = Modifier.matchParentSize())

        Text(
            text = "KAARDI EELVAADE",
            modifier = Modifier.align(Alignment.Center),
            color = RouteFitTextPrimary.copy(alpha = 0.58f),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 92.dp, end = 24.dp)
                .size(68.dp),
            shape = CircleShape,
            color = RouteFitAccent.copy(alpha = 0.16f),
            border = BorderStroke(2.dp, RouteFitAccent.copy(alpha = 0.76f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                MyLocationIcon(
                    modifier = Modifier.size(34.dp),
                    color = RouteFitAccent
                )
            }
        }

        ControlPanel(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 22.dp, vertical = 24.dp)
        )
    }
}

@Composable
private fun ControlPanel(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = RouteFitSurface.copy(alpha = 0.94f),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, RouteFitOutline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(118.dp)
                .padding(horizontal = 22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(RouteFitAccent)
                )
                Column(modifier = Modifier.padding(start = 14.dp)) {
                    Text(
                        text = "GPS jälgimine",
                        color = RouteFitAccent,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                    Text(
                        text = "Kõrge täpsus",
                        modifier = Modifier.padding(top = 4.dp),
                        color = RouteFitTextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            CircleActionButton(
                label = "START",
                containerColor = RouteFitSurfaceVariant,
                borderColor = RouteFitAccent,
                contentColor = RouteFitAccent,
                modifier = Modifier.padding(horizontal = 18.dp)
            ) {
                PlayIcon(
                    modifier = Modifier.size(28.dp),
                    color = RouteFitAccent
                )
            }

            CircleActionButton(
                label = "STOP",
                containerColor = Color(0xFFB98282),
                borderColor = Color.Transparent,
                contentColor = Color(0xFF4B1515)
            ) {
                StopIcon(
                    modifier = Modifier.size(24.dp),
                    color = Color(0xFF4B1515)
                )
            }
        }
    }
}

@Composable
private fun CircleActionButton(
    label: String,
    containerColor: Color,
    borderColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(76.dp),
            shape = CircleShape,
            color = containerColor,
            border = BorderStroke(4.dp, borderColor)
        ) {
            Box(contentAlignment = Alignment.Center) {
                icon()
            }
        }
        Text(
            text = label,
            modifier = Modifier.padding(top = 8.dp),
            color = contentColor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun StylizedMapBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(RouteFitSurfaceVariant)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        RouteFitSurfaceVariant,
                        RouteFitSurfaceVariant.copy(alpha = 0.72f),
                        RouteFitBackground.copy(alpha = 0.94f)
                    )
                )
            )

            val roadColor = RouteFitTextSecondary.copy(alpha = 0.18f)
            val fineLine = RouteFitTextSecondary.copy(alpha = 0.08f)

            repeat(8) { index ->
                val y = size.height * (index + 1) / 9f
                drawLine(
                    color = fineLine,
                    start = androidx.compose.ui.geometry.Offset(0f, y),
                    end = androidx.compose.ui.geometry.Offset(size.width, y + (index % 2) * 34f),
                    strokeWidth = 1.dp.toPx()
                )
            }

            repeat(6) { index ->
                val x = size.width * (index + 1) / 7f
                drawLine(
                    color = fineLine,
                    start = androidx.compose.ui.geometry.Offset(x, 0f),
                    end = androidx.compose.ui.geometry.Offset(x - 42f, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }

            drawLine(
                color = roadColor,
                start = androidx.compose.ui.geometry.Offset(size.width * 0.08f, 0f),
                end = androidx.compose.ui.geometry.Offset(size.width * 0.58f, size.height),
                strokeWidth = 16.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = roadColor,
                start = androidx.compose.ui.geometry.Offset(0f, size.height * 0.42f),
                end = androidx.compose.ui.geometry.Offset(size.width, size.height * 0.26f),
                strokeWidth = 12.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = roadColor,
                start = androidx.compose.ui.geometry.Offset(size.width * 0.42f, 0f),
                end = androidx.compose.ui.geometry.Offset(size.width * 0.78f, size.height),
                strokeWidth = 9.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun MyLocationIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        drawCircle(color = color.copy(alpha = 0.24f), radius = size.minDimension * 0.44f)
        drawCircle(color = color, radius = size.minDimension * 0.18f, style = stroke)
        drawLine(color = color, start = androidx.compose.ui.geometry.Offset(center.x, 0f), end = androidx.compose.ui.geometry.Offset(center.x, size.height * 0.24f), strokeWidth = 2.5.dp.toPx(), cap = StrokeCap.Round)
        drawLine(color = color, start = androidx.compose.ui.geometry.Offset(center.x, size.height * 0.76f), end = androidx.compose.ui.geometry.Offset(center.x, size.height), strokeWidth = 2.5.dp.toPx(), cap = StrokeCap.Round)
        drawLine(color = color, start = androidx.compose.ui.geometry.Offset(0f, center.y), end = androidx.compose.ui.geometry.Offset(size.width * 0.24f, center.y), strokeWidth = 2.5.dp.toPx(), cap = StrokeCap.Round)
        drawLine(color = color, start = androidx.compose.ui.geometry.Offset(size.width * 0.76f, center.y), end = androidx.compose.ui.geometry.Offset(size.width, center.y), strokeWidth = 2.5.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
private fun PlayIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(size.width * 0.32f, size.height * 0.2f)
            lineTo(size.width * 0.32f, size.height * 0.8f)
            lineTo(size.width * 0.78f, size.height * 0.5f)
            close()
        }
        drawPath(path = path, color = color)
    }
}

@Composable
private fun StopIcon(modifier: Modifier = Modifier, color: Color) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(color)
    )
}
