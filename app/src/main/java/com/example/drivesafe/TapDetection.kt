package com.example.drivesafe

import android.accessibilityservice.AccessibilityService
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import kotlin.math.ln
import com.example.drivesafe.TapStateHolder
import com.example.drivesafe.MotionStateHolder
import com.example.drivesafe.notifications.NotificationHelper



//import com.example.drivesafe.AlertManager


/**
 * lets us listen to UI elements on the phone while enabled in Android -> Settings -> Accessibility
 */
class TapDetection : AccessibilityService() {

    private var riskScore = 0
    private var riskScoreTyping = 20
    private var riskScoreTapping = 20
    private var riskScoreScrolling = 20
    private val SAFE_LIMIT = 0.4
    private val CAUTION_LIMIT = 0.7
    private val MAX_SCORE = 100
    private val ALERT_THRESHOLD = 15

    //for frequency detection
    private val tapTimes = mutableListOf<Long>()
    private val scrollTimes = mutableListOf<Long>()
    private val windowMs = 5 * 1000L //5 seconds

    //violation counters
    private var tapViolationCounter = 0
    private var scrollViolationCounter = 0
    private var typeViolationCounter = 0
    private var lastViolationTime = System.currentTimeMillis()

    private var lastRiskLevel = "SAFE"


    /**
     * called every time something happens on the phone
     * @param event any movement on the phone
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val type = event.eventType    //type = type of event (tap, text typed, etc)
        val now = System.currentTimeMillis()


        // Only track risk when the car is actually moving
        if (!MotionStateHolder.isMoving) {
            // Optional: keep the UI in a known state
            TapStateHolder.updateState(
                riskScore = 0,
                riskTyping = riskScoreTyping,
                riskTapping = riskScoreTapping,
                riskScrolling = riskScoreScrolling,
                tapViolations = tapViolationCounter,
                scrollViolations = scrollViolationCounter,
                typeViolations = typeViolationCounter,
                riskLevel = "SAFE",
                description = "Vehicle not moving – monitoring paused",
                timestamp = now
            )
            return
        }

        var description: String? = null

        // Is this event coming from our own app?
        val isOurApp = event.packageName == packageName


//detect typing + increase risk score
        if (type == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            typeViolationCounter++
            lastViolationTime = now
            riskScoreTyping += (9 * ln(typeViolationCounter.toDouble())).toInt()

            // local buzz
            vibrate()

            // system notification (sound + buzz + banner)
            //NotificationHelper.showTextingAlert(this)

            Log.d("TapService", "Typing detected - immediate risk")
            showBanner("Typing detected - please focus on the road.")
            description = "Typing event (violations=$typeViolationCounter)"
        }


// detect tapping + increase risk score
// On some Compose buttons, we might see focus/accessibility events instead of pure CLICK
        if (
            type == AccessibilityEvent.TYPE_VIEW_CLICKED ||
            (isOurApp && (
                    type == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED ||
                            type == AccessibilityEvent.TYPE_VIEW_FOCUSED
                    ))
        ) {
            // store timestamp for this tap
            tapTimes.add(now)

            // remove taps older than 5 seconds
            tapTimes.removeAll { now - it > windowMs }

            // if 6 taps happened within 5 seconds, 1 violation
            if (tapTimes.size >= 2) {
                tapViolationCounter++
                lastViolationTime = now
                riskScoreTapping += (6 * ln(tapViolationCounter.toDouble())).toInt()
                tapTimes.clear() // clear window after violation

                // for every 3 violations, show a warning (not every single time)
                if (tapViolationCounter % 3 == 0) {
                    showBanner("Lots of tapping detected")
                }
                Log.d("TapService", "Tap violation ($tapViolationCounter)")
                description = "Tap violation (#$tapViolationCounter, ≥6 taps in 5s)"
            } else {
                description = "Single tap"
            }
        }


        //detect scrolling + increase risk score
        if (type == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            //store timestamp for this scroll
            scrollTimes.add(now)

            //remove scrolled older than 5 seconds
            scrollTimes.removeAll { now - it > windowMs }

            //if 6 scrolls happened within 5 seconds, 1 violation
            if (scrollTimes.size >= 6) {
                scrollViolationCounter++
                lastViolationTime = now
                riskScoreScrolling += (7 * ln(scrollViolationCounter.toDouble())).toInt()
                scrollTimes.clear() //clear window after violation


                //for every 3 violations, show a warning (not every single time)
                if (scrollViolationCounter % 3 == 0) {
                    showBanner("Lots of scrolling detected")
                }
                Log.d("TapService", "Scroll violation ($scrollViolationCounter")
                description = "Scroll violation (#$scrollViolationCounter, ≥6 scrolls in 5s)"
            } else {
                description = "Scroll event"
            }
        }


        // 1) recompute combined risk score from sub-scores
        riskScore = (riskScoreScrolling + riskScoreTapping + riskScoreTyping) / 3

// --- Cooldown: decay subscores if there was a quiet period ---
        val noViolationMs = now - lastViolationTime

// For demo: 2 seconds cooldown
        if (noViolationMs > 2 * 1000L) {   // 2 * 1000L = 2000 ms = 2 seconds
            riskScoreTyping = (riskScoreTyping * 0.95).toInt()
            riskScoreTapping = (riskScoreTapping * 0.95).toInt()
            riskScoreScrolling = (riskScoreScrolling * 0.95).toInt()
            lastViolationTime = now

            Log.d(
                "TapService",
                "Risk reduced due to safe behavior -> typing=$riskScoreTyping, tapping=$riskScoreTapping, scrolling=$riskScoreScrolling"
            )
        }

// Compute level
        val displayScore = riskScore.coerceIn(0, MAX_SCORE)
        val riskPercent = displayScore.toDouble() / MAX_SCORE

        val level = when {
            riskPercent < SAFE_LIMIT -> "SAFE"
            riskPercent < CAUTION_LIMIT -> "CAUTION"
            else -> "CRITICAL"
        }

// ----- SUPER SIMPLE NOTIFICATION SYSTEM -----

        if (level != lastRiskLevel) {
            when (level) {
                "SAFE" -> NotificationHelper.safe(this)
                "CAUTION" -> NotificationHelper.caution(this)
                "CRITICAL" -> NotificationHelper.critical(this)
            }
            lastRiskLevel = level
        }

// ----- END OF SIMPLE SYSTEM -----

// Now update UI
        TapStateHolder.updateState(
            riskScore = displayScore,
            riskTyping = riskScoreTyping,
            riskTapping = riskScoreTapping,
            riskScrolling = riskScoreScrolling,
            tapViolations = tapViolationCounter,
            scrollViolations = scrollViolationCounter,
            typeViolations = typeViolationCounter,
            riskLevel = level,
            description = description,
            timestamp = now
        )


    }



    private fun vibrate(){
        val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
        v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    private fun showBanner(message: String){
        Log.d("TapService", "BANNER:  $message")
    }

    override fun onInterrupt() {}
}











