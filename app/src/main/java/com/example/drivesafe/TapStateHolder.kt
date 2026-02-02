package com.example.drivesafe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.mutableStateListOf
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Global tap/typing/scroll state for debug UI.
 * TapDetection (AccessibilityService) writes to this,
 * composables read from it.
 */
object TapStateHolder {

    var riskScore by mutableStateOf(0)
        private set

    var riskTyping by mutableStateOf(0)
        private set

    var riskTapping by mutableStateOf(0)
        private set

    var riskScrolling by mutableStateOf(0)
        private set

    var tapViolations by mutableStateOf(0)
        private set

    var scrollViolations by mutableStateOf(0)
        private set

    var typeViolations by mutableStateOf(0)
        private set

    var riskLevel by mutableStateOf("SAFE")
        private set

    // Simple log of recent events (for the debug screen)
    val events: SnapshotStateList<String> = mutableStateListOf()

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    fun updateState(
        riskScore: Int,
        riskTyping: Int,
        riskTapping: Int,
        riskScrolling: Int,
        tapViolations: Int,
        scrollViolations: Int,
        typeViolations: Int,
        riskLevel: String,
        description: String?,
        timestamp: Long
    ) {
        this.riskScore = riskScore
        this.riskTyping = riskTyping
        this.riskTapping = riskTapping
        this.riskScrolling = riskScrolling
        this.tapViolations = tapViolations
        this.scrollViolations = scrollViolations
        this.typeViolations = typeViolations
        this.riskLevel = riskLevel

        if (description != null) {
            val time = timeFormat.format(timestamp)
            val line = "[$time] $description"
            events.add(0, line)
            if (events.size > 50) {
                if (events.isNotEmpty()) {
                    events.removeAt(events.lastIndex)
                }
            }

        }
    }
}
