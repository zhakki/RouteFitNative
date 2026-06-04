package com.example.routefitnative.data

import com.example.routefitnative.model.RouteModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class RouteRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun routesCollection(uid: String) =
        db.collection("users")
            .document(uid)
            .collection("routes")

    private fun dailySummaryDocument(uid: String, date: String) =
        db.collection("users")
            .document(uid)
            .collection("daily_summaries")
            .document(date)

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

    suspend fun saveRouteAndUpdateDailySummary(uid: String, route: RouteModel): String {
        val routeId = saveRoute(uid, route)
        updateDailySummary(uid, route)
        return routeId
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

    private suspend fun updateDailySummary(uid: String, route: RouteModel) {
        val date = routeDateString(route.endTime)

        val updates = mapOf(
            "date" to date,
            "totalSteps" to FieldValue.increment(route.steps.toLong()),
            "calories" to FieldValue.increment(route.calories.toLong()),
            "distanceKm" to FieldValue.increment(route.distanceKm),
            "durationSeconds" to FieldValue.increment(route.durationSeconds.toLong()),
            "updatedAt" to System.currentTimeMillis()
        )

        dailySummaryDocument(uid, date)
            .set(updates, SetOptions.merge())
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

        return saveRouteAndUpdateDailySummary(uid, testRoute)
    }

    private fun routeDateString(timeMillis: Long): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(timeMillis)
    }
}