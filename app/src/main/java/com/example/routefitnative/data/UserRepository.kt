package com.example.routefitnative.data

import com.example.routefitnative.model.UserProfile
import com.example.routefitnative.model.UserSettings
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
        return snapshot.toObject(UserProfile::class.java)
    }

    suspend fun updateUserProfile(profile: UserProfile) {
        userDocument(profile.uid)
            .set(profile.copy(updatedAt = System.currentTimeMillis()))
            .await()
    }
}