package com.example.routefitnative.data

import com.example.routefitnative.model.RouteModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class RouteRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun routesCollection(uid: String) =
        db.collection("users")
            .document(uid)
            .collection("routes")

    suspend fun saveRoute(uid: String, route: RouteModel): String {
        val routeRef = routesCollection(uid).document()

        val routeToSave = route.copy(
            routeId = routeRef.id,
            userId = uid,
            createdAt = System.currentTimeMillis()
        )

        routeRef.set(routeToSave).await()

        return routeRef.id
    }

    suspend fun getUserRoutes(uid: String): List<RouteModel> {
        val snapshot = routesCollection(uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()

        return snapshot.documents.mapNotNull { document ->
            document.toObject(RouteModel::class.java)
        }
    }

    suspend fun updateRouteTitle(uid: String, routeId: String, newTitle: String) {
        routesCollection(uid)
            .document(routeId)
            .update("title", newTitle)
            .await()
    }

    suspend fun saveTestRoute(uid: String): String {
        val testRoute = RouteModel(
            title = "Test route",
            distanceKm = 2.5,
            durationSeconds = 1200,
            steps = 3200,
            calories = 180,
            averageSpeed = 7.5,
            activityType = "walking"
        )

        return saveRoute(uid, testRoute)
    }
}