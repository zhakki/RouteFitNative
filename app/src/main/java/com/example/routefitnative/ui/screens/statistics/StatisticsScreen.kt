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
import com.example.routefitnative.ui.components.BottomNavItem
import com.example.routefitnative.ui.components.BottomNavigationBar
import com.example.routefitnative.ui.theme.RouteFitAccent
import com.example.routefitnative.ui.theme.RouteFitBackground
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
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    onBottomNavItemClick: (BottomNavItem) -> Unit = {}
) {
    val authRepository = remember { AuthRepository() }
    val userRepository = remember { UserRepository() }
    val db = remember { FirebaseFirestore.getInstance() }

    var uiState by remember { mutableStateOf(StatisticsUiState()) }

    LaunchedEffect(Unit) {
        val currentUser = authRepository.currentUser

        if (currentUser == null) {
            uiState = uiState.copy(errorMessage = "Kasutaja pole sisse logitud.")
            return@LaunchedEffect
        }

        try {
            val settings = userRepository.getUserSettings(currentUser.uid)
            val routes = loadStatisticRoutes(db, currentUser.uid)
            val summaries = loadStatisticDailySummaries(db, currentUser.uid)

            val weekDates = currentWeekDates()
            val weekRoutes = routes.filter { route ->
                route.date in weekDates
            }

            val routesByDate = weekRoutes.groupBy { it.date }

            val dailySteps = weekDates.map { date ->
                val summarySteps = summaries[date]?.totalSteps ?: 0
                val routeSteps = routesByDate[date]?.sumOf { it.steps } ?: 0

                maxOf(summarySteps, routeSteps)
            }

            val dailyCalories = weekDates.map { date ->
                val summaryCalories = summaries[date]?.calories ?: 0
                val routeCalories = routesByDate[date]?.sumOf { it.calories } ?: 0

                maxOf(summaryCalories, routeCalories)
            }

            val dailyDistanceKm = weekDates.map { date ->
                val summaryDistance = summaries[date]?.distanceKm ?: 0.0
                val routeDistance = routesByDate[date]?.sumOf { it.distanceKm } ?: 0.0

                maxOf(summaryDistance, routeDistance)
            }

            val weekSteps = dailySteps.sum()
            val weekCalories = dailyCalories.sum()
            val weekDistanceKm = dailyDistanceKm.sum()

            val daysPassedThisWeek = (todayWeekIndex() + 1).coerceIn(1, 7)
            val averageSteps = weekSteps / daysPassedThisWeek

            val completedDays = dailySteps.count { it >= settings.dailyStepGoal }

            val recentRoutes = routes
                .sortedByDescending { it.timeMillis }
                .take(3)

            uiState = StatisticsUiState(
                dailyGoal = settings.dailyStepGoal,
                distanceUnit = settings.distanceUnit,
                averageSteps = averageSteps,
                completedGoalDays = completedDays,
                weekCalories = weekCalories,
                weekDistanceKm = weekDistanceKm,
                weekSteps = weekSteps,
                dailySteps = dailySteps,
                recentRoutes = recentRoutes,
                routeCount = weekRoutes.size,
                errorMessage = ""
            )
        } catch (e: Exception) {
            uiState = uiState.copy(
                errorMessage = e.message ?: "Statistika laadimine ebaõnnestus."
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = RouteFitBackground,
        bottomBar = {
            BottomNavigationBar(
                selectedItem = BottomNavItem.Statistics,
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
                    averageSteps = uiState.averageSteps,
                    completedGoalDays = uiState.completedGoalDays,
                    modifier = Modifier.padding(top = 22.dp)
                )

                ActivityChartCard(
                    dailySteps = uiState.dailySteps,
                    dailyGoal = uiState.dailyGoal,
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
                        value = "${formatSteps(uiState.weekCalories)} kcal",
                        description = "põletatud",
                        modifier = Modifier.weight(1f)
                    )
                    SmallStatsCard(
                        title = "Vahemaa",
                        value = formatDistance(uiState.weekDistanceKm, uiState.distanceUnit),
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
                        title = "Marsruudid",
                        detail = "${uiState.routeCount} kokku",
                        modifier = Modifier.weight(1f)
                    )
                    AchievementCard(
                        title = "Kalorid",
                        detail = "${formatSteps(uiState.weekCalories)} kcal",
                        modifier = Modifier.weight(1f)
                    )
                }

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

                SectionTitle(
                    text = "Viimased marsruudid",
                    modifier = Modifier.padding(top = 24.dp)
                )

                if (uiState.recentRoutes.isEmpty()) {
                    Text(
                        text = "Marsruute pole veel salvestatud",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                        color = RouteFitTextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    uiState.recentRoutes.forEachIndexed { index, route ->
                        RecentRouteRow(
                            date = formatShortDate(route.timeMillis),
                            title = route.title,
                            distance = formatDistance(route.distanceKm, uiState.distanceUnit),
                            duration = formatDuration(route.durationSeconds),
                            calories = "${route.calories} kcal",
                            modifier = Modifier.padding(top = if (index == 0) 14.dp else 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyStatsCard(
    averageSteps: Int,
    completedGoalDays: Int,
    modifier: Modifier = Modifier
) {
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
                    text = formatSteps(averageSteps),
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
                        text = "$completedGoalDays/7",
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
private fun ActivityChartCard(
    dailySteps: List<Int>,
    dailyGoal: Int,
    modifier: Modifier = Modifier
) {
    RouteFitStatsCard(modifier = modifier) {
        Text(
            text = "Aktiivsus päevade kaupa",
            color = RouteFitTextPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )
        ActivityLineChart(
            dailySteps = dailySteps,
            dailyGoal = dailyGoal,
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
            val todayIndex = todayWeekIndex()

            listOf("E", "T", "K", "N", "R", "L", "P").forEachIndexed { index, day ->
                Text(
                    text = day,
                    color = if (index == todayIndex) RouteFitAccent else RouteFitTextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun ActivityLineChart(
    dailySteps: List<Int>,
    dailyGoal: Int,
    modifier: Modifier = Modifier
) {
    val maxValue = maxOf(
        dailyGoal,
        dailySteps.maxOrNull() ?: 0,
        1
    )

    val values = dailySteps
        .ifEmpty { List(7) { 0 } }
        .take(7)
        .map { steps ->
            (steps.toFloat() / maxValue.toFloat()).coerceIn(0.04f, 1f)
        }

    val todayIndex = todayWeekIndex()

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

        points.take(todayIndex + 1)
            .zipWithNext()
            .forEach { (start, end) ->
                drawLine(
                    color = RouteFitAccent,
                    start = start,
                    end = end,
                    strokeWidth = accentStroke.width,
                    cap = StrokeCap.Round
                )
            }

        points.forEachIndexed { index, point ->
            val selected = index == todayIndex

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

private data class StatisticsUiState(
    val dailyGoal: Int = 24000,
    val distanceUnit: String = "km",
    val averageSteps: Int = 0,
    val completedGoalDays: Int = 0,
    val weekCalories: Int = 0,
    val weekDistanceKm: Double = 0.0,
    val weekSteps: Int = 0,
    val dailySteps: List<Int> = List(7) { 0 },
    val recentRoutes: List<StatisticRouteSummary> = emptyList(),
    val routeCount: Int = 0,
    val errorMessage: String = ""
)

private data class StatisticDailySummary(
    val date: String,
    val totalSteps: Int,
    val calories: Int,
    val distanceKm: Double,
    val durationSeconds: Long
)

private data class StatisticRouteSummary(
    val title: String,
    val date: String,
    val timeMillis: Long,
    val distanceKm: Double,
    val durationSeconds: Long,
    val steps: Int,
    val calories: Int
)

private suspend fun loadStatisticDailySummaries(
    db: FirebaseFirestore,
    uid: String
): Map<String, StatisticDailySummary> {
    val snapshot = db.collection("users")
        .document(uid)
        .collection("daily_summaries")
        .get()
        .await()

    return snapshot.documents.associate { document ->
        val date = document.getDateString("date") ?: document.id

        date to StatisticDailySummary(
            date = date,
            totalSteps = document.getNumberInt("totalSteps"),
            calories = document.getNumberInt("calories"),
            distanceKm = document.getNumberDouble("distanceKm"),
            durationSeconds = document.getNumberLong("durationSeconds")
        )
    }
}

private suspend fun loadStatisticRoutes(
    db: FirebaseFirestore,
    uid: String
): List<StatisticRouteSummary> {
    val snapshot = db.collection("users")
        .document(uid)
        .collection("routes")
        .get()
        .await()

    return snapshot.documents.map { document ->
        val timeMillis = document.getTimeMillis("startTime")
            ?: document.getTimeMillis("createdAt")
            ?: System.currentTimeMillis()

        StatisticRouteSummary(
            title = document.getString("title") ?: "Marsruut",
            date = dateStringFromMillis(timeMillis),
            timeMillis = timeMillis,
            distanceKm = document.getNumberDouble("distanceKm"),
            durationSeconds = document.getNumberLong("durationSeconds"),
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

private fun currentWeekDates(): List<String> {
    val calendar = Calendar.getInstance()
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

    val mondayOffset = if (dayOfWeek == Calendar.SUNDAY) {
        -6
    } else {
        Calendar.MONDAY - dayOfWeek
    }

    calendar.add(Calendar.DAY_OF_MONTH, mondayOffset)

    return (0..6).map { index ->
        val dayCalendar = calendar.clone() as Calendar
        dayCalendar.add(Calendar.DAY_OF_MONTH, index)
        dateStringFromMillis(dayCalendar.timeInMillis)
    }
}

private fun todayWeekIndex(): Int {
    val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

    return if (dayOfWeek == Calendar.SUNDAY) {
        6
    } else {
        dayOfWeek - Calendar.MONDAY
    }
}

private fun dateStringFromMillis(timeMillis: Long): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(timeMillis)
}

private fun formatShortDate(timeMillis: Long): String {
    return SimpleDateFormat("dd.MM", Locale.US).format(timeMillis)
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
private fun DocumentSnapshot.getDateString(fieldName: String): String? {
    val value = get(fieldName)

    return when (value) {
        is String -> value
        is Timestamp -> dateStringFromMillis(value.toDate().time)
        is Long -> dateStringFromMillis(value)
        is Int -> dateStringFromMillis(value.toLong())
        is Double -> dateStringFromMillis(value.toLong())
        else -> null
    }
}