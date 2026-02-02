package com.example.drivesafe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Simple global state that both the service and the UI can see.
 * When the service calls update(), any composable reading these values will recompose.
 */
// Replace your MotionStateHolder with this updated version
object MotionStateHolder {
    var speedKmh by mutableStateOf(0f)
        private set

    var isMoving by mutableStateOf(false)
        private set

    var averageSpeed by mutableStateOf(0f)
        private set

    fun update(speed: Float, moving: Boolean) {
        speedKmh = speed
        isMoving = moving

        // Update average speed calculation when moving and speed is reasonable
        if (moving && speed > 2f) {
            AverageSpeedCalculator.addSpeedReading(speed)
            averageSpeed = AverageSpeedCalculator.getAverageSpeed()
        }
    }

    fun resetAverageSpeed() {
        AverageSpeedCalculator.reset()
        averageSpeed = 0f
    }
}

object AverageSpeedCalculator {
    private val speedReadings = mutableListOf<Float>()
    private const val MAX_READINGS = 100 // Keep last 100 readings

    fun addSpeedReading(speed: Float) {
        // Only add reasonable speed readings
        if (speed in 2f..200f) {
            speedReadings.add(speed)

            // Keep only the most recent readings
            if (speedReadings.size > MAX_READINGS) {
                speedReadings.removeAt(0)
            }
        }
    }

    fun getAverageSpeed(): Float {
        if (speedReadings.isEmpty()) return 0f
        return speedReadings.average().toFloat()
    }

    fun reset() {
        speedReadings.clear()
    }

    fun getReadingCount(): Int = speedReadings.size
}