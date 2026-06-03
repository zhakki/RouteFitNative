package com.example.routefitnative.data

import com.example.routefitnative.model.UserProfile
import com.example.routefitnative.model.UserSettings
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun userDocument(uid: String) =
        db.collection("users").document(uid)

    suspend fun createUserProfile(profile: UserProfile) {
        val userRef = userDocument(profile.uid)
        val settingsRef = userRef.collection("settings").document("main")

        val batch = db.batch()

        batch.set(userRef, profile)
        batch.set(settingsRef, UserSettings())

        batch.commit().await()
    }

    suspend fun getUserProfile(uid: String): UserProfile? {
        val snapshot = userDocument(uid).get().await()

        if (!snapshot.exists()) {
            return null
        }

        return snapshot.toUserProfile()
    }

    suspend fun updateUserProfile(profile: UserProfile) {
        userDocument(profile.uid)
            .set(profile.copy(updatedAt = System.currentTimeMillis()))
            .await()
    }

    suspend fun ensureUserProfileExists(uid: String, email: String) {
        val existingProfile = getUserProfile(uid)

        if (existingProfile == null) {
            createUserProfile(
                UserProfile(
                    uid = uid,
                    email = email,
                    fullName = ""
                )
            )
        }
    }

    private fun DocumentSnapshot.toUserProfile(): UserProfile {
        return UserProfile(
            uid = getString("uid") ?: id,
            email = getString("email") ?: "",
            fullName = getString("fullName")
                ?: getString("fullname")
                ?: "",
            age = getLong("age")?.toInt() ?: 0,
            weightKg = getDouble("weightKg") ?: 0.0,
            heightCm = getDouble("heightCm") ?: 0.0,
            gender = getString("gender") ?: "",
            createdAt = getTimeMillis("createdAt"),
            updatedAt = getTimeMillis("updatedAt")
        )
    }

    private fun DocumentSnapshot.getTimeMillis(fieldName: String): Long {
        val value = get(fieldName)

        return when (value) {
            is Timestamp -> value.toDate().time
            is Long -> value
            is Int -> value.toLong()
            is Double -> value.toLong()
            else -> System.currentTimeMillis()
        }
    }
}