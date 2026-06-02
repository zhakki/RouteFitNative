package com.example.routefitnative.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.routefitnative.viewmodel.TrackingViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen(
    viewModel: TrackingViewModel = viewModel()
) {
    val routePoints by viewModel.routePoints.collectAsState()
    val isTracking by viewModel.isTracking.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val steps by viewModel.steps.collectAsState()
    val totalDistance by viewModel.totalDistance.collectAsState()

    val initialPos = LatLng(59.437, 24.753) // Tallinn
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPos, 15f)
    }

    // Auto-center/Follow logic
    LaunchedEffect(routePoints) {
        if (routePoints.isNotEmpty()) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLng(routePoints.last()),
                durationMs = 500
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(myLocationButtonEnabled = true)
        ) {
            if (routePoints.isNotEmpty()) {
                Polyline(
                    points = routePoints,
                    color = Color.Blue,
                    width = 10f
                )
            }
        }

        // Simple Stats Overlay for testing
        Card(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "Sammud: $steps", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Text(text = "Vahemaa: ${"%.0f".format(totalDistance)} m", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            }
        }

        // Controls
        Row(
            modifier = Modifier
                .padding(bottom = 48.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isTracking) {
                FloatingActionButton(
                    onClick = { viewModel.startTracking() },
                    containerColor = Color.Green,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Start")
                }
            } else {
                FloatingActionButton(
                    onClick = {
                        if (isPaused) viewModel.resumeTracking() else viewModel.pauseTracking()
                    },
                    containerColor = if (isPaused) Color.Yellow else Color.Gray,
                    contentColor = Color.Black
                ) {
                    Icon(
                        if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = "Pause/Resume"
                    )
                }
                
                FloatingActionButton(
                    onClick = { viewModel.stopTracking() },
                    containerColor = Color.Red,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Stop, contentDescription = "Stop")
                }
            }
        }
    }
}
