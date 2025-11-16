package com.example.drivesafe.ui.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import com.example.drivesafe.MotionStateHolder

/**
 * Simple debug screen that shows current speed & moving status.
 * Brandon can plug this into whatever navigation he sets up.
 */
@Composable
fun MotionDebugScreen(modifier: Modifier = Modifier) {
    val speed = MotionStateHolder.speedKmh
    val moving = MotionStateHolder.isMoving

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Current speed")
        Text(text = String.format("%.1f km/h", speed))
        Text(text = if (moving) "Status: MOVING" else "Status: STOPPED")
    }
}
