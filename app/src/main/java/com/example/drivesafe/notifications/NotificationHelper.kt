package com.example.drivesafe.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.drivesafe.R

object NotificationHelper {

    private const val CHANNEL_ID = "drivesafe_alerts"

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "DriveSafe Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifications for driving safety"
                    enableVibration(true)
                }
                manager.createNotificationChannel(channel)
            }
        }
    }

    fun caution(context: Context) {
        notify(context, "⚠️ Caution", "Phone use is increasing. Stay focused!")
    }

    fun critical(context: Context) {
        notify(context, "🚨 CRITICAL", "Heavy phone use detected. Look at the road NOW!")
    }

    fun safe(context: Context) {
        notify(context, "✅ Safe again", "Great job staying off your phone!")
    }

    private fun notify(context: Context, title: String, msg: String) {
        ensureChannel(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify((Math.random() * 999999).toInt(), builder.build())
    }
}
