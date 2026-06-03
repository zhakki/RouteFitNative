package com.example.routefitnative.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
fun HomeScreen(
    modifier: Modifier = Modifier,
    onBottomNavItemClick: (BottomNavItem) -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = RouteFitBackground,
        bottomBar = {
            BottomNavigationBar(
                selectedItem = BottomNavItem.Home,
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
                                RouteFitAccent.copy(alpha = 0.14f),
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
                    .padding(top = 28.dp, bottom = 24.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "RouteFit",
                    color = RouteFitAccent,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 52.sp,
                        lineHeight = 58.sp
                    ),
                    fontStyle = FontStyle.Italic
                )

                StepsProgressCard(
                    modifier = Modifier.padding(top = 28.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    MetricCard(
                        label = "PÄEVA EESMÄRK",
                        value = "24 000 sammu",
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        label = "JÄÄNUD",
                        value = "20 311",
                        modifier = Modifier.weight(1f)
                    )
                }

                WeeklyProgressCard(
                    modifier = Modifier.padding(top = 18.dp)
                )

                LastRouteCard(
                    modifier = Modifier.padding(top = 18.dp)
                )

                NewRouteButton(
                    modifier = Modifier.padding(top = 22.dp)
                )
            }
        }
    }
}

@Composable
private fun StepsProgressCard(modifier: Modifier = Modifier) {
    RouteFitCard(modifier = modifier) {
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularStepsProgress(
                progress = 0.15f,
                modifier = Modifier.size(228.dp)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "3 689",
                    color = RouteFitTextPrimary,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "/ 24 000 sammu",
                    color = RouteFitTextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }

        Text(
            text = "15% valmis",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 26.dp),
            color = RouteFitAccent,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Text(
            text = "20 311 sammu jäänud",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            color = RouteFitTextSecondary,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun WeeklyProgressCard(modifier: Modifier = Modifier) {
    RouteFitCard(modifier = modifier) {
        Text(
            text = "Nädala progress",
            color = RouteFitTextPrimary,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "7 430 / 168 000 sammu",
            modifier = Modifier.padding(top = 10.dp),
            color = RouteFitTextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )

        ProgressTrack(
            progress = 7430f / 168000f,
            modifier = Modifier.padding(top = 20.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            MetricCard(
                label = "VEEL VAJA",
                value = "160 570",
                modifier = Modifier.weight(1f),
                compact = true
            )
            MetricCard(
                label = "PÄEVA KESKMINE",
                value = "3715",
                modifier = Modifier.weight(1f),
                compact = true
            )
        }

        WeekDaysRow(
            modifier = Modifier.padding(top = 22.dp)
        )
    }
}

@Composable
private fun LastRouteCard(modifier: Modifier = Modifier) {
    RouteFitCard(modifier = modifier) {
        Text(
            text = "VIIMANE MARSRUUT",
            color = RouteFitAccent,
            style = MaterialTheme.typography.labelMedium
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RouteValue(label = "MAA", value = "3.8 km")
            RouteValue(label = "AEG", value = "38 min")
            RouteValue(label = "KAL", value = "195 kcal")
        }
    }
}

@Composable
private fun NewRouteButton(modifier: Modifier = Modifier) {
    Button(
        onClick = {},
        modifier = modifier
            .fillMaxWidth()
            .height(66.dp),
        shape = RoundedCornerShape(34.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = RouteFitAccent,
            contentColor = RouteFitOnAccent
        ),
        contentPadding = PaddingValues(horizontal = 24.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = "UUS MARSRUUT",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    Surface(
        modifier = modifier,
        color = RouteFitSurfaceVariant.copy(alpha = 0.72f),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, RouteFitOutline)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = if (compact) 16.dp else 18.dp,
                vertical = if (compact) 16.dp else 20.dp
            )
        ) {
            Text(
                text = label,
                color = RouteFitAccent,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = value,
                modifier = Modifier.padding(top = 8.dp),
                color = RouteFitTextPrimary,
                style = if (compact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun RouteValue(label: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            color = RouteFitTextSecondary,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = value,
            modifier = Modifier.padding(top = 8.dp),
            color = RouteFitTextPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun WeekDaysRow(modifier: Modifier = Modifier) {
    val days = listOf("E", "T", "K", "N", "R", "L", "P")

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        days.forEachIndexed { index, day ->
            val selected = index == 1
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (selected) RouteFitAccent else RouteFitSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    color = if (selected) RouteFitOnAccent else RouteFitTextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun ProgressTrack(progress: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(RouteFitBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(RouteFitAccent)
        )
    }
}

@Composable
private fun CircularStepsProgress(progress: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = 17.dp.toPx()
        val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        val arcSize = size.minDimension - strokeWidth
        val topLeft = androidx.compose.ui.geometry.Offset(strokeWidth / 2f, strokeWidth / 2f)
        val size = androidx.compose.ui.geometry.Size(arcSize, arcSize)

        drawArc(
            color = RouteFitSurfaceVariant,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = size,
            style = stroke
        )
        drawArc(
            color = RouteFitAccent,
            startAngle = -90f,
            sweepAngle = 360f * progress.coerceIn(0f, 1f),
            useCenter = false,
            topLeft = topLeft,
            size = size,
            style = stroke
        )
    }
}

@Composable
private fun RouteFitCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, RouteFitOutline),
                RoundedCornerShape(24.dp)
            ),
        color = RouteFitSurface.copy(alpha = 0.92f),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            content = content
        )
    }
}
