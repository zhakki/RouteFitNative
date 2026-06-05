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

    private var isCounting = false
    private var isPaused = false
    
    private var totalStepsBeforeCurrentSegment = 0
    private var sensorValueAtSegmentStart: Int? = null
    private var lastSensorValue: Int? = null

    fun startCounting() {
        if (isCounting || stepCounterSensor == null) return

        isCounting = true
        isPaused = false
        totalStepsBeforeCurrentSegment = 0
        sensorValueAtSegmentStart = null
        lastSensorValue = null
        _steps.value = 0
        
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
    }

    fun pauseCounting() {
        if (!isCounting || isPaused) return
        
        // Salvestame hetkeni kogutud sammud püsivaks
        lastSensorValue?.let { current ->
            sensorValueAtSegmentStart?.let { start ->
                totalStepsBeforeCurrentSegment += (current - start)
            }
        }
        
        isPaused = true
        sensorValueAtSegmentStart = null // Märgime, et aktiivset segmenti pole
    }

    fun resumeCounting() {
        if (!isCounting || !isPaused) return
        
        isPaused = false
        // Uus segment algab järgmise sensorisündmusega
        sensorValueAtSegmentStart = null 
    }

    fun stopCounting(): Int {
        val finalSteps = _steps.value
        sensorManager.unregisterListener(this)
        isCounting = false
        isPaused = false
        sensorValueAtSegmentStart = null
        lastSensorValue = null
        return finalSteps
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val currentSensorValue = event.values[0].toInt()
            lastSensorValue = currentSensorValue
            
            if (isPaused) return

            // Kui segment on just alanud (start või resume), fikseerime algpunkti
            if (sensorValueAtSegmentStart == null) {
                sensorValueAtSegmentStart = currentSensorValue
            }

            // Arvutame: varem kogutud + selle segmendi sammud
            val stepsInCurrentSegment = currentSensorValue - (sensorValueAtSegmentStart ?: currentSensorValue)
            _steps.value = totalStepsBeforeCurrentSegment + stepsInCurrentSegment
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
