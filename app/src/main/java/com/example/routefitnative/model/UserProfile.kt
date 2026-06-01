package com.example.routefitnative.model

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val fullName: String = "",
    val age: Int = 0,
    val weightKg: Double = 0.0,
    val heightCm: Double = 0.0,
    val gender: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)