package com.example.routefitnative.ui.screens.statistics

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
fun StatisticsScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = RouteFitBackground,
        bottomBar = {
            BottomNavigationBar(selectedItem = BottomNavItem.Statistics)
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

                Text(
                    text = "Nädala aktiivsus",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    color = RouteFitTextPrimary,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 36.sp,
                        lineHeight = 42.sp
                    ),
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Sinu tegelikud andmed",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    color = RouteFitTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )

                WeeklyStatsCard(
                    modifier = Modifier.padding(top = 22.dp)
                )

                ActivityChartCard(
                    modifier = Modifier.padding(top = 18.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SmallStatsCard(
                        title = "Kalorid",
                        value = "327 kcal",
                        description = "põletatud",
                        modifier = Modifier.weight(1f)
                    )
                    SmallStatsCard(
                        title = "Vahemaa",
                        value = "6.4 km",
                        description = "jälgitud",
                        modifier = Modifier.weight(1f)
                    )
                }

                SectionTitle(
                    text = "Eesmärgid ja tulemused",
                    modifier = Modifier.padding(top = 24.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AchievementCard(
                        title = "Esimene marsruut",
                        detail = "Alustatud",
                        modifier = Modifier.weight(1f)
                    )
                    AchievementCard(
                        title = "Kalorid",
                        detail = "327 kcal",
                        modifier = Modifier.weight(1f)
                    )
                }

                SectionTitle(
                    text = "Viimased marsruudid",
                    modifier = Modifier.padding(top = 24.dp)
                )

                RecentRouteRow(
                    date = "02.06",
                    title = "Uus marsruut",
                    distance = "3.8 km",
                    duration = "38 min",
                    calories = "195 kcal",
                    modifier = Modifier.padding(top = 14.dp)
                )
                RecentRouteRow(
                    date = "01.06",
                    title = "Õhtune jalutuskäik",
                    distance = "2.6 km",
                    duration = "27 min",
                    calories = "132 kcal",
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun WeeklyStatsCard(modifier: Modifier = Modifier) {
    RouteFitStatsCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Keskmised sammud päevas",
                    color = RouteFitTextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "3 715",
                    modifier = Modifier.padding(top = 18.dp),
                    color = RouteFitTextPrimary,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 54.sp,
                        lineHeight = 60.sp
                    ),
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
                Text(
                    text = "sammu",
                    color = RouteFitTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Surface(
                modifier = Modifier.size(104.dp),
                shape = CircleShape,
                color = RouteFitAccent.copy(alpha = 0.12f),
                border = BorderStroke(2.dp, RouteFitAccent.copy(alpha = 0.7f))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "0/7",
                        color = RouteFitAccent,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "päeva eesmärk täidetud",
                        color = RouteFitTextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityChartCard(modifier: Modifier = Modifier) {
    RouteFitStatsCard(modifier = modifier) {
        Text(
            text = "Aktiivsus päevade kaupa",
            color = RouteFitTextPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )
        ActivityLineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(132.dp)
                .padding(top = 20.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("E", "T", "K", "N", "R", "L", "P").forEachIndexed { index, day ->
                Text(
                    text = day,
                    color = if (index == 1) RouteFitAccent else RouteFitTextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun ActivityLineChart(modifier: Modifier = Modifier) {
    val values = listOf(0.18f, 0.54f, 0.32f, 0.7f, 0.46f, 0.78f, 0.58f)

    Canvas(modifier = modifier) {
        val horizontalPadding = 12.dp.toPx()
        val usableWidth = size.width - horizontalPadding * 2f
        val points = values.mapIndexed { index, value ->
            Offset(
                x = horizontalPadding + usableWidth * index / (values.lastIndex.coerceAtLeast(1)),
                y = size.height - (size.height * value)
            )
        }
        val mutedStroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        val accentStroke = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)

        points.zipWithNext().forEach { (start, end) ->
            drawLine(
                color = RouteFitTextSecondary.copy(alpha = 0.26f),
                start = start,
                end = end,
                strokeWidth = mutedStroke.width,
                cap = StrokeCap.Round
            )
        }
        points.take(2).zipWithNext().forEach { (start, end) ->
            drawLine(
                color = RouteFitAccent,
                start = start,
                end = end,
                strokeWidth = accentStroke.width,
                cap = StrokeCap.Round
            )
        }
        points.forEachIndexed { index, point ->
            val selected = index == 1
            drawCircle(
                color = if (selected) RouteFitAccent.copy(alpha = 0.18f) else Color.Transparent,
                radius = if (selected) 15.dp.toPx() else 0f,
                center = point
            )
            drawCircle(
                color = if (selected) RouteFitAccent else RouteFitTextSecondary.copy(alpha = 0.5f),
                radius = if (selected) 7.dp.toPx() else 5.dp.toPx(),
                center = point
            )
        }
    }
}

@Composable
private fun SmallStatsCard(
    title: String,
    value: String,
    description: String,
    modifier: Modifier = Modifier
) {
    RouteFitStatsCard(modifier = modifier) {
        Text(
            text = title,
            color = RouteFitAccent,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = value,
            modifier = Modifier.padding(top = 12.dp),
            color = RouteFitTextPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
        Text(
            text = description,
            modifier = Modifier.padding(top = 4.dp),
            color = RouteFitTextSecondary,
            style = MaterialTheme.typography.bodySmall
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
private fun AchievementCard(
    title: String,
    detail: String,
    modifier: Modifier = Modifier
) {
    RouteFitStatsCard(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(RouteFitAccent.copy(alpha = 0.16f), CircleShape)
                .border(BorderStroke(1.dp, RouteFitAccent.copy(alpha = 0.7f)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title.first().toString(),
                color = RouteFitAccent,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Text(
            text = title,
            modifier = Modifier.padding(top = 14.dp),
            color = RouteFitTextPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = detail,
            modifier = Modifier.padding(top = 6.dp),
            color = RouteFitTextSecondary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun RecentRouteRow(
    date: String,
    title: String,
    distance: String,
    duration: String,
    calories: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = RouteFitSurface.copy(alpha = 0.94f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, RouteFitOutline)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .size(56.dp)
                    .background(RouteFitSurfaceVariant.copy(alpha = 0.78f), RoundedCornerShape(16.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = date,
                    color = RouteFitAccent,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text(
                    text = title,
                    color = RouteFitTextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
                Text(
                    text = "$distance • $duration • $calories",
                    modifier = Modifier.padding(top = 6.dp),
                    color = RouteFitTextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun RouteFitStatsCard(
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
