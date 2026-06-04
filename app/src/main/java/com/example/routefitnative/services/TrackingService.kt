package com.example.routefitnative.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.example.routefitnative.utils.MapUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TrackingService : Service() {

    private val binder = TrackingBinder()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var stepSensor: StepSensor
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _routePoints = MutableStateFlow<List<LatLng>>(emptyList())
    val routePoints: StateFlow<List<LatLng>> = _routePoints

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps

    private val _totalDistance = MutableStateFlow(0.0)
    val totalDistance: StateFlow<Double> = _totalDistance

    private val _duration = MutableStateFlow(java.time.Duration.ZERO)
    val duration: StateFlow<java.time.Duration> = _duration

    private var timerJob: Job? = null

    companion object {
        const val CHANNEL_ID = "tracking_channel"
        const val NOTIFICATION_ID = 1
    }

    inner class TrackingBinder : Binder() {
        fun getService(): TrackingService = this@TrackingService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                "START" -> startTracking()
                "STOP" -> stopTracking()
            }
        }
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        stepSensor = StepSensor(this)
        createNotificationChannel()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (_isTracking.value && !_isPaused.value) {
                    locationResult.lastLocation?.let { location ->
                        addPoint(location)
                    }
                }
            }
        }
    }

    private fun addPoint(location: Location) {
        val newPoint = LatLng(location.latitude, location.longitude)
        val currentList = _routePoints.value.toMutableList()
        
        if (currentList.isNotEmpty()) {
            _totalDistance.value += MapUtils.calculateDistance(currentList.last(), newPoint)
        }

        currentList.add(newPoint)
        _routePoints.value = currentList
    }

    fun startTracking() {
        if (_isTracking.value) return

        _routePoints.value = emptyList()
        _totalDistance.value = 0.0
        _steps.value = 0
        _duration.value = java.time.Duration.ZERO
        _isTracking.value = true
        _isPaused.value = false

        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (isActive) {
                delay(1000)
                if (!_isPaused.value) {
                    _duration.value = _duration.value.plusSeconds(1)
                }
            }
        }

        serviceScope.launch {
            stepSensor.steps.collect {
                _steps.value = it
            }
        }
        stepSensor.startCounting()

        startForeground(NOTIFICATION_ID, createNotification("RouteFit is tracking..."))
        requestLocationUpdates()
    }

    fun pauseTracking() {
        if (!_isTracking.value || _isPaused.value) return
        _isPaused.value = true
        stepSensor.pauseCounting()
        updateNotification("Tracking paused")
    }

    fun resumeTracking() {
        if (!_isTracking.value || !_isPaused.value) return
        _isPaused.value = false
        stepSensor.resumeCounting()
        updateNotification("RouteFit is tracking...")
    }

    fun stopTracking() {
        _isTracking.value = false
        _isPaused.value = false
        _routePoints.value = emptyList()
        _totalDistance.value = 0.0
        _duration.value = java.time.Duration.ZERO
        
        timerJob?.cancel()
        timerJob = null
        stepSensor.stopCounting()
        
        fusedLocationClient.removeLocationUpdates(locationCallback)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L)
            .setMinUpdateDistanceMeters(2f)
            .build()

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (unlikely: SecurityException) {
            _isTracking.value = false
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Route Tracking",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(contentText: String) = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("RouteFit")
        .setContentText(contentText)
        .setSmallIcon(android.R.drawable.ic_menu_mylocation)
        .setOngoing(true)
        .build()

    private fun updateNotification(contentText: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, createNotification(contentText))
    }
}
