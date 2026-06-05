package com.example.routefitnative.ui.screens.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.routefitnative.data.AuthRepository
import com.example.routefitnative.data.UserRepository
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
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    onBottomNavItemClick: (BottomNavItem) -> Unit = {},
    onRouteClick: (String) -> Unit = {}
)
 {
    val authRepository = remember { AuthRepository() }
    val userRepository = remember { UserRepository() }
    val db = remember { FirebaseFirestore.getInstance() }

    var uiState by remember { mutableStateOf(HistoryUiState()) }

    LaunchedEffect(Unit) {
        val currentUser = authRepository.currentUser

        if (currentUser == null) {
            uiState = uiState.copy(errorMessage = "Kasutaja pole sisse logitud.")
            return@LaunchedEffect
        }

        try {
            val settings = userRepository.getUserSettings(currentUser.uid)
            val routes = loadHistoryRoutes(db, currentUser.uid)
                .filter { it.timeMillis >= thirtyDaysAgoMillis() }
                .sortedByDescending { it.timeMillis }

            uiState = HistoryUiState(
                distanceUnit = settings.distanceUnit,
                totalDistanceKm = routes.sumOf { it.distanceKm },
                routes = routes,
                errorMessage = ""
            )
        } catch (e: Exception) {
            uiState = uiState.copy(
                errorMessage = e.message ?: "Ajaloo laadimine ebaõnnestus."
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = RouteFitBackground,
        bottomBar = {
            BottomNavigationBar(
                selectedItem = BottomNavItem.History,
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
                    totalDistanceKm = uiState.totalDistanceKm,
                    distanceUnit = uiState.distanceUnit,
                    modifier = Modifier.padding(top = 18.dp)
                )

                if (uiState.errorMessage.isNotBlank()) {
                    Text(
                        text = uiState.errorMessage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        color = RouteFitAccent,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }

                if (uiState.routes.isEmpty()) {
                    Text(
                        text = "Viimase 30 päeva jooksul marsruute pole.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 22.dp),
                        color = RouteFitTextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                } else {
                    uiState.routes.forEach { route ->
                        RouteHistoryCard(
                            title = route.title,
                            date = formatRouteDate(route.timeMillis),
                            distance = formatDistance(route.distanceKm, uiState.distanceUnit),
                            duration = formatDuration(route.durationSeconds),
                            steps = formatSteps(route.steps),
                            calories = "${formatSteps(route.calories)} kcal",
                            onClick = {
                                onRouteClick(route.routeId)
                            },
                            modifier = Modifier.padding(top = 18.dp)
                        )
                    }
                }
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
private fun TotalDistanceCard(
    totalDistanceKm: Double,
    distanceUnit: String,
    modifier: Modifier = Modifier
) {
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
                    text = formatDistance(totalDistanceKm, distanceUnit),
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
            start = Offset(size.width * 0.66f, size.height * 0.24f),
            end = Offset(size.width * 0.84f, size.height * 0.24f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.84f, size.height * 0.24f),
            end = Offset(size.width * 0.84f, size.height * 0.42f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun WalkingIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.5.dp.toPx()

        drawCircle(
            color = color,
            radius = size.minDimension * 0.1f,
            center = Offset(size.width * 0.52f, size.height * 0.18f)
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.5f, size.height * 0.3f),
            end = Offset(size.width * 0.42f, size.height * 0.55f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.46f, size.height * 0.4f),
            end = Offset(size.width * 0.26f, size.height * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.48f, size.height * 0.42f),
            end = Offset(size.width * 0.66f, size.height * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.42f, size.height * 0.55f),
            end = Offset(size.width * 0.28f, size.height * 0.82f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.42f, size.height * 0.55f),
            end = Offset(size.width * 0.66f, size.height * 0.82f),
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
            start = Offset(size.width * 0.28f, size.height * 0.22f),
            end = Offset(size.width * 0.66f, size.height * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.66f, size.height * 0.5f),
            end = Offset(size.width * 0.28f, size.height * 0.78f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

private data class HistoryUiState(
    val distanceUnit: String = "km",
    val totalDistanceKm: Double = 0.0,
    val routes: List<HistoryRouteSummary> = emptyList(),
    val errorMessage: String = ""
)

private data class HistoryRouteSummary(
    val routeId: String,
    val title: String,
    val timeMillis: Long,
    val distanceKm: Double,
    val durationSeconds: Long,
    val steps: Int,
    val calories: Int
)

private suspend fun loadHistoryRoutes(
    db: FirebaseFirestore,
    uid: String
): List<HistoryRouteSummary> {
    val snapshot = db.collection("users")
        .document(uid)
        .collection("routes")
        .get()
        .await()

    return snapshot.documents.map { document ->
        val timeMillis = document.getTimeMillis("startTime")
            ?: document.getTimeMillis("createdAt")
            ?: System.currentTimeMillis()

        HistoryRouteSummary(
            routeId = document.getString("routeId") ?: document.id,
            title = document.getString("title")
                ?: document.getString("name")
                ?: "Marsruut",
            timeMillis = timeMillis,
            distanceKm = document.getNumberDouble("distanceKm")
                .takeIf { it > 0.0 }
                ?: document.getNumberDouble("distance"),
            durationSeconds = document.getNumberLong("durationSeconds")
                .takeIf { it > 0L }
                ?: document.getNumberLong("duration"),
            steps = document.getNumberInt("steps"),
            calories = document.getNumberInt("calories")
        )
    }
}

private fun DocumentSnapshot.getNumberInt(fieldName: String): Int {
    val value = get(fieldName)

    return when (value) {
        is Number -> value.toInt()
        else -> 0
    }
}

private fun DocumentSnapshot.getNumberLong(fieldName: String): Long {
    val value = get(fieldName)

    return when (value) {
        is Number -> value.toLong()
        else -> 0L
    }
}

private fun DocumentSnapshot.getNumberDouble(fieldName: String): Double {
    val value = get(fieldName)

    return when (value) {
        is Number -> value.toDouble()
        else -> 0.0
    }
}

private fun DocumentSnapshot.getTimeMillis(fieldName: String): Long? {
    val value = get(fieldName)

    return when (value) {
        is Timestamp -> value.toDate().time
        is Long -> value
        is Int -> value.toLong()
        is Double -> value.toLong()
        else -> null
    }
}

private fun thirtyDaysAgoMillis(): Long {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -30)
    return calendar.timeInMillis
}

private fun formatRouteDate(timeMillis: Long): String {
    return SimpleDateFormat("dd.MM • HH:mm", Locale.US).format(timeMillis)
}

private fun formatSteps(value: Int): String {
    return String.format(Locale.US, "%,d", value).replace(",", " ")
}

private fun formatDistance(distanceKm: Double, distanceUnit: String): String {
    return if (distanceUnit == "mi") {
        val miles = distanceKm * 0.621371
        "${formatOneDecimal(miles)} mi"
    } else {
        "${formatOneDecimal(distanceKm)} km"
    }
}

private fun formatOneDecimal(value: Double): String {
    return String.format(Locale.US, "%.1f", value)
}

private fun formatDuration(durationSeconds: Long): String {
    val totalMinutes = (durationSeconds / 60).coerceAtLeast(0)

    return if (totalMinutes < 60) {
        "$totalMinutes min"
    } else {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        "${hours}h ${minutes}min"
    }
}