package com.example.routefitnative.ui.screens.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
fun HistoryScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = RouteFitBackground,
        bottomBar = {
            BottomNavigationBar(selectedItem = BottomNavItem.History)
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
                Text(
                    text = "RouteFit",
                    color = RouteFitAccent,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 48.sp,
                        lineHeight = 54.sp
                    ),
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Ajalugu",
                        color = RouteFitTextPrimary,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 36.sp,
                            lineHeight = 42.sp
                        ),
                        fontWeight = FontWeight.ExtraBold
                    )
                    FilterChip()
                }

                TotalDistanceCard(
                    modifier = Modifier.padding(top = 18.dp)
                )

                RouteHistoryCard(
                    title = "Uus marsruut",
                    date = "02.06 • 08:27",
                    distance = "3.8 km",
                    duration = "38 min",
                    steps = "3 689",
                    calories = "195 kcal",
                    modifier = Modifier.padding(top = 18.dp)
                )

                RouteHistoryCard(
                    title = "Õhtune jalutuskäik",
                    date = "01.06 • 19:12",
                    distance = "2.6 km",
                    duration = "27 min",
                    steps = "2 940",
                    calories = "132 kcal",
                    modifier = Modifier.padding(top = 18.dp)
                )
            }
        }
    }
}

@Composable
private fun FilterChip(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = RouteFitSurfaceVariant.copy(alpha = 0.7f),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, RouteFitOutline)
    ) {
        Text(
            text = "Viimased 30 päeva",
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            color = RouteFitTextSecondary,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
    }
}

@Composable
private fun TotalDistanceCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = RouteFitSurface.copy(alpha = 0.94f),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, RouteFitOutline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "KOGU VAHEMAA",
                    color = RouteFitAccent,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = "19.4 km",
                    modifier = Modifier.padding(top = 12.dp),
                    color = RouteFitTextPrimary,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 42.sp,
                        lineHeight = 48.sp
                    ),
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Surface(
                modifier = Modifier.size(66.dp),
                shape = CircleShape,
                color = RouteFitAccent.copy(alpha = 0.16f),
                border = BorderStroke(2.dp, RouteFitAccent.copy(alpha = 0.72f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    TrendingIcon(
                        modifier = Modifier.size(34.dp),
                        color = RouteFitAccent
                    )
                }
            }
        }
    }
}

@Composable
private fun RouteHistoryCard(
    title: String,
    date: String,
    distance: String,
    duration: String,
    steps: String,
    calories: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = RouteFitSurface.copy(alpha = 0.94f),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, RouteFitOutline)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = RouteFitAccent.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, RouteFitAccent.copy(alpha = 0.6f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        WalkingIcon(
                            modifier = Modifier.size(28.dp),
                            color = RouteFitAccent
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 14.dp)
                ) {
                    Text(
                        text = title,
                        color = RouteFitTextPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                    Text(
                        text = date,
                        modifier = Modifier.padding(top = 4.dp),
                        color = RouteFitTextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                ArrowRightIcon(
                    modifier = Modifier.size(28.dp),
                    color = RouteFitTextSecondary
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 22.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                StatTile(label = "VAHEMAA", value = distance, modifier = Modifier.weight(1f))
                StatTile(label = "KESTUS", value = duration, modifier = Modifier.weight(1f))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                StatTile(label = "SAMMUD", value = steps, modifier = Modifier.weight(1f))
                StatTile(label = "KALORID", value = calories, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(86.dp),
        color = RouteFitSurfaceVariant.copy(alpha = 0.72f),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, RouteFitOutline)
    ) {
        Column(
            modifier = Modifier.padding(PaddingValues(horizontal = 16.dp, vertical = 14.dp)),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                color = RouteFitAccent,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1
            )
            Text(
                text = value,
                modifier = Modifier.padding(top = 8.dp),
                color = RouteFitTextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun TrendingIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val strokeWidth = 3.dp.toPx()
        val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        val path = Path().apply {
            moveTo(size.width * 0.12f, size.height * 0.68f)
            lineTo(size.width * 0.36f, size.height * 0.48f)
            lineTo(size.width * 0.52f, size.height * 0.58f)
            lineTo(size.width * 0.84f, size.height * 0.24f)
        }
        drawPath(path = path, color = color, style = stroke)
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.66f, size.height * 0.24f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.84f, size.height * 0.24f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.84f, size.height * 0.24f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.84f, size.height * 0.42f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun WalkingIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.5.dp.toPx()
        val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        drawCircle(
            color = color,
            radius = size.minDimension * 0.1f,
            center = androidx.compose.ui.geometry.Offset(size.width * 0.52f, size.height * 0.18f)
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.3f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.42f, size.height * 0.55f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.46f, size.height * 0.4f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.26f, size.height * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.48f, size.height * 0.42f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.66f, size.height * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.42f, size.height * 0.55f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.28f, size.height * 0.82f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.42f, size.height * 0.55f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.66f, size.height * 0.82f),
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
            start = androidx.compose.ui.geometry.Offset(size.width * 0.28f, size.height * 0.22f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.66f, size.height * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * 0.66f, size.height * 0.5f),
            end = androidx.compose.ui.geometry.Offset(size.width * 0.28f, size.height * 0.78f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}
