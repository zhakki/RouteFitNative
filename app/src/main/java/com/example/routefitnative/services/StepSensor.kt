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

    fun startCounting() {
        if (isCounting || stepCounterSensor == null) return

        startSteps = null
        isCounting = true
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
    }

    fun stopCounting(): Int {
        val finalSteps = _steps.value
        sensorManager.unregisterListener(this)
        isCounting = false
        startSteps = null
        _steps.value = 0
        return finalSteps
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0].toInt()
            if (startSteps == null) {
                startSteps = totalSteps
            }
            _steps.value = totalSteps - (startSteps ?: totalSteps)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
