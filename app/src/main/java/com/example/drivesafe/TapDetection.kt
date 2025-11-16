package com.example.drivesafe

import android.accessibilityservice.AccessibilityService
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import kotlin.math.ln

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

    /**
     * called every time something happens on the phone
     * @param event any movement on the phone
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val type = event.eventType    //type = type of event (tap, text typed, etc)
        val now = System.currentTimeMillis()


        //detect typing + increase risk score
        if (type == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            typeViolationCounter++
            lastViolationTime = now
            riskScoreTyping += (9 * ln(typeViolationCounter.toDouble())).toInt()
            vibrate()
            Log.d("TapService", "Typing detected - immediate risk")
            showBanner("Typing detected - please focus on the road.")
        }

        //detect tapping + increase risk score
        if (type == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            //store timestamp for this tap
            tapTimes.add(now)

            //remove taps older than 5 seconds
            tapTimes.removeAll { now - it > windowMs }

            //if 6 taps happened within 5 seconds, 1 violations
            if (tapTimes.size >= 6) {
                tapViolationCounter++
                lastViolationTime = now
                riskScoreTapping += (6 * ln(tapViolationCounter.toDouble())).toInt()
                tapTimes.clear() //clear window after violation


                //for every 3 violations, show a warning (not every single time)
                if (tapViolationCounter % 3 == 0) {
                    showBanner("Lots of tapping detected")
                }
                Log.d("TapService", "Tap violation ($tapViolationCounter")
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
                tapViolationCounter++
                lastViolationTime = now
                riskScoreScrolling += (7 * ln(scrollViolationCounter.toDouble())).toInt()
                tapTimes.clear() //clear window after violation


                //for every 3 violations, show a warning (not every single time)
                if (tapViolationCounter % 3 == 0) {
                    showBanner("Lots of tapping detected")
                }
                Log.d("TapService", "Scroll violation ($scrollViolationCounter")
            }
        }

        //weighted risk score
        riskScore = (riskScoreScrolling + riskScoreTapping + riskScoreTyping) / 3

        //check if risk score passed threshold
        if (riskScore >= ALERT_THRESHOLD) {
            Log.d(
                "TapService",
                "ALERT: High distraction detected. Please stop using phone while driving."
            )
            riskScore = 0 //OPTIONAL RESET so alert doesn't trigger constantly
        }

        val noViolationMs = now - lastViolationTime

        if (noViolationMs > 3 * 60 * 1000L){ //3 minutes without any violations
            riskScore = (riskScore * 0.95).toInt() //reduce by 5%
            lastViolationTime = now //reset timer so decay doesn't repeat immediately
            Log.d("TapService", "Risk reduced due to safe behavior -> $riskScore")
        }


        //normalize (0.0 to 1.0)
        riskScore = Math.min(riskScore, MAX_SCORE)
        val riskPercent = riskScore.toDouble() / MAX_SCORE

        //risk ranges
        when {
            riskPercent < SAFE_LIMIT -> {
                Log.d("TapService", "Risk level: SAFE ($riskScore)")
            }

            riskPercent < CAUTION_LIMIT -> {
                Log.d("TapService", "Risk level: CAUTION ($riskScore)")
            }

            else -> {
                Log.d("TapService", "Risk level: CRITICAL ($riskScore)")
                showBanner("CRITICAL RISK: heavy phone use while driving ($riskScore")
                riskScore = 0 //reset after warning
            }
        }
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












