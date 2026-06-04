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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.routefitnative.ui.theme.RouteFitAccent
import com.example.routefitnative.ui.theme.RouteFitBackground
import com.example.routefitnative.ui.theme.RouteFitOnAccent
import com.example.routefitnative.ui.theme.RouteFitOutline
import com.example.routefitnative.ui.theme.RouteFitSurface
import com.example.routefitnative.ui.theme.RouteFitSurfaceVariant
import com.example.routefitnative.ui.theme.RouteFitTextPrimary
import com.example.routefitnative.ui.theme.RouteFitTextSecondary
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onBackHomeClick: () -> Unit = {}
) {
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }

    var route by remember { mutableStateOf<ResultRouteSummary?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            errorMessage = "Kasutaja pole sisse logitud."
            return@LaunchedEffect
        }

        try {
            route = loadLastSavedRoute(db, currentUser.uid)
            errorMessage = ""

            if (route == null) {
                errorMessage = "Viimast marsruuti ei leitud."
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "Marsruudi laadimine ebaõnnestus."
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
                text = "Treening lõpetatud",
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
                text = route?.let { formatFullDate(it.endTime) } ?: "Andmeid laaditakse...",
                modifier = Modifier.padding(top = 8.dp),
                color = RouteFitTextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            QuickSummary(
                route = route,
                modifier = Modifier.padding(top = 24.dp)
            )

            RoutePreviewCard(
                route = route,
                modifier = Modifier.padding(top = 18.dp)
            )

            StatsGrid(
                route = route,
                modifier = Modifier.padding(top = 18.dp)
            )

            SavedInfoCard(
                routeFound = route != null,
                modifier = Modifier.padding(top = 18.dp)
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
    route: ResultRouteSummary?,
    modifier: Modifier = Modifier
) {
    RouteFitResultCard(
        modifier = modifier,
        contentPadding = 18.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SummaryItem(
                label = "Vahemaa",
                value = formatDistance(route?.distanceKm ?: 0.0),
                modifier = Modifier.weight(1f)
            )
            SummaryItem(
                label = "Kestus",
                value = formatDuration(route?.durationSeconds ?: 0),
                modifier = Modifier.weight(1f)
            )
            SummaryItem(
                label = "Sammud",
                value = formatSteps(route?.steps ?: 0),
                modifier = Modifier.weight(1f)
            )
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
    route: ResultRouteSummary?,
    modifier: Modifier = Modifier
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
                    text = formatDistance(route?.distanceKm ?: 0.0),
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
    route: ResultRouteSummary?,
    modifier: Modifier = Modifier
) {
    val stats = listOf(
        "Vahemaa" to formatDistance(route?.distanceKm ?: 0.0),
        "Kestus" to formatDuration(route?.durationSeconds ?: 0),
        "Sammud" to formatSteps(route?.steps ?: 0),
        "Kalorid" to "${formatSteps(route?.calories ?: 0)} kcal",
        "Keskmine kiirus" to formatSpeed(route?.averageSpeed ?: 0.0),
        "Algus/Lõpp aeg" to formatTimeRange(
            route?.startTime ?: 0L,
            route?.endTime ?: 0L
        )
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
private fun SavedInfoCard(
    routeFound: Boolean,
    modifier: Modifier = Modifier
) {
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
                text = if (routeFound) {
                    "Marsruut on salvestatud ajalukku"
                } else {
                    "Marsruudi andmeid laaditakse"
                },
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

private data class ResultRouteSummary(
    val title: String,
    val startTime: Long,
    val endTime: Long,
    val distanceKm: Double,
    val durationSeconds: Int,
    val steps: Int,
    val calories: Int,
    val averageSpeed: Double
)

private suspend fun loadLastSavedRoute(
    db: FirebaseFirestore,
    uid: String
): ResultRouteSummary? {
    val snapshot = db.collection("users")
        .document(uid)
        .collection("routes")
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .limit(1)
        .get()
        .await()

    val document = snapshot.documents.firstOrNull() ?: return null

    return document.toResultRouteSummary()
}

private fun DocumentSnapshot.toResultRouteSummary(): ResultRouteSummary {
    return ResultRouteSummary(
        title = getString("title") ?: "Marsruut",
        startTime = getTimeMillis("startTime")
            ?: getTimeMillis("createdAt")
            ?: System.currentTimeMillis(),
        endTime = getTimeMillis("endTime")
            ?: getTimeMillis("createdAt")
            ?: System.currentTimeMillis(),
        distanceKm = getNumberDouble("distanceKm"),
        durationSeconds = getNumberInt("durationSeconds"),
        steps = getNumberInt("steps"),
        calories = getNumberInt("calories"),
        averageSpeed = getNumberDouble("averageSpeed")
    )
}

private fun DocumentSnapshot.getNumberInt(fieldName: String): Int {
    val value = get(fieldName)

    return when (value) {
        is Number -> value.toInt()
        else -> 0
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

private fun formatFullDate(timeMillis: Long): String {
    return SimpleDateFormat("dd.MM.yyyy • HH:mm", Locale.US).format(Date(timeMillis))
}

private fun formatTimeRange(startTime: Long, endTime: Long): String {
    if (startTime <= 0L || endTime <= 0L) {
        return "—"
    }

    val formatter = SimpleDateFormat("HH:mm", Locale.US)

    return "${formatter.format(Date(startTime))} / ${formatter.format(Date(endTime))}"
}

private fun formatSteps(value: Int): String {
    return String.format(Locale.US, "%,d", value).replace(",", " ")
}

private fun formatDistance(distanceKm: Double): String {
    return "${formatOneDecimal(distanceKm)} km"
}

private fun formatOneDecimal(value: Double): String {
    return String.format(Locale.US, "%.1f", value)
}

private fun formatDuration(durationSeconds: Int): String {
    val safeSeconds = durationSeconds.coerceAtLeast(0)

    return when {
        safeSeconds < 60 -> "$safeSeconds s"
        safeSeconds < 3600 -> "${safeSeconds / 60} min"
        else -> {
            val hours = safeSeconds / 3600
            val minutes = (safeSeconds % 3600) / 60
            "${hours}h ${minutes}min"
        }
    }
}

private fun formatSpeed(speedKmh: Double): String {
    return "${formatOneDecimal(speedKmh)} km/h"
}
