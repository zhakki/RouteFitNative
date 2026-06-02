package com.example.routefitnative.ui.screens.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.routefitnative.ui.components.BottomNavItem
import com.example.routefitnative.ui.components.BottomNavigationBar
import com.example.routefitnative.ui.theme.*
import com.example.routefitnative.viewmodel.TrackingViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.gms.maps.CameraUpdateFactory
import java.time.Duration

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    trackingViewModel: TrackingViewModel = viewModel()
) {
    val routePoints by trackingViewModel.routePoints.collectAsState()
    val isTracking by trackingViewModel.isTracking.collectAsState()
    val isPaused by trackingViewModel.isPaused.collectAsState()
    val duration by trackingViewModel.duration.collectAsState()
    val totalDistance by trackingViewModel.totalDistance.collectAsState()

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
                    value = formatPace(totalDistance, duration),
                    unit = "/km",
                    modifier = Modifier.weight(1f)
                )
                MapStatCard(
                    label = "Vahemaa",
                    value = if (totalDistance < 1000) "%.0f".format(totalDistance) else "%.2f".format(totalDistance / 1000),
                    unit = if (totalDistance < 1000) "m" else "km",
                    modifier = Modifier.weight(1f)
                )
                MapStatCard(
                    label = "Aeg",
                    value = formatDuration(duration),
                    unit = "",
                    modifier = Modifier.weight(1f)
                )
            }

            MapPreview(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 18.dp),
                routePoints = routePoints,
                isTracking = isTracking,
                isPaused = isPaused,
                onStart = { trackingViewModel.startTracking() },
                onPause = { trackingViewModel.pauseTracking() },
                onResume = { trackingViewModel.resumeTracking() },
                onStop = { trackingViewModel.stopTracking() }
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
                        fontSize = if (value.length > 3) 30.sp else 36.sp,
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
private fun MapPreview(
    modifier: Modifier = Modifier,
    routePoints: List<LatLng>,
    isTracking: Boolean,
    isPaused: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit
) {
    val initialPos = LatLng(59.437, 24.753)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPos, 15f)
    }

    // Follow logic
    LaunchedEffect(routePoints) {
        if (routePoints.isNotEmpty()) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLng(routePoints.last())
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(RouteFitBackground)
    ) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false,
                mapToolbarEnabled = false
            )
        ) {
            if (routePoints.isNotEmpty()) {
                Polyline(
                    points = routePoints,
                    color = RouteFitAccent,
                    width = 12f
                )
            }
        }

        // Overlay to maintain the stylized look
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            RouteFitBackground.copy(alpha = 0.4f),
                            Color.Transparent,
                            RouteFitBackground.copy(alpha = 0.6f)
                        )
                    )
                )
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 24.dp, end = 24.dp)
                .size(68.dp),
            shape = CircleShape,
            color = RouteFitSurface.copy(alpha = 0.8f),
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
                .padding(horizontal = 22.dp, vertical = 24.dp),
            isTracking = isTracking,
            isPaused = isPaused,
            onStart = onStart,
            onPause = onPause,
            onResume = onResume,
            onStop = onStop
        )
    }
}

@Composable
private fun ControlPanel(
    modifier: Modifier = Modifier,
    isTracking: Boolean,
    isPaused: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit
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

            if (!isTracking) {
                CircleActionButton(
                    label = "START",
                    containerColor = RouteFitSurfaceVariant,
                    borderColor = RouteFitAccent,
                    contentColor = RouteFitAccent,
                    modifier = Modifier.padding(horizontal = 18.dp),
                    onClick = onStart
                ) {
                    PlayIcon(
                        modifier = Modifier.size(28.dp),
                        color = RouteFitAccent
                    )
                }
            } else {
                CircleActionButton(
                    label = if (isPaused) "RESUME" else "PAUSE",
                    containerColor = RouteFitSurfaceVariant,
                    borderColor = if (isPaused) RouteFitAccent else RouteFitTextSecondary,
                    contentColor = if (isPaused) RouteFitAccent else RouteFitTextPrimary,
                    modifier = Modifier.padding(horizontal = 18.dp),
                    onClick = if (isPaused) onResume else onPause
                ) {
                    if (isPaused) {
                        PlayIcon(modifier = Modifier.size(28.dp), color = RouteFitAccent)
                    } else {
                        PauseIcon(modifier = Modifier.size(28.dp), color = RouteFitTextPrimary)
                    }
                }

                CircleActionButton(
                    label = "STOP",
                    containerColor = Color(0xFFB98282),
                    borderColor = Color.Transparent,
                    contentColor = Color(0xFF4B1515),
                    onClick = onStop
                ) {
                    StopIcon(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF4B1515)
                    )
                }
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
    onClick: () -> Unit = {},
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
            border = BorderStroke(4.dp, borderColor),
            onClick = onClick
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
private fun PauseIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val width = 6.dp.toPx()
        val spacing = 8.dp.toPx()
        drawRect(
            color = color,
            topLeft = androidx.compose.ui.geometry.Offset(center.x - spacing / 2 - width, size.height * 0.2f),
            size = androidx.compose.ui.geometry.Size(width, size.height * 0.6f)
        )
        drawRect(
            color = color,
            topLeft = androidx.compose.ui.geometry.Offset(center.x + spacing / 2, size.height * 0.2f),
            size = androidx.compose.ui.geometry.Size(width, size.height * 0.6f)
        )
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

private fun formatDuration(duration: Duration): String {
    val seconds = duration.seconds % 60
    val minutes = (duration.seconds / 60) % 60
    val hours = duration.seconds / 3600
    return if (hours > 0) {
        "%02d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}

private fun formatPace(distanceMeters: Double, duration: Duration): String {
    if (distanceMeters <= 0 || duration.seconds <= 0) return "0'00"
    val distanceKm = distanceMeters / 1000.0
    val totalSecondsPerKm = (duration.seconds / distanceKm).toInt()
    val minutes = totalSecondsPerKm / 60
    val seconds = totalSecondsPerKm % 60
    return "%d'%02d".format(minutes, seconds)
}
