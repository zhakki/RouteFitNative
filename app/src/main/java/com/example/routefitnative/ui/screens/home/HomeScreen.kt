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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onBottomNavItemClick: (BottomNavItem) -> Unit = {}
) {
    val authRepository = remember { AuthRepository() }
    val userRepository = remember { UserRepository() }
    val db = remember { FirebaseFirestore.getInstance() }

    var uiState by remember { mutableStateOf(HomeUiState()) }

    LaunchedEffect(Unit) {
        val currentUser = authRepository.currentUser

        if (currentUser == null) {
            uiState = uiState.copy(errorMessage = "Kasutaja pole sisse logitud.")
            return@LaunchedEffect
        }

        try {
            val settings = userRepository.getUserSettings(currentUser.uid)
            val routes = loadRoutes(db, currentUser.uid)
            val dailySummaries = loadDailySummaries(db, currentUser.uid)

            val todayDate = todayDateString()
            val weekDates = currentWeekDates()

            val routeStepsByDate = routes
                .groupBy { it.date }
                .mapValues { entry -> entry.value.sumOf { it.steps } }

            val todaySteps = dailySummaries[todayDate]?.totalSteps
                ?: routeStepsByDate[todayDate]
                ?: 0

            val todayCalories = dailySummaries[todayDate]?.calories
                ?: routes.filter { it.date == todayDate }.sumOf { it.calories }

            val weekSteps = weekDates.sumOf { date ->
                dailySummaries[date]?.totalSteps
                    ?: routeStepsByDate[date]
                    ?: 0
            }

            val weeklyGoal = settings.dailyStepGoal * 7
            val daysPassed = todayWeekIndex() + 1
            val averageSteps = if (daysPassed > 0) weekSteps / daysPassed else 0

            val lastRoute = routes.maxByOrNull { it.timeMillis }

            uiState = HomeUiState(
                dailyGoal = settings.dailyStepGoal,
                distanceUnit = settings.distanceUnit,
                todaySteps = todaySteps,
                todayCalories = todayCalories,
                weekSteps = weekSteps,
                weeklyGoal = weeklyGoal,
                averageSteps = averageSteps,
                lastRoute = lastRoute,
                completedWeekDays = weekDates.mapIndexedNotNull { index, date ->
                    val steps = dailySummaries[date]?.totalSteps
                        ?: routeStepsByDate[date]
                        ?: 0

                    if (steps >= settings.dailyStepGoal) index else null
                }.toSet(),
                errorMessage = ""
            )
        } catch (e: Exception) {
            uiState = uiState.copy(
                errorMessage = e.message ?: "Andmete laadimine ebaõnnestus."
            )
        }
    }

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
                    todaySteps = uiState.todaySteps,
                    dailyGoal = uiState.dailyGoal,
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
                        value = formatSteps(uiState.dailyGoal),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        label = "JÄÄNUD",
                        value = formatSteps(uiState.remainingSteps),
                        modifier = Modifier.weight(1f)
                    )
                }

                WeeklyProgressCard(
                    weekSteps = uiState.weekSteps,
                    weeklyGoal = uiState.weeklyGoal,
                    averageSteps = uiState.averageSteps,
                    completedWeekDays = uiState.completedWeekDays,
                    modifier = Modifier.padding(top = 18.dp)
                )

                LastRouteCard(
                    lastRoute = uiState.lastRoute,
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

                NewRouteButton(
                    modifier = Modifier.padding(top = 22.dp),
                    onClick = {
                        onBottomNavItemClick(BottomNavItem.Map)
                    }
                )
            }
        }
    }
}

@Composable
private fun StepsProgressCard(
    todaySteps: Int,
    dailyGoal: Int,
    modifier: Modifier = Modifier
) {
    val safeGoal = dailyGoal.coerceAtLeast(1)
    val progress = (todaySteps.toFloat() / safeGoal.toFloat()).coerceIn(0f, 1f)
    val percent = (progress * 100).roundToInt()
    val remaining = (dailyGoal - todaySteps).coerceAtLeast(0)

    RouteFitCard(modifier = modifier) {
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularStepsProgress(
                progress = progress,
                modifier = Modifier.size(228.dp)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatSteps(todaySteps),
                    color = RouteFitTextPrimary,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "/ ${formatSteps(dailyGoal)} sammu",
                    color = RouteFitTextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }

        Text(
            text = "$percent% valmis",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 26.dp),
            color = RouteFitAccent,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Text(
            text = "${formatSteps(remaining)} sammu jäänud",
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
private fun WeeklyProgressCard(
    weekSteps: Int,
    weeklyGoal: Int,
    averageSteps: Int,
    completedWeekDays: Set<Int>,
    modifier: Modifier = Modifier
) {
    val safeWeeklyGoal = weeklyGoal.coerceAtLeast(1)
    val remaining = (weeklyGoal - weekSteps).coerceAtLeast(0)

    RouteFitCard(modifier = modifier) {
        Text(
            text = "Nädala progress",
            color = RouteFitTextPrimary,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "${formatSteps(weekSteps)} / ${formatSteps(weeklyGoal)} sammu",
            modifier = Modifier.padding(top = 10.dp),
            color = RouteFitTextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )

        ProgressTrack(
            progress = weekSteps.toFloat() / safeWeeklyGoal.toFloat(),
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
                value = formatSteps(remaining),
                modifier = Modifier.weight(1f),
                compact = true
            )
            MetricCard(
                label = "PÄEVA KESKMINE",
                value = formatSteps(averageSteps),
                modifier = Modifier.weight(1f),
                compact = true
            )
        }

        WeekDaysRow(
            completedWeekDays = completedWeekDays,
            modifier = Modifier.padding(top = 22.dp)
        )
    }
}

@Composable
private fun LastRouteCard(
    lastRoute: HomeRouteSummary?,
    distanceUnit: String,
    modifier: Modifier = Modifier
) {
    RouteFitCard(modifier = modifier) {
        Text(
            text = "VIIMANE MARSRUUT",
            color = RouteFitAccent,
            style = MaterialTheme.typography.labelMedium
        )

        if (lastRoute == null) {
            Text(
                text = "Marsruute pole veel salvestatud",
                modifier = Modifier.padding(top = 18.dp),
                color = RouteFitTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RouteValue(
                    label = "MAA",
                    value = formatDistance(lastRoute.distanceKm, distanceUnit)
                )
                RouteValue(
                    label = "AEG",
                    value = formatDuration(lastRoute.durationSeconds)
                )
                RouteValue(
                    label = "KAL",
                    value = "${lastRoute.calories} kcal"
                )
            }
        }
    }
}

@Composable
private fun NewRouteButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
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
private fun WeekDaysRow(
    completedWeekDays: Set<Int>,
    modifier: Modifier = Modifier
) {
    val days = listOf("E", "T", "K", "N", "R", "L", "P")
    val todayIndex = todayWeekIndex()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        days.forEachIndexed { index, day ->
            val selected = index == todayIndex || completedWeekDays.contains(index)

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

private data class HomeUiState(
    val dailyGoal: Int = 24000,
    val distanceUnit: String = "km",
    val todaySteps: Int = 0,
    val todayCalories: Int = 0,
    val weekSteps: Int = 0,
    val weeklyGoal: Int = 168000,
    val averageSteps: Int = 0,
    val lastRoute: HomeRouteSummary? = null,
    val completedWeekDays: Set<Int> = emptySet(),
    val errorMessage: String = ""
) {
    val remainingSteps: Int
        get() = (dailyGoal - todaySteps).coerceAtLeast(0)
}

private data class HomeDailySummary(
    val date: String,
    val totalSteps: Int,
    val calories: Int,
    val distanceKm: Double,
    val durationSeconds: Long
)

private data class HomeRouteSummary(
    val title: String,
    val date: String,
    val timeMillis: Long,
    val distanceKm: Double,
    val durationSeconds: Long,
    val steps: Int,
    val calories: Int
)

private suspend fun loadDailySummaries(
    db: FirebaseFirestore,
    uid: String
): Map<String, HomeDailySummary> {
    val snapshot = db.collection("users")
        .document(uid)
        .collection("daily_summaries")
        .get()
        .await()

    return snapshot.documents.associate { document ->
        val date = document.getString("date") ?: document.id

        date to HomeDailySummary(
            date = date,
            totalSteps = document.getNumberInt("totalSteps"),
            calories = document.getNumberInt("calories"),
            distanceKm = document.getNumberDouble("distanceKm"),
            durationSeconds = document.getNumberLong("durationSeconds")
        )
    }
}

private suspend fun loadRoutes(
    db: FirebaseFirestore,
    uid: String
): List<HomeRouteSummary> {
    val snapshot = db.collection("users")
        .document(uid)
        .collection("routes")
        .get()
        .await()

    return snapshot.documents.map { document ->
        val timeMillis = document.getTimeMillis("startTime")
            ?: document.getTimeMillis("createdAt")
            ?: System.currentTimeMillis()

        HomeRouteSummary(
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

private fun todayDateString(): String {
    return dateStringFromMillis(System.currentTimeMillis())
}

private fun dateStringFromMillis(timeMillis: Long): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(timeMillis)
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