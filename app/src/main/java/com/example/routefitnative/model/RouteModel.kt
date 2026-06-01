package com.example.routefitnative.model

data class RouteModel(
    val routeId: String = "",
    val userId: String = "",
    val title: String = "",
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long = System.currentTimeMillis(),
    val distanceKm: Double = 0.0,
    val durationSeconds: Int = 0,
    val steps: Int = 0,
    val calories: Int = 0,
    val averageSpeed: Double = 0.0,
    val activityType: String = "walking",
    val createdAt: Long = System.currentTimeMillis()
)