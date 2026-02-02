package com.example.drivesafe.ui.debug

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.drivesafe.TapStateHolder
import com.example.drivesafe.MotionStateHolder

@Composable
fun TapDebugScreen(modifier: Modifier = Modifier) {

    // read from TapStateHolder
    val risk = TapStateHolder.riskScore
    val typing = TapStateHolder.riskTyping
    val tapping = TapStateHolder.riskTapping
    val scrolling = TapStateHolder.riskScrolling
    val tapViolations = TapStateHolder.tapViolations
    val scrollViolations = TapStateHolder.scrollViolations
    val typeViolations = TapStateHolder.typeViolations
    val level = TapStateHolder.riskLevel
    val events = TapStateHolder.events

    // also show motion info to see if it’s moving later when you hook that in
    val speed = MotionStateHolder.speedKmh
    val moving = MotionStateHolder.isMoving

    var text by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Tap / Scroll / Typing Debug",
            style = MaterialTheme.typography.titleLarge
        )

        // Motion info
        Card {
            Column(Modifier.padding(12.dp)) {
                Text("Speed: ${"%.1f".format(speed)} km/h")
                Text("Moving: ${if (moving) "YES" else "NO"}")
            }
        }

        // Risk summary
        Card {
            Column(
                Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Risk Score: $risk", style = MaterialTheme.typography.titleMedium)
                Text("Risk Level: $level")
                Text("Typing score: $typing (violations=$typeViolations)")
                Text("Tapping score: $tapping (violations=$tapViolations)")
                Text("Scrolling score: $scrolling (violations=$scrollViolations)")
            }
        }

        // Test area
        Card {
            Column(
                Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
                    .height(220.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Test area (inside app)")

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { /* generates click event */ }) {
                        Text("Tap me")
                    }
                    Button(onClick = { /* spam taps quickly */ }) {
                        Text("Tap fast")
                    }
                }

                // Scrollable area
                Column(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    repeat(30) {
                        Text("Scrollable item #$it", Modifier.padding(4.dp))
                    }
                }

                // Typing test
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Type here") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Text("Recent events:")
        Card(
            modifier = Modifier.weight(1f)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                items(events) { line ->
                    Text(line)
                }
            }
        }
    }
}
