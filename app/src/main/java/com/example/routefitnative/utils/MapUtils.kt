package com.example.routefitnative.utils

import com.google.android.gms.maps.model.LatLng
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sqrt

object MapUtils {

    /**
     * Calculates the distance between two points in meters using Haversine formula.
     */
    fun calculateDistance(p1: LatLng, p2: LatLng): Double {
        val p = 0.017453292519943295
        val a = 0.5 -
                cos((p2.latitude - p1.latitude) * p) / 2 +
                cos(p1.latitude * p) * cos(p2.latitude * p) *
                (1 - cos((p2.longitude - p1.longitude) * p)) / 2
        return 12742000 * asin(sqrt(a)) // 2 * R; R = 6371000 meters
    }

    /**
     * Calculates the total distance of a route in meters.
     */
    fun calculateRouteDistance(points: List<LatLng>): Double {
        if (points.size < 2) return 0.0
        var totalDistance = 0.0
        for (i in 0 until points.size - 1) {
            totalDistance += calculateDistance(points[i], points[i + 1])
        }
        return totalDistance
    }
}
