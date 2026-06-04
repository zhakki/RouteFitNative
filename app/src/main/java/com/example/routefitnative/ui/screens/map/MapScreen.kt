package com.example.routefitnative.ui.screens.map

import android.graphics.Bitmap
import android.annotation.SuppressLint
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.routefitnative.ui.components.BottomNavItem
import com.example.routefitnative.ui.components.BottomNavigationBar
import com.example.routefitnative.ui.theme.*
import com.example.routefitnative.viewmodel.PermissionState
import com.example.routefitnative.viewmodel.TrackingViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.Duration

private const val DEFAULT_ZOOM = 18f

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    onBottomNavItemClick: (BottomNavItem) -> Unit = {},
    onStopClick: () -> Unit = {},
    trackingViewModel: TrackingViewModel = viewModel()
) {
    val routePoints by trackingViewModel.routePoints.collectAsState()
    val isTracking by trackingViewModel.isTracking.collectAsState()
    val isPaused by trackingViewModel.isPaused.collectAsState()
    val duration by trackingViewModel.duration.collectAsState()
    val totalDistance by trackingViewModel.totalDistance.collectAsState()
    val isSaving by trackingViewModel.isSaving.collectAsState()
    val permissionState by trackingViewModel.permissionState.collectAsState()
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    var isMapLoading by remember { mutableStateOf(true) }

    // Permission Launchers
    val foregroundPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        trackingViewModel.onForegroundPermissionResult(granted)
    }

    val backgroundPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        trackingViewModel.onBackgroundPermissionResult(granted)
    }

    // Handle permission state changes
    LaunchedEffect(permissionState) {
        when (permissionState) {
            PermissionState.NEEDS_FOREGROUND -> {
                foregroundPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            PermissionState.NEEDS_BACKGROUND -> {
                backgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = RouteFitBackground,
        bottomBar = {
            BottomNavigationBar(
                selectedItem = BottomNavItem.Map,
                onItemClick = onBottomNavItemClick
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(modifier = Modifier.fillMaxSize()) {
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

                Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 18.dp)) {
                    MapPreview(
                        routePoints = routePoints,
                        isTracking = isTracking,
                        isPaused = isPaused,
                        onStartClick = { 
                            if (permissionState == PermissionState.GRANTED) {
                                trackingViewModel.startTracking()
                            } else {
                                trackingViewModel.checkPermissions()
                            }
                        },
                        onPauseClick = { 
                            if (isPaused) trackingViewModel.resumeTracking() else trackingViewModel.pauseTracking()
                        },
                        onStopRequested = { map ->
                            scope.launch {
                                // 1. Visual zoom out
                                try {
                                    if (routePoints.size >= 2 && map != null) {
                                        val builder = LatLngBounds.builder()
                                        routePoints.forEach { builder.include(it) }
                                        map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100))
                                        delay(1000) // Wait for animation to finish and tiles to load

                                        // 1.5 Capture Snapshot
                                        kotlinx.coroutines.suspendCancellableCoroutine<Bitmap?> { continuation ->
                                            map.snapshot { bitmap ->
                                                continuation.resume(bitmap) {}
                                            }
                                        }?.let { snapshot ->
                                            trackingViewModel.setSnapshot(snapshot)
                                        }
                                    }
                                } catch (e: Exception) {}

                                // 2. Definitive stop and save (Firebase stubbed for now)
                                val success = trackingViewModel.finishAndSaveRoute()
                                
                                // 3. RE-ENABLED: Navigate on success
                                if (success) {
                                    onStopClick()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        fusedLocationClient = fusedLocationClient,
                        permissionState = permissionState,
                        onLocationLoaded = { isMapLoading = false }
                    )
                    
                    if (isMapLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(RouteFitBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = RouteFitAccent)
                        }
                    }
                }
            }

            if (isSaving) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = RouteFitAccent)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Salvestamine...",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Background Permission Rationale Dialog
            if (permissionState == PermissionState.NEEDS_BACKGROUND_RATIONALE) {
                AlertDialog(
                    onDismissRequest = { trackingViewModel.dismissRationale() },
                    title = { Text("Tausta asukoha kasutus") },
                    text = {
                        Column {
                            Text(
                                "RouteFit vajab asukoha luba ka taustal, et sinu teekonda täpselt jälgida, kui telefon on taskus või ekraan kinni.",
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Text(
                                "Kuidas lubada:",
                                fontWeight = FontWeight.Bold
                            )
                            Text("1. Vali avanevas aknas 'Seaded'")
                            Text("2. Vali Load -> Asukoht")
                            Text("3. Vali 'Luba alati'")
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { trackingViewModel.dismissRationale() },
                            colors = ButtonDefaults.buttonColors(containerColor = RouteFitAccent)
                        ) {
                            Text("Sain aru")
                        }
                    },
                    containerColor = RouteFitSurface,
                    titleContentColor = RouteFitTextPrimary,
                    textContentColor = RouteFitTextSecondary
                )
            }
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
    routePoints: List<LatLng>,
    isTracking: Boolean,
    isPaused: Boolean,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onStopRequested: (com.google.android.gms.maps.GoogleMap?) -> Unit,
    modifier: Modifier = Modifier,
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    permissionState: PermissionState,
    onLocationLoaded: () -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(59.437, 24.753), DEFAULT_ZOOM)
    }

    var googleMap by remember { mutableStateOf<com.google.android.gms.maps.GoogleMap?>(null) }
    var isFollowModeEnabled by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // Detect manual movement to disable follow mode
    LaunchedEffect(cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving && cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
            isFollowModeEnabled = false
        }
    }

    @SuppressLint("MissingPermission")
    LaunchedEffect(Unit) {
        try {
            if (permissionState == PermissionState.GRANTED) {
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).await()
                
                if (location != null) {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                        LatLng(location.latitude, location.longitude), 
                        DEFAULT_ZOOM
                    )
                }
            }
        } catch (e: SecurityException) {
        } finally {
            onLocationLoaded()
        }
    }

    LaunchedEffect(routePoints, isFollowModeEnabled) {
        if (isFollowModeEnabled && routePoints.isNotEmpty() && !isPaused && isTracking) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(routePoints.last(), DEFAULT_ZOOM)
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = permissionState == PermissionState.GRANTED
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false,
                mapToolbarEnabled = false
            )
        ) {
            MapEffect(Unit) { map ->
                googleMap = map
            }
            
            if (routePoints.isNotEmpty()) {
                Polyline(
                    points = routePoints,
                    color = RouteFitAccent,
                    width = 12f,
                    jointType = com.google.android.gms.maps.model.JointType.ROUND
                )
            }
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            RouteFitBackground.copy(alpha = 0.3f),
                            Color.Transparent,
                            RouteFitBackground.copy(alpha = 0.5f)
                        )
                    )
                )
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 24.dp, end = 24.dp)
                .size(68.dp)
                .clip(CircleShape)
                .clickable {
                    isFollowModeEnabled = true
                    scope.launch {
                        // Try to center on the last route point first, if available
                        val targetLatLng = if (routePoints.isNotEmpty()) {
                            routePoints.last()
                        } else {
                            // Fallback to current GPS location
                            try {
                                if (permissionState == PermissionState.GRANTED) {
                                    @SuppressLint("MissingPermission")
                                    val loc = fusedLocationClient.getCurrentLocation(
                                        Priority.PRIORITY_HIGH_ACCURACY,
                                        null
                                    ).await()
                                    loc?.let { LatLng(it.latitude, it.longitude) }
                                } else null
                            } catch (e: Exception) { null }
                        }

                        targetLatLng?.let {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(it, DEFAULT_ZOOM)
                            )
                        }
                    }
                },
            shape = CircleShape,
            color = RouteFitSurface.copy(alpha = 0.8f),
            border = BorderStroke(
                2.dp,
                if (isFollowModeEnabled) RouteFitAccent else RouteFitAccent.copy(alpha = 0.4f)
            )
        ) {
            Box(contentAlignment = Alignment.Center) {
                MyLocationIcon(
                    modifier = Modifier.size(34.dp),
                    color = if (isFollowModeEnabled) RouteFitAccent else RouteFitTextSecondary
                )
            }
        }

        ControlPanel(
            isTracking = isTracking,
            isPaused = isPaused,
            onStartClick = onStartClick,
            onPauseClick = onPauseClick,
            onStopClick = {
                isFollowModeEnabled = false
                onStopRequested(googleMap)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 22.dp, vertical = 24.dp)
        )
    }
}

@Composable
private fun ControlPanel(
    isTracking: Boolean,
    isPaused: Boolean,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onStopClick: () -> Unit,
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
                    onClick = onStartClick,
                    modifier = Modifier.padding(horizontal = 18.dp)
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
                    onClick = onPauseClick,
                    modifier = Modifier.padding(horizontal = 18.dp)
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
                    onClick = onStopClick
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
            modifier = Modifier
                .size(76.dp)
                .clickable(onClick = onClick),
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

private fun formatDuration(duration: java.time.Duration): String {
    val seconds = duration.seconds % 60
    val minutes = (duration.seconds / 60) % 60
    val hours = duration.seconds / 3600
    return if (hours > 0) {
        "%02d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}

private fun formatPace(distanceMeters: Double, duration: java.time.Duration): String {
    if (distanceMeters <= 0 || duration.seconds <= 0) return "0'00"
    val distanceKm = distanceMeters / 1000.0
    val totalSecondsPerKm = (duration.seconds / distanceKm).toInt()
    val minutes = totalSecondsPerKm / 60
    val seconds = totalSecondsPerKm % 60
    return "%d'%02d".format(minutes, seconds)
}
