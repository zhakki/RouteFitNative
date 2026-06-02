package com.example.routefitnative.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.routefitnative.services.TrackingService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrackingViewModel(application: Application) : AndroidViewModel(application) {

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

    override fun onCleared() {
        super.onCleared()
        if (isBound) {
            getApplication<Application>().unbindService(serviceConnection)
            isBound = false
        }
    }
}
