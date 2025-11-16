package com.example.drivesafe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Simple global state that both the service and the UI can see.
 * When the service calls update(), any composable reading these values will recompose.
 */
object MotionStateHolder {

    var speedKmh by mutableStateOf(0f)
        private set

    var isMoving by mutableStateOf(false)
        private set

    fun update(speed: Float, moving: Boolean) {
        speedKmh = speed
        isMoving = moving
    }
}
