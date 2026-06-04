package com.example.routefitnative.viewmodel

import android.Manifest
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.routefitnative.data.RouteRepository
import com.example.routefitnative.data.UserRepository
import com.example.routefitnative.model.RouteModel
import com.example.routefitnative.services.TrackingService
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class PermissionState {
    IDLE,
    NEEDS_FOREGROUND,
    NEEDS_BACKGROUND_RATIONALE,
    NEEDS_BACKGROUND,
    GRANTED,
    DENIED
}

class TrackingViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository()
    private val auth = FirebaseAuth.getInstance()

    private var trackingService: TrackingService? = null
    private var isBound = false

    private val _routePoints = MutableStateFlow<List<LatLng>>(emptyList())
    val routePoints: StateFlow<List<LatLng>> = _routePoints.asStateFlow()

    private val _currentLocation = MutableStateFlow<android.location.Location?>(null)
    val currentLocation: StateFlow<android.location.Location?> = _currentLocation.asStateFlow()

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

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _lastSavedRoute = MutableStateFlow<RouteModel?>(null)
    val lastSavedRoute: StateFlow<RouteModel?> = _lastSavedRoute.asStateFlow()

    private val _routeSnapshot = MutableStateFlow<Bitmap?>(null)
    val routeSnapshot: StateFlow<Bitmap?> = _routeSnapshot.asStateFlow()

    private val _permissionState = MutableStateFlow(PermissionState.IDLE)
    val permissionState: StateFlow<PermissionState> = _permissionState.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TrackingService.TrackingBinder
            trackingService = binder.getService()
            isBound = true

            viewModelScope.launch {
                trackingService?.routePoints?.collect { _routePoints.value = it }
            }
            viewModelScope.launch {
                trackingService?.currentLocation?.collect { _currentLocation.value = it }
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
        checkPermissions()
    }

    fun checkPermissions() {
        val context = getApplication<Application>()
        val hasForeground = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasBackground = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        _permissionState.value = when {
            hasForeground && hasBackground -> PermissionState.GRANTED
            hasForeground && !hasBackground -> PermissionState.NEEDS_BACKGROUND_RATIONALE
            else -> PermissionState.NEEDS_FOREGROUND
        }
    }

    fun onForegroundPermissionResult(granted: Boolean) {
        if (granted) {
            checkPermissions()
        } else {
            _permissionState.value = PermissionState.DENIED
        }
    }

    fun onBackgroundPermissionResult(granted: Boolean) {
        if (granted) {
            _permissionState.value = PermissionState.GRANTED
        } else {
            // Background permission is optional but recommended
            // We can still track in foreground, but we warn the user
            _permissionState.value = PermissionState.GRANTED 
        }
    }

    fun dismissRationale() {
        _permissionState.value = PermissionState.NEEDS_BACKGROUND
    }

    fun setSnapshot(bitmap: Bitmap?) {
        _routeSnapshot.value = bitmap
    }

    fun clearSnapshot() {
        _routeSnapshot.value = null
    }

    private fun bindTrackingService() {
        val intent = Intent(getApplication(), TrackingService::class.java)
        getApplication<Application>().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun startTracking() {
        val intent = Intent(getApplication(), TrackingService::class.java).apply {
            action = "START"
        }
        ContextCompat.startForegroundService(getApplication(), intent)
        viewModelScope.launch {
            trackingService?.startTracking()
        }
    }

    fun pauseTracking() {
        viewModelScope.launch {
            trackingService?.pauseTracking()
        }
    }

    fun resumeTracking() {
        viewModelScope.launch {
            trackingService?.resumeTracking()
        }
    }

    fun stopTracking() {
        // Command service to stop and clear UI state immediately
        val intent = Intent(getApplication(), TrackingService::class.java).apply {
            action = "STOP"
        }
        getApplication<Application>().startService(intent)
        viewModelScope.launch {
            trackingService?.stopTracking()
        }

        _isTracking.value = false
        _isPaused.value = false
        _routePoints.value = emptyList()
        _totalDistance.value = 0.0
        _steps.value = 0
        _duration.value = java.time.Duration.ZERO
    }

    /**
     * Prepares the data and hand over the saving process to the database developer.
     */
    suspend fun finishAndSaveRoute(): Boolean {
        if (_isSaving.value) return false
        
        // 1. CAPTURE DATA: Take immediate snapshot of final values
        val finalPoints = _routePoints.value
        val finalDistance = _totalDistance.value
        val finalDuration = _duration.value
        val finalSteps = _steps.value
        
        // 2. STOP ENGINE: Kill the background service and notification NOW
        stopTracking()

        if (finalPoints.isEmpty()) return true

        return try {
            _isSaving.value = true

            // 3. HANDOFF: Prepare the model for the database developer
            val userId = auth.currentUser?.uid ?: "unknown"
            val routeId = java.util.UUID.randomUUID().toString()
            
            // Note: In real case, we might want to fetch user weight from Firestore
            // but to keep it non-blocking, we use a default or cached value
            val weightKg = 75.0 
            val calories = (weightKg * (finalDistance / 1000.0) * 0.9).toInt()
            val avgSpeed = if (finalDuration.seconds > 0) (finalDistance / 1000.0) / (finalDuration.seconds / 3600.0) else 0.0

            val routeToSave = RouteModel(
                routeId = routeId,
                userId = userId,
                title = "Uus treening",
                startTime = System.currentTimeMillis() - finalDuration.toMillis(),
                endTime = System.currentTimeMillis(),
                distanceKm = finalDistance / 1000.0,
                durationSeconds = finalDuration.seconds.toInt(),
                steps = finalSteps,
                calories = calories,
                averageSpeed = avgSpeed,
                activityType = "walking"
            )

            // Store for the results screen
            _lastSavedRoute.value = routeToSave

            // ==========================================================
            // TODO: DATABASE DEVELOPER (KOLMAS ISIK)
            // Kasuta: routeToSave ja finalPoints
            // ==========================================================
            delay(1200) // Simulated work
            // ==========================================================

            _isSaving.value = false
            true 
        } catch (e: Exception) {
            _isSaving.value = false
            true // Robustness: let user proceed to results even on save error during testing
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (isBound) {
            getApplication<Application>().unbindService(serviceConnection)
            isBound = false
        }
    }
}
