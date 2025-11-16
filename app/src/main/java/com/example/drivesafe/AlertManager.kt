package com.example.drivesafe

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat

/**
 * handles all alert notifications for the app
 * 3 levels: minor (regular), major (high priority, covers keyboard), critical (full screen, red)
 */
object AlertManager {

    // unique channel IDs for each notification type (Android requires these)
    private const val CHANNEL_ID_MINOR = "drivesafe_minor"
    private const val CHANNEL_ID_MAJOR = "drivesafe_major"
    private const val CHANNEL_ID_CRITICAL = "drivesafe_critical"

    // increments so each notification is unique and can appear simultaneously
    private var notificationId = 1000

    /**
     * Must be called once when app starts (e.g., in MainActivity or Application class)
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Minor channel - default priority, no sound
            val minorChannel = NotificationChannel(
                CHANNEL_ID_MINOR,
                "Minor Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Regular distraction warnings"
                setSound(null, null)
                enableVibration(false)
                enableLights(false)
            }

            // Major channel - high priority, shows over keyboard, with sound
            val majorChannel = NotificationChannel(
                CHANNEL_ID_MAJOR,
                "Major Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Serious distraction warnings"
                enableVibration(true)
                enableLights(true)
                lightColor = Color.YELLOW
            }

            // Critical channel - max priority, full screen intent, red theme
            val criticalChannel = NotificationChannel(
                CHANNEL_ID_CRITICAL,
                "Critical Alerts",
                NotificationManager.IMPORTANCE_HIGH // Use HIGH, not MAX (MAX was removed)
            ).apply {
                description = "Dangerous distraction level - requires immediate attention"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                enableLights(true)
                lightColor = Color.RED
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(minorChannel)
            notificationManager.createNotificationChannel(majorChannel)
            notificationManager.createNotificationChannel(criticalChannel)
        }
    }

    /**
     * Regular notification for each violation
     * Low priority, doesn't make noise, appears in notification shade
     */
    fun showMinorAlert(context: Context, message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_MINOR)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("DriveSafe")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setTimeoutAfter(3000) // auto-dismiss after 3 seconds
            .build()

        showNotification(context, notification)
    }

    /**
     * Large notification for every 3 violations or typing
     * High priority - appears as heads-up notification (covers keyboard area)
     * Makes sound and vibrates
     */
    fun showMajorAlert(context: Context, message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_MAJOR)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("DriveGuard Warning")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setTimeoutAfter(5000) // auto-dismiss after 5 seconds
            .setColor(Color.rgb(255, 165, 0)) // orange color
            .build()

        showNotification(context, notification)
    }

    /**
     * Critical full-screen notification
     * Maximum priority - shows over everything as heads-up
     * Red theme, persistent vibration
     */
    fun showCriticalAlert(context: Context, message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_CRITICAL)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("CRITICAL: Stop Using Phone")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true) // can't be swiped away easily
            .setAutoCancel(false) // user must interact to dismiss
            .setTimeoutAfter(10000) // stays for 10 seconds
            .setColor(Color.RED)
            .build()

        showNotification(context, notification)
    }

    private fun showNotification(context: Context, notification: Notification) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId++, notification)
    }
}