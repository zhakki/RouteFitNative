package com.example.routefitnative.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.routefitnative.data.RouteRepository
import com.example.routefitnative.data.UserRepository
import com.example.routefitnative.model.RouteModel
import com.example.routefitnative.services.TrackingService
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrackingViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository()
    private val routeRepository = RouteRepository()
    private val auth = FirebaseAuth.getInstance()

    private var trackingService: TrackingService? = null
    private var isBound = false

    private val _routePoints = MutableStateFlow<List<LatLng>>(emptyList())
    val routePoints: StateFlow<List<LatLng>> = _routePoints.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps.asStateFlow()

    private val _totalDistance = MutableStateFlow(0.0)
    val totalDistance: StateFlow<Double> = _totalDistance.asStateFlow()

    private val _duration = MutableStateFlow(java.time.Duration.ZERO)
    val duration: StateFlow<java.time.Duration> = _duration.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TrackingService.TrackingBinder
            trackingService = binder.getService()
            isBound = true

            // Observe data from service
            viewModelScope.launch {
                trackingService?.routePoints?.collect { _routePoints.value = it }
            }
            viewModelScope.launch {
                trackingService?.isTracking?.collect { _isTracking.value = it }
            }
            viewModelScope.launch {
                trackingService?.isPaused?.collect { _isPaused.value = it }
            }
            viewModelScope.launch {
                trackingService?.steps?.collect { _steps.value = it }
            }
            viewModelScope.launch {
                trackingService?.totalDistance?.collect { _totalDistance.value = it }
            }
            viewModelScope.launch {
                trackingService?.duration?.collect { _duration.value = it }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            trackingService = null
            isBound = false
        }
    }

    init {
        bindTrackingService()
    }

    private fun bindTrackingService() {
        val intent = Intent(getApplication(), TrackingService::class.java)
        getApplication<Application>().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun startTracking() {
        val intent = Intent(getApplication(), TrackingService::class.java).apply {
            action = "START"
        }
        getApplication<Application>().startForegroundService(intent)
        trackingService?.startTracking()
    }

    fun pauseTracking() {
        trackingService?.pauseTracking()
    }

    fun resumeTracking() {
        trackingService?.resumeTracking()
    }

    fun stopTracking() {
        trackingService?.stopTracking()
    }

    suspend fun saveCurrentRoute() {
        val userId = auth.currentUser?.uid ?: return

        val points = _routePoints.value
        val distanceMeters = _totalDistance.value
        val duration = _duration.value
        val finalSteps = _steps.value

        if (points.isEmpty()) return

        val profile = userRepository.getUserProfile(userId)
        val weight = profile?.weightKg ?: 70.0
        val calories = (weight * (distanceMeters / 1000.0) * 0.9).toInt()

        val routeModel = RouteModel(
            userId = userId,
            title = "Uus marsruut",
            startTime = System.currentTimeMillis() - duration.toMillis(),
            endTime = System.currentTimeMillis(),
            distanceKm = distanceMeters / 1000.0,
            durationSeconds = duration.seconds.toInt(),
            steps = finalSteps,
            calories = calories,
            averageSpeed = if (duration.seconds > 0) (distanceMeters / 1000.0) / (duration.seconds / 3600.0) else 0.0,
            activityType = "walking"
        )

        routeRepository.saveRoute(userId, routeModel)

        // After saving, we can finally stop everything
        stopTracking()
    }

    override fun onCleared() {
        super.onCleared()
        if (isBound) {
            getApplication<Application>().unbindService(serviceConnection)
            isBound = false
        }
    }
}
