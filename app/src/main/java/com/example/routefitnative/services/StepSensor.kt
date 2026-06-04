package com.example.routefitnative.services

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StepSensor(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps

    private var startSteps: Int? = null
    private var isCounting = false
    private var isPaused = false
    private var stepsAtPause = 0
    private var totalPausedSteps = 0

    fun startCounting() {
        if (isCounting || stepCounterSensor == null) return

        startSteps = null
        isCounting = true
        isPaused = false
        stepsAtPause = 0
        totalPausedSteps = 0
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
    }

    fun pauseCounting() {
        if (!isCounting || isPaused) return
        isPaused = true
        stepsAtPause = _steps.value
    }

    fun resumeCounting() {
        if (!isCounting || !isPaused) return
        isPaused = false
    }

    fun stopCounting(): Int {
        val finalSteps = _steps.value
        sensorManager.unregisterListener(this)
        isCounting = false
        isPaused = false
        startSteps = null
        _steps.value = 0
        return finalSteps
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val currentSensorValue = event.values[0].toInt()
            
            if (startSteps == null) {
                startSteps = currentSensorValue
            }

            if (!isPaused) {
                _steps.value = currentSensorValue - (startSteps ?: currentSensorValue) - totalPausedSteps
            } else {
                val currentStepsTotal = currentSensorValue - (startSteps ?: currentSensorValue) - totalPausedSteps
                totalPausedSteps += (currentStepsTotal - stepsAtPause)
                stepsAtPause = currentStepsTotal
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
