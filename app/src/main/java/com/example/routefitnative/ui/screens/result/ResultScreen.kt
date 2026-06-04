package com.example.routefitnative.ui.screens.result

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.routefitnative.ui.theme.RouteFitAccent
import com.example.routefitnative.ui.theme.RouteFitBackground
import com.example.routefitnative.ui.theme.RouteFitOnAccent
import com.example.routefitnative.ui.theme.RouteFitOutline
import com.example.routefitnative.ui.theme.RouteFitSurface
import com.example.routefitnative.ui.theme.RouteFitSurfaceVariant
import com.example.routefitnative.ui.theme.RouteFitTextPrimary
import com.example.routefitnative.ui.theme.RouteFitTextSecondary
import com.example.routefitnative.viewmodel.TrackingViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onBackHomeClick: () -> Unit = {},
    trackingViewModel: TrackingViewModel = viewModel()
) {
    val lastRoute by trackingViewModel.lastSavedRoute.collectAsState()

    // Formatters
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy • HH:mm")
        .withZone(ZoneId.systemDefault())

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
                .padding(top = 30.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ResultTopBar(onBackClick = onBackClick)

            Text(
                text = "MARSRUUT LÕPETATUD",
                modifier = Modifier.padding(top = 28.dp),
                color = RouteFitAccent,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = lastRoute?.title ?: "Treening lõpetatud",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                color = RouteFitTextPrimary,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 36.sp,
                    lineHeight = 42.sp
                ),
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = lastRoute?.let { dateFormatter.format(Instant.ofEpochMilli(it.endTime)) } ?: "--.--.---- • --:--",
                modifier = Modifier.padding(top = 8.dp),
                color = RouteFitTextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            QuickSummary(
                modifier = Modifier.padding(top = 24.dp),
                distanceKm = lastRoute?.distanceKm ?: 0.0,
                durationSeconds = lastRoute?.durationSeconds ?: 0,
                steps = lastRoute?.steps ?: 0
            )

            RoutePreviewCard(
                modifier = Modifier.padding(top = 18.dp),
                distanceKm = lastRoute?.distanceKm ?: 0.0
            )

            StatsGrid(
                modifier = Modifier.padding(top = 18.dp),
                distanceKm = lastRoute?.distanceKm ?: 0.0,
                durationSeconds = lastRoute?.durationSeconds ?: 0,
                steps = lastRoute?.steps ?: 0,
                calories = lastRoute?.calories ?: 0,
                averageSpeed = lastRoute?.averageSpeed ?: 0.0,
                startTime = lastRoute?.startTime ?: 0L,
                endTime = lastRoute?.endTime ?: 0L
            )

            SavedInfoCard(
                modifier = Modifier.padding(top = 18.dp)
            )

            Button(
                onClick = onBackHomeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .height(66.dp),
                shape = RoundedCornerShape(34.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RouteFitAccent,
                    contentColor = RouteFitOnAccent
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "Tagasi avalehele",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun ResultTopBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
    ) {
        Surface(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(46.dp),
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
            text = "RouteFit",
            modifier = Modifier.align(Alignment.Center),
            color = RouteFitAccent,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 44.sp,
                lineHeight = 50.sp
            ),
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun QuickSummary(
    modifier: Modifier = Modifier,
    distanceKm: Double,
    durationSeconds: Int,
    steps: Int
) {
    RouteFitResultCard(
        modifier = modifier,
        contentPadding = 18.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SummaryItem(label = "Vahemaa", value = "%.1f km".format(distanceKm), modifier = Modifier.weight(1f))
            SummaryItem(label = "Kestus", value = "%d min".format(durationSeconds / 60), modifier = Modifier.weight(1f))
            SummaryItem(label = "Sammud", value = steps.toString(), modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun SummaryItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = RouteFitTextSecondary,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
        Text(
            text = value,
            modifier = Modifier.padding(top = 8.dp),
            color = RouteFitAccent,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
    }
}

@Composable
private fun RoutePreviewCard(
    modifier: Modifier = Modifier,
    distanceKm: Double
) {
    RouteFitResultCard(modifier = modifier) {
        Text(
            text = "Marsruudi eelvaade",
            color = RouteFitTextPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(228.dp)
                .padding(top = 18.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(RouteFitSurfaceVariant)
                .border(BorderStroke(1.dp, RouteFitOutline), RoundedCornerShape(22.dp))
        ) {
            ResultMapBackground(modifier = Modifier.matchParentSize())
            RoutePathPreview(modifier = Modifier.matchParentSize())

            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                color = RouteFitBackground.copy(alpha = 0.78f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, RouteFitOutline)
            ) {
                Text(
                    text = "%.1f km".format(distanceKm),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    color = RouteFitAccent,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun StatsGrid(
    modifier: Modifier = Modifier,
    distanceKm: Double,
    durationSeconds: Int,
    steps: Int,
    calories: Int,
    averageSpeed: Double,
    startTime: Long,
    endTime: Long
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        .withZone(ZoneId.systemDefault())

    val stats = listOf(
        "Vahemaa" to "%.1f km".format(distanceKm),
        "Kestus" to "%d min".format(durationSeconds / 60),
        "Sammud" to steps.toString(),
        "Kalorid" to "%d kcal".format(calories),
        "Keskmine kiirus" to "%.1f km/h".format(averageSpeed),
        "Algus/Lõpp aeg" to "${timeFormatter.format(Instant.ofEpochMilli(startTime))} / ${timeFormatter.format(Instant.ofEpochMilli(endTime))}"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        stats.chunked(2).forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = if (rowIndex == 0) 0.dp else 14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                rowItems.forEach { stat ->
                    StatTile(
                        label = stat.first,
                        value = stat.second,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    RouteFitResultCard(
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
private fun SavedInfoCard(modifier: Modifier = Modifier) {
    RouteFitResultCard(
        modifier = modifier,
        contentPadding = 20.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = RouteFitAccent.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, RouteFitAccent.copy(alpha = 0.7f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CheckIcon(
                        modifier = Modifier.size(24.dp),
                        color = RouteFitAccent
                    )
                }
            }
            Text(
                text = "Marsruut on salvestatud ajalukku",
                modifier = Modifier.padding(start = 14.dp),
                color = RouteFitTextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun ResultMapBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    RouteFitSurfaceVariant,
                    RouteFitSurfaceVariant.copy(alpha = 0.76f),
                    RouteFitBackground.copy(alpha = 0.9f)
                )
            )
        )

        val grid = RouteFitTextSecondary.copy(alpha = 0.08f)
        val road = RouteFitTextSecondary.copy(alpha = 0.18f)

        repeat(7) { index ->
            val y = size.height * (index + 1) / 8f
            drawLine(
                color = grid,
                start = Offset(0f, y),
                end = Offset(size.width, y + if (index % 2 == 0) 22f else -18f),
                strokeWidth = 1.dp.toPx()
            )
        }
        repeat(6) { index ->
            val x = size.width * (index + 1) / 7f
            drawLine(
                color = grid,
                start = Offset(x, 0f),
                end = Offset(x - 34f, size.height),
                strokeWidth = 1.dp.toPx()
            )
        }

        drawLine(
            color = road,
            start = Offset(size.width * 0.08f, size.height * 0.84f),
            end = Offset(size.width * 0.86f, size.height * 0.12f),
            strokeWidth = 12.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = road,
            start = Offset(0f, size.height * 0.42f),
            end = Offset(size.width, size.height * 0.58f),
            strokeWidth = 10.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun RoutePathPreview(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(size.width * 0.18f, size.height * 0.72f)
            cubicTo(
                size.width * 0.28f,
                size.height * 0.44f,
                size.width * 0.5f,
                size.height * 0.82f,
                size.width * 0.62f,
                size.height * 0.48f
            )
            cubicTo(
                size.width * 0.7f,
                size.height * 0.24f,
                size.width * 0.84f,
                size.height * 0.38f,
                size.width * 0.78f,
                size.height * 0.18f
            )
        }

        drawPath(
            path = path,
            color = RouteFitAccent.copy(alpha = 0.28f),
            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
        )
        drawPath(
            path = path,
            color = RouteFitAccent,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
        drawCircle(
            color = RouteFitAccent,
            radius = 7.dp.toPx(),
            center = Offset(size.width * 0.18f, size.height * 0.72f)
        )
        drawCircle(
            color = RouteFitTextPrimary,
            radius = 7.dp.toPx(),
            center = Offset(size.width * 0.78f, size.height * 0.18f)
        )
    }
}

@Composable
private fun RouteFitResultCard(
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
private fun CheckIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val strokeWidth = 3.dp.toPx()
        drawLine(
            color = color,
            start = Offset(size.width * 0.22f, size.height * 0.52f),
            end = Offset(size.width * 0.42f, size.height * 0.72f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.42f, size.height * 0.72f),
            end = Offset(size.width * 0.78f, size.height * 0.3f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}
