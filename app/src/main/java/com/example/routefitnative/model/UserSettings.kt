package com.example.routefitnative.model

data class UserSettings(
    val distanceUnit: String = "km",
    val saveRoutes: Boolean = true,
    val allowLocation: Boolean = true,
    val dailyStepGoal: Int = 8000,
    val updatedAt: Long = System.currentTimeMillis()
)